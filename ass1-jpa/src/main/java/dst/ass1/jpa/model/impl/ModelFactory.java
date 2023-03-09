package dst.ass1.jpa.model.impl;

import dst.ass1.jpa.model.*;

import java.util.LinkedList;

/**
 * Creates new instances of your model implementations.
 */
public class ModelFactory implements IModelFactory {

    @Override
    public IModelFactory createModelFactory() {


        return new ModelFactory();
    }

    @Override
    public IDriver createDriver() {
        // TODO
        var driver = new Driver();
        driver.setEmployments(new LinkedList<>());

        return driver;
    }

    @Override
    public IEmployment createEmployment() {
        // TODO

        return new Employment();
    }

    @Override
    public IEmploymentKey createEmploymentKey() {
        // TODO
        return new EmploymentKey();
    }

    @Override
    public ILocation createLocation() {
        // TODO


        return new Location();
    }

    @Override
    public IMatch createMatch() {
        // TODO
        return new Match();
    }

    @Override
    public IMoney createMoney() {
        // TODO
        return new Money();
    }

    @Override
    public IOrganization createOrganization() {
        var organization = new Organization();
        organization.setEmployments(new LinkedList<>());
        organization.setParts(new LinkedList<>());
        organization.setPartOf(new LinkedList<>());
        organization.setVehicles(new LinkedList<>());
        // TODO
        return organization;
    }

    @Override
    public IRider createRider() {
        // TODO
        var rider = new Rider();
        rider.setTrips(new LinkedList<>());

        return rider;
    }

    @Override
    public ITrip createTrip() {
        // TODO
        var trip = new Trip();
        trip.setStops(new LinkedList<>());

        return trip;
    }

    @Override
    public ITripInfo createTripInfo() {
        // TODO
        return new TripInfo();
    }

    @Override
    public IVehicle createVehicle() {
        // TODO
        return new Vehicle();
    }
}
