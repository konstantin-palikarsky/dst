package dst.ass2.service.auth.impl;

import dst.ass1.jpa.dao.IDAOFactory;
import dst.ass1.jpa.dao.IRiderDAO;
import dst.ass1.jpa.model.IModelFactory;
import dst.ass2.service.api.auth.AuthenticationException;
import dst.ass2.service.api.auth.NoSuchUserException;
import dst.ass2.service.auth.ICachingAuthenticationService;

import javax.annotation.PostConstruct;
import javax.ejb.Singleton;
import javax.inject.Inject;
import javax.inject.Named;
import javax.transaction.Transactional;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

@Singleton
@Named
@Transactional
public class CachingAuthenticationService implements ICachingAuthenticationService {
    @Inject
    private IDAOFactory daoFactory;

    @Inject
    private IModelFactory modelFactory;

    private Map<String, byte[]> emailToPasswordMap;
    private Map<String, String> tokenToEmailMap;

    private IRiderDAO riderRepository;
    private final ReadWriteLock passwordLock = new ReentrantReadWriteLock();


    @Override
    @PostConstruct
    public void loadData() {
        riderRepository = daoFactory.createRiderDAO();
        emailToPasswordMap = new HashMap<>();
        tokenToEmailMap = new HashMap<>();

        var riders = riderRepository.findAll();
        riders.forEach(x -> emailToPasswordMap.put(x.getEmail(), x.getPassword()));
    }

    @Override
    public String getUser(String token) {
        if (!isValid(token)) {
            return null;
        }

        return tokenToEmailMap.get(token);
    }

    @Override
    public boolean isValid(String token) {
        return tokenToEmailMap.containsKey(token);
    }

    @Override
    public boolean invalidate(String token) {
        if (!isValid(token)) {
            return false;
        }

        tokenToEmailMap.remove(token);
        return true;
    }

    @Override
    public String authenticate(String email, String password) throws NoSuchUserException, AuthenticationException {
        passwordLock.readLock().lock();
        try {
            //Update cache to hold new Rider
            if (!emailToPasswordMap.containsKey(email)) {
                var rider = riderRepository.findByEmail(email);
                if (rider == null) {
                    throw new NoSuchUserException("Trying to authenticate non-existent rider");
                }
                emailToPasswordMap.put(email, rider.getPassword());
            }

            if (!isUserAuthenticated(email, password)) {
                throw new AuthenticationException("Wrong email or password");
            }

            var uniqueTokenId = UUID.randomUUID().toString();

            tokenToEmailMap.put(uniqueTokenId, email);
            return uniqueTokenId;
        } finally {
            passwordLock.readLock().unlock();
        }

    }

    @Override
    public void changePassword(String email, String newPassword) throws NoSuchUserException {
        //DDOS protection
        var rider = riderRepository.findByEmail(email);
        if (rider == null) {
            throw new NoSuchUserException("Trying to update password of non-existent user");
        }

        passwordLock.writeLock().lock();
        try {
            var pwHash = hashPassword(newPassword);
            rider.setPassword(pwHash);
            riderRepository.save(rider);
            emailToPasswordMap.put(email, pwHash);
        } finally {
            passwordLock.writeLock().unlock();
        }

    }


    @Override
    public void clearCache() {
        emailToPasswordMap = null;
    }

    private boolean isUserAuthenticated(String email, String pwString) {
        var passwordInput = hashPassword(pwString);
        var storedPassword = emailToPasswordMap.get(email);


        return Arrays.equals(passwordInput, storedPassword);
    }

    private byte[] hashPassword(String pwString) {
        MessageDigest md = getMessageDigest();

        return md.digest(pwString.getBytes());
    }

    private MessageDigest getMessageDigest() {
        try {
            return MessageDigest.getInstance("SHA1");
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException(e);
        }
    }
}
