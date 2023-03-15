package dst.ass1.jpa.dao.impl;

import dst.ass1.jpa.dao.IVehicleDAO;
import dst.ass1.jpa.model.IVehicle;
import dst.ass1.jpa.model.impl.Vehicle;

import javax.persistence.EntityManager;

public class VehicleDAO extends FinderDaoImpl<IVehicle, Vehicle> implements IVehicleDAO {
    public VehicleDAO(EntityManager em) {
        super(Vehicle.class, em);
    }
}
