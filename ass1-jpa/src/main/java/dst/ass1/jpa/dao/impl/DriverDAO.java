package dst.ass1.jpa.dao.impl;

import dst.ass1.jpa.dao.IDriverDAO;
import dst.ass1.jpa.model.IDriver;
import dst.ass1.jpa.model.impl.Driver;

import javax.persistence.EntityManager;

public class DriverDAO extends FinderDaoImpl<IDriver, Driver> implements IDriverDAO {
    public DriverDAO(EntityManager em) {
        super(Driver.class, em);
    }
}
