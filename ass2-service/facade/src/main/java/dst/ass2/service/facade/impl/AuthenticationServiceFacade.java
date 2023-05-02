package dst.ass2.service.facade.impl;

import dst.ass2.service.api.auth.AuthenticationException;
import dst.ass2.service.api.auth.NoSuchUserException;
import dst.ass2.service.api.auth.rest.IAuthenticationResource;
import dst.ass2.service.auth.client.IAuthenticationClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import javax.ws.rs.Path;
import javax.ws.rs.core.Response;

@Path("/auth")
public class AuthenticationServiceFacade implements IAuthenticationResource {

    @Inject
    private IAuthenticationClient authClient;

    private static final Logger LOG = LoggerFactory.getLogger(AuthenticationServiceFacade.class);

    @Override
    public Response authenticate(String email,
                                  String password)
            throws NoSuchUserException, AuthenticationException {
        LOG.info("Authenticating user {}", email);
        var auth = authClient.authenticate(email, password);

        return Response.ok().entity(auth).build();
    }
}
