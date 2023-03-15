package dst.ass1.jpa.dao.impl;

import dst.ass1.jpa.dao.ITripDAO;
import dst.ass1.jpa.model.ITrip;
import dst.ass1.jpa.model.impl.Trip;

import javax.persistence.EntityManager;

public class TripDAO extends FinderDaoImpl<ITrip, Trip> implements ITripDAO {
    public TripDAO(EntityManager em) {
        super(Trip.class, em);
    }
}
