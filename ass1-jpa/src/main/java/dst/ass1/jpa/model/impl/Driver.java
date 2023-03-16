package dst.ass1.jpa.model.impl;

import dst.ass1.jpa.model.IDriver;
import dst.ass1.jpa.model.IEmployment;
import dst.ass1.jpa.model.IVehicle;
import dst.ass1.jpa.util.Constants;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.Collection;

@Entity
@NamedQuery(
        name = Constants.Q_TOP_DRIVER_OF_ORGANIZATION,
        query = "SELECT d FROM Driver d " +
                "JOIN d.employments as e on (e.active=true) " +
                "JOIN e.id as i " +
                "JOIN i.organization as o " +
                "WHERE o.id=:organization_id"
)
public class Driver extends PlatformUser implements IDriver {

    @ManyToMany(fetch = FetchType.LAZY, targetEntity = Employment.class)
    private Collection<IEmployment> employments;

    @ManyToOne(targetEntity = Vehicle.class, optional = false)
    @NotNull
    private IVehicle vehicle;

    @Override
    public void addEmployment(IEmployment employment) {
        this.employments.add(employment);
    }

    @Override
    public void setEmployments(Collection<IEmployment> employments) {
        this.employments = employments;
    }

    @Override
    public void setVehicle(IVehicle vehicle) {
        this.vehicle = vehicle;
    }

    @Override
    public Collection<IEmployment> getEmployments() {
        return employments;
    }

    @Override
    public IVehicle getVehicle() {
        return vehicle;
    }
}
