package dst.ass1.jpa.model.impl;

import dst.ass1.jpa.model.IDriver;
import dst.ass1.jpa.model.IEmployment;
import dst.ass1.jpa.model.IVehicle;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.validation.constraints.NotNull;
import java.util.Collection;

@Entity
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
