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
        return new DriverDAO();
    }

    @Override
    public IEmploymentDAO createEmploymentDAO() {
        // TODO
        return new EmploymentDAO();
    }

    @Override
    public ILocationDAO createLocationDAO() {
        // TODO
        return new LocationDAO();
    }

    @Override
    public IMatchDAO createMatchDAO() {
        // TODO
        return new MatchDAO();
    }

    @Override
    public IOrganizationDAO createOrganizationDAO() {
        // TODO
        return new OrganizationDAO();
    }

    @Override
    public IRiderDAO createRiderDAO() {
        // TODO
        return new RiderDAO();
    }

    @Override
    public ITripDAO createTripDAO() {
        // TODO
        return new TripDAO();
    }

    @Override
    public ITripInfoDAO createTripInfoDAO() {
        // TODO
        return new TripInfoDAO();
    }

    @Override
    public IVehicleDAO createVehicleDAO() {
        // TODO
        return new VehicleDAO();
    }
}
