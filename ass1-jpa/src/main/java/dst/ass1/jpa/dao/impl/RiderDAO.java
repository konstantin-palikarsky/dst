package dst.ass1.jpa.dao.impl;

import dst.ass1.jpa.dao.IRiderDAO;
import dst.ass1.jpa.model.IRider;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

public class RiderDAO implements IRiderDAO {
    @Override
    public IRider findById(Long id) {
        return null;
    }

    @Override
    public List<IRider> findAll() {
        return null;
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
