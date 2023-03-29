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
import java.util.Date;
import java.util.List;

public class RiderDAO extends BasicDAOImpl<IRider> implements IRiderDAO {


    public RiderDAO(EntityManager em) {
        super(Rider.class, em);
    }

    @Override
    public List<IRider> findRidersByCurrencyValueAndCurrency(BigDecimal currencyValue, String currency) {
        try {
            return this.em.createNamedQuery(Constants.Q_RIDER_BY_SPENT_AND_CURRENCY, IRider.class)
                    .setParameter("value", currencyValue)
                    .setParameter("currency", currency)
                    .getResultList();

        } catch (NoResultException e) {
            return null;
        }
    }

    @Override
    public List<IRider> findTopThreeRidersWithMostCanceledTripsAndRatingLowerEqualTwo(Date start, Date end) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<IRider> cq = cb.createQuery(IRider.class);
        Root<Rider> rider = cq.from(Rider.class);

        Join<Rider, Trip> trip = rider.join("trips", JoinType.INNER);

        //Always true predicates
        Predicate startPredicate = cb.and();
        Predicate endPredicate = cb.and();

        if (start != null) {
            startPredicate = cb.greaterThanOrEqualTo(trip.get("created"), start);
        }
        if (end != null) {
            endPredicate = cb.lessThanOrEqualTo(trip.get("created"), end);
        }

        cq.select(rider)
                .where(
                        cb.and(
                                cb.and(startPredicate, endPredicate),
                                cb.and(
                                        cb.le(rider.get("avgRating"), 2.0),
                                        cb.equal(trip.get("state"), TripState.CANCELLED)
                                )
                        )
                )
                .groupBy(rider)
                .orderBy(cb.desc(cb.count(trip.get("id"))));

        TypedQuery<IRider> q = em.createQuery(cq);

        return q.setMaxResults(3).getResultList();
    }

    @Override
    public IRider findByEmail(String email) {
        try {
            return this.em.createNamedQuery(Constants.Q_RIDER_BY_EMAIL, IRider.class)
                    .setParameter("email", email)
                    .getSingleResult();
        } catch (NoResultException e) {
            return null;
        }
    }

}
