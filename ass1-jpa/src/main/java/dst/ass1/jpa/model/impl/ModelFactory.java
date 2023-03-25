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
        var driver = new Driver();
        driver.setEmployments(new LinkedList<>());

        return driver;
    }

    @Override
    public IEmployment createEmployment() {

        return new Employment();
    }

    @Override
    public IEmploymentKey createEmploymentKey() {
        return new EmploymentKey();
    }

    @Override
    public ILocation createLocation() {


        return new Location();
    }

    @Override
    public IMatch createMatch() {
        return new Match();
    }

    @Override
    public IMoney createMoney() {
        return new Money();
    }

    @Override
    public IOrganization createOrganization() {
        var organization = new Organization();
        organization.setEmployments(new LinkedList<>());
        organization.setParts(new LinkedList<>());
        organization.setPartOf(new LinkedList<>());
        organization.setVehicles(new LinkedList<>());
        return organization;
    }

    @Override
    public IRider createRider() {
        var rider = new Rider();
        rider.setTrips(new LinkedList<>());

        return rider;
    }

    @Override
    public ITrip createTrip() {
        var trip = new Trip();
        trip.setStops(new LinkedList<>());
        trip.setDestination(new Location());

        return trip;
    }

    @Override
    public ITripInfo createTripInfo() {
        return new TripInfo();
    }

    @Override
    public IVehicle createVehicle() {
        return new Vehicle();
    }
}
