package dst.ass1.jpa.dao.impl;

import dst.ass1.jpa.dao.ITripDAO;
import dst.ass1.jpa.model.ITrip;
import dst.ass1.jpa.model.impl.Trip;
import dst.ass1.jpa.util.Constants;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;

public class TripDAO extends BasicDAOImpl<ITrip> implements ITripDAO {
    public TripDAO(EntityManager em) {
        super(Trip.class, em);
    }

    @Override
    public ITrip getLastTripOfDriver(Long driverId) {

        try {
            return  this.em.createNamedQuery(Constants.Q_LAST_TRIP_OF_DRIVER, ITrip.class)
                    .setMaxResults(1)
                    .setParameter("driver_id", driverId)
                    .getSingleResult();

         } catch (NoResultException e) {
            return null;
        }
    }
}
