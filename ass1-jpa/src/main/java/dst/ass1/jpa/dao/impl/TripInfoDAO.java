package dst.ass1.jpa.dao.impl;

import dst.ass1.jpa.dao.ITripInfoDAO;
import dst.ass1.jpa.model.ITripInfo;
import dst.ass1.jpa.model.impl.TripInfo;

import javax.persistence.EntityManager;

public class TripInfoDAO extends FinderDaoImpl<ITripInfo> implements ITripInfoDAO {
    public TripInfoDAO(EntityManager em) {
        super(TripInfo.class, em);
    }
}
