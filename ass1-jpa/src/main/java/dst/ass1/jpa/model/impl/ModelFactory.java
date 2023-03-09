package dst.ass1.jpa.model.impl;

import dst.ass1.jpa.model.*;

/**
 * Creates new instances of your model implementations.
 */
public class ModelFactory implements IModelFactory {

    @Override
    public IModelFactory createModelFactory() {
        // TODO
        return new ModelFactory();
    }

    @Override
    public IDriver createDriver() {
        // TODO
        return new Driver();
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
        // TODO
        return new Organization();
    }

    @Override
    public IRider createRider() {
        // TODO
        return new Rider();
    }

    @Override
    public ITrip createTrip() {
        // TODO
        return new Trip();
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
