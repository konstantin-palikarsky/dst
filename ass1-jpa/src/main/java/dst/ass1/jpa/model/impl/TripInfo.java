package dst.ass1.jpa.model.impl;

import dst.ass1.jpa.model.IMoney;
import dst.ass1.jpa.model.ITrip;
import dst.ass1.jpa.model.ITripInfo;
import org.hibernate.annotations.Target;

import javax.persistence.*;
import java.util.Date;

@Entity
public class TripInfo implements ITripInfo {

    @Id
    @GeneratedValue
    private Long id;

    private Date completed;

    private Double distance;

    @Embedded
    @Target(Money.class)
    private IMoney total;

    private Integer driverRating;

    private Integer riderRating;

    @OneToOne(targetEntity = Trip.class, optional = false)
    private ITrip trip;

    @Override
    public void setTrip(ITrip trip) {
        this.trip = trip;
    }

    @Override
    public ITrip getTrip() {
        return trip;
    }

    @Override
    public void setId(Long id) {
        this.id = id;
    }

    @Override
    public void setCompleted(Date completed) {
        this.completed = completed;
    }

    @Override
    public void setDistance(Double distance) {
        this.distance = distance;
    }

    @Override
    public void setTotal(IMoney total) {
        this.total = total;
    }

    @Override
    public void setDriverRating(Integer driverRating) {
        this.driverRating = driverRating;
    }

    @Override
    public void setRiderRating(Integer riderRating) {
        this.riderRating = riderRating;
    }

    @Override
    public Long getId() {
        return id;
    }

    @Override
    public Date getCompleted() {
        return completed;
    }

    @Override
    public Double getDistance() {
        return distance;
    }

    @Override
    public IMoney getTotal() {
        return total;
    }

    @Override
    public Integer getDriverRating() {
        return driverRating;
    }

    @Override
    public Integer getRiderRating() {
        return riderRating;
    }
}
