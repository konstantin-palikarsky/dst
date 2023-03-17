package dst.ass1.jpa.model.impl;

import dst.ass1.jpa.model.IRider;
import dst.ass1.jpa.model.ITrip;
import dst.ass1.jpa.util.Constants;

import javax.persistence.*;
import java.util.Collection;

@Entity
@Table(indexes = @Index(name = "RiderIndex", columnList = Constants.M_RIDER_ACCOUNT + "," + Constants.M_RIDER_BANK_CODE))
@NamedQuery(
        name = Constants.Q_RIDER_BY_EMAIL,
        query = "SELECT r FROM Rider r WHERE r.email=:email"
)
@NamedQuery(
        name = Constants.Q_RIDER_BY_SPENT_AND_CURRENCY,
        query = "SELECT r FROM Rider r " +
                "JOIN r.trips as tr on (tr.rider.id=r.id) " +
                "WHERE tr.tripInfo.total.currency=:currency " +
                "GROUP BY r " +
                "HAVING SUM(tr.tripInfo.total.currencyValue)>:value "
)
public class Rider extends PlatformUser implements IRider {
    @OneToMany(mappedBy = "rider", targetEntity = Trip.class)
    private Collection<ITrip> trips;

    @Column(unique = true, nullable = false)
    private String email;

    @Column(columnDefinition = "VARBINARY(20)")
    private byte[] password;

    @Column(unique = true)
    private String accountNo;

    @Column(unique = true)
    private String bankCode;

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

    @Override
    public String toString() {
        return "Rider{" +
                "email='" + email + '\'' + trips.toString()+
                '}';
    }
}
