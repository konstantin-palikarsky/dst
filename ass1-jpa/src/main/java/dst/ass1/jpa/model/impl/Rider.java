package dst.ass1.jpa.model.impl;

import dst.ass1.jpa.model.IRider;
import dst.ass1.jpa.model.ITrip;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.Collection;

@Entity
public class Rider implements IRider {

    @Id
    private Long id;

    @OneToMany(mappedBy = "rider")
    private Collection<ITrip> trips;

    @NotNull
    @Column(unique = true)
    private String email;

    private byte[] password;

    private String accountNo;

    private String bankCode;

    private String name;

    @NotNull
    private String tel;

    private Double avgRating;

    @Override
    public void setTrips(Collection<ITrip> trips) {
        this.trips = trips;
    }

    @Override
    public void addTrip(ITrip trip) {
        trips.add(trip);
    }

    @Override
    public Collection<ITrip> getTrips() {
        return trips;
    }

    @Override
    public void setId(Long id) {
        this.id = id;
    }

    @Override
    public void setName(String name) {
        this.name = name;
    }

    @Override
    public void setTel(String tel) {
        this.tel = tel;
    }

    @Override
    public void setAvgRating(Double avgRating) {
        this.avgRating = avgRating;
    }

    @Override
    public Long getId() {
        return id;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String getTel() {
        return tel;
    }

    @Override
    public Double getAvgRating() {
        return avgRating;
    }

    @Override
    public String getEmail() {
        return email;
    }

    @Override
    public byte[] getPassword() {
        return password;
    }

    @Override
    public String getAccountNo() {
        return accountNo;
    }

    @Override
    public String getBankCode() {
        return bankCode;
    }

    @Override
    public void setEmail(String email) {
        this.email = email;
    }

    @Override
    public void setPassword(byte[] password) {
        this.password = password;
    }

    @Override
    public void setAccountNo(String accountNo) {
        this.accountNo = accountNo;
    }

    @Override
    public void setBankCode(String bankCode) {
        this.bankCode = bankCode;
    }
}
