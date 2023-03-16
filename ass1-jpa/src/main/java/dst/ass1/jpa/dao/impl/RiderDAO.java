package dst.ass1.jpa.dao.impl;

import dst.ass1.jpa.dao.IRiderDAO;
import dst.ass1.jpa.model.IRider;
import dst.ass1.jpa.model.TripState;
import dst.ass1.jpa.model.impl.Rider;
import dst.ass1.jpa.model.impl.Trip;
import dst.ass1.jpa.util.Constants;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.*;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class RiderDAO extends BasicDAOImpl<IRider> implements IRiderDAO {


    public RiderDAO(EntityManager em) {
        super(Rider.class, em);
    }

    @Override
    public List<IRider> findRidersByCurrencyValueAndCurrency(BigDecimal currencyValue, String currency) {
        try {
            var riderList = this.em.createNamedQuery(Constants.Q_RIDER_BY_SPENT_AND_CURRENCY)
                    .setParameter("value", currencyValue)
                    .setParameter("currency", currency)
                    .getResultList();

            //System.err.println(riderList.size());

            return riderList;
        } catch (NoResultException e) {
            return null;
        }
    }

    @Override
    public List<IRider> findTopThreeRidersWithMostCanceledTripsAndRatingLowerEqualTwo(Date start, Date end) {
        CriteriaBuilder cb = em.getCriteriaBuilder();

        CriteriaQuery<Rider> cq = cb.createQuery(Rider.class);
        Root<Rider> rider = cq.from(Rider.class);

/*
        Join<Rider, Trip> trip = rider.join("trips",JoinType.INNER);

        Predicate predicate = null;

        if (start == null && end == null) {
            System.err.println("Should have right ");
        } else if (start == null) {
            predicate = cb.lessThanOrEqualTo(trip.get("created"), end);
        } else if (end == null) {
            predicate = cb.greaterThanOrEqualTo(trip.get("created"), start);

        } else {
            predicate = cb.between(trip.get("created"), start, end);
        }

        cb.and(predicate);
*/

        cq.select(rider).where(cb.le(rider.get("avgRating"), 2.0));

        TypedQuery<Rider> q = em.createQuery(cq);


        return new ArrayList<>(q.setMaxResults(3).getResultList());
    }

    @Override
    public IRider findByEmail(String email) {

        try {
            return (IRider) this.em.createNamedQuery(Constants.Q_RIDER_BY_EMAIL)
                    .setParameter("email", email).getSingleResult();
        } catch (NoResultException e) {
            return null;
        }

    }
}
