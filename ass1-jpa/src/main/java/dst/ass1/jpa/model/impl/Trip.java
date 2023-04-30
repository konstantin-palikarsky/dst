package dst.ass1.jpa.model.impl;

import dst.ass1.jpa.model.*;
import dst.ass1.jpa.util.Constants;

import javax.persistence.NamedQuery;
import java.util.Collection;
import java.util.Date;

/**
 * JPA Implementation in XML
 */
@NamedQuery(
        name = Constants.Q_LAST_TRIP_OF_DRIVER,
        query = "SELECT t FROM Trip t " +
                "WHERE t.match.driver.id=:driver_id " +
                "ORDER BY t.match.date DESC"
)
public class Trip implements ITrip {
    private Long id;
    private Date created;
    private Date updated;
    private TripState state;
    private ILocation pickup;
    private ILocation destination;
    private IRider rider;
    private IMatch match;
    private ITripInfo tripInfo;
    private Collection<ILocation> stops;

    @Override
    public Long getId() {
        return id;
    }

    @Override
    public Date getCreated() {
        return created;
    }

    @Override
    public Date getUpdated() {
        return updated;
    }

    @Override
    public TripState getState() {
        return state;
    }

    @Override
    public ILocation getPickup() {
        return pickup;
    }

    @Override
    public ILocation getDestination() {
        return destination;
    }

    @Override
    public IRider getRider() {
        return rider;
    }

    @Override
    public IMatch getMatch() {
        return match;
    }

    @Override
    public ITripInfo getTripInfo() {
        return tripInfo;
    }

    @Override
    public Collection<ILocation> getStops() {
        return stops;
    }

    @Override
    public void setId(Long id) {
        this.id = id;
    }

    @Override
    public void setCreated(Date created) {
        this.created = created;
    }

    @Override
    public void setUpdated(Date updated) {
        this.updated = updated;
    }

    @Override
    public void setState(TripState state) {
        this.state = state;
    }

    @Override
    public void setPickup(ILocation pickup) {
        this.pickup = pickup;
    }

    @Override
    public void setDestination(ILocation destination) {
        this.destination = destination;
    }

    @Override
    public void addStop(ILocation stop) {
        this.stops.add(stop);
    }

    @Override
    public void setRider(IRider rider) {
        this.rider = rider;
    }

    @Override
    public void setMatch(IMatch match) {
        this.match = match;
    }

    @Override
    public void setTripInfo(ITripInfo tripInfo) {
        this.tripInfo = tripInfo;
    }

    @Override
    public void setStops(Collection<ILocation> stops) {
        this.stops = stops;
    }

    @Override
    public String toString() {
        return "Trip{" +
                "id=" + id +
                ", tripInfo=" + tripInfo +
                ", state=" + state +

                '}';
    }
}
