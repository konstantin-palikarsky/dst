package dst.ass1.jpa.model.impl;

import dst.ass1.jpa.model.*;
import dst.ass1.jpa.util.Constants;
import org.hibernate.annotations.Target;

import javax.persistence.*;
import java.util.Date;

@Entity
@NamedQuery(
        name = Constants.Q_COUNT_MATCH_BY_DATE,
        query = "SELECT COUNT(m) FROM Match m WHERE m.date=:date"
)
public class Match implements IMatch {

    @Id
    @GeneratedValue
    private Long id;

    private Date date;

    @Embedded
    @Target(Money.class)
    private IMoney fare;

    @OneToOne(targetEntity = Trip.class)
    private ITrip trip;

    @ManyToOne(targetEntity = Vehicle.class)
    private IVehicle vehicle;

    @ManyToOne(targetEntity = Driver.class)
    private IDriver driver;


    @Override
    public void setDriver(IDriver driver) {
        this.driver = driver;
    }

    @Override
    public IDriver getDriver() {
        return driver;
    }

    @Override
    public void setVehicle(IVehicle vehicle) {
        this.vehicle = vehicle;
    }

    @Override
    public IVehicle getVehicle() {
        return vehicle;
    }

    @Override
    public void setId(Long id) {
        this.id = id;
    }

    @Override
    public void setDate(Date date) {
        this.date = date;
    }

    @Override
    public void setFare(IMoney fare) {
        this.fare = fare;
    }

    @Override
    public void setTrip(ITrip trip) {
        this.trip = trip;
    }

    @Override
    public Long getId() {
        return id;
    }

    @Override
    public Date getDate() {
        return date;
    }

    @Override
    public IMoney getFare() {
        return fare;
    }

    @Override
    public ITrip getTrip() {
        return trip;
    }
}
