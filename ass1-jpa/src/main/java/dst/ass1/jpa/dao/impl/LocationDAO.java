package dst.ass1.jpa.dao.impl;

import dst.ass1.jpa.dao.ILocationDAO;
import dst.ass1.jpa.model.ILocation;
import dst.ass1.jpa.model.impl.Location;

import javax.persistence.EntityManager;

public class LocationDAO extends FinderDaoImpl<ILocation, Location> implements ILocationDAO {
    public LocationDAO(EntityManager em) {
        super(Location.class, em);
    }
}
