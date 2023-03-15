package dst.ass1.jpa.dao.impl;

import dst.ass1.jpa.dao.IRiderDAO;
import dst.ass1.jpa.model.IRider;
import dst.ass1.jpa.model.impl.Rider;
import dst.ass1.jpa.util.Constants;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

public class RiderDAO extends FinderDaoImpl<IRider> implements IRiderDAO {


    public RiderDAO(EntityManager em) {
        super(Rider.class, em);
    }

    @Override
    public List<IRider> findRidersByCurrencyValueAndCurrency(BigDecimal currencyValue, String currency) {
        return null;
    }

    @Override
    public List<IRider> findTopThreeRidersWithMostCanceledTripsAndRatingLowerEqualTwo(Date start, Date end) {
        return null;
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
