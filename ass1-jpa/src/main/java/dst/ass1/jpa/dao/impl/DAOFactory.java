package dst.ass1.jpa.dao.impl;

import dst.ass1.jpa.dao.*;

import javax.persistence.EntityManager;

public class DAOFactory implements IDAOFactory {

    private EntityManager em;

    public DAOFactory(EntityManager em) {
        this.em = em;
    }

    @Override
    public IDriverDAO createDriverDAO() {
        // TODO
        return new DriverDAO(em);
    }

    @Override
    public IEmploymentDAO createEmploymentDAO() {
        // TODO
        return new EmploymentDAO(em);
    }

    @Override
    public ILocationDAO createLocationDAO() {
        // TODO
        return new LocationDAO(em);
    }

    @Override
    public IMatchDAO createMatchDAO() {
        // TODO
        return new MatchDAO(em);
    }

    @Override
    public IOrganizationDAO createOrganizationDAO() {
        // TODO
        return new OrganizationDAO(em);
    }

    @Override
    public IRiderDAO createRiderDAO() {
        // TODO
        return new RiderDAO(em);
    }

    @Override
    public ITripDAO createTripDAO() {
        // TODO
        return new TripDAO(em);
    }

    @Override
    public ITripInfoDAO createTripInfoDAO() {
        // TODO
        return new TripInfoDAO(em);
    }

    @Override
    public IVehicleDAO createVehicleDAO() {
        // TODO
        return new VehicleDAO(em);
    }
}
