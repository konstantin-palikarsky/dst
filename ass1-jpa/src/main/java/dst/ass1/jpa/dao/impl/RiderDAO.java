package dst.ass1.jpa.dao.impl;

import dst.ass1.jpa.dao.IRiderDAO;
import dst.ass1.jpa.model.IRider;
import dst.ass1.jpa.model.impl.Rider;

import javax.persistence.EntityManager;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

public class RiderDAO extends FinderDaoImpl<IRider, Rider> implements IRiderDAO {

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
        return null;
    }
}
