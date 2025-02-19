package dst.ass1.jpa.model.impl;


import dst.ass1.jpa.model.IEmployment;
import dst.ass1.jpa.model.IOrganization;
import dst.ass1.jpa.model.IVehicle;
import dst.ass1.jpa.util.Constants;

import javax.persistence.*;
import java.util.Collection;

@Entity
@NamedQuery(
        name = Constants.Q_TOP_DRIVER_OF_ORGANIZATION,
        query = "SELECT d " +
                "FROM Driver d " +
                "JOIN Match as m on (m.driver.id=d.id) " +
                "JOIN Employment as empl " +
                "ON (empl.id.organization.id=:organization_id " +
                "AND empl.id.driver.id=d.id) " +
                "WHERE m.date >= empl.since " +
                "AND empl.active = TRUE " +
                "GROUP BY d " +
                "ORDER BY SUM(m.trip.tripInfo.distance) DESC"
)
public class Organization implements IOrganization {

    @Id
    @GeneratedValue
    private Long id;

    private String name;

    @ManyToMany(targetEntity = Employment.class)
    private Collection<IEmployment> employments;

    @ManyToMany(targetEntity = Organization.class)
    @JoinTable(
            name = Constants.J_ORGANIZATION_PARTS,
            joinColumns = @JoinColumn(name = Constants.I_ORGANIZATION_PARTS),
            inverseJoinColumns = @JoinColumn(name = Constants.I_ORGANIZATION_PART_OF)
    )
    private Collection<IOrganization> parts;

    @ManyToMany(targetEntity = Organization.class, mappedBy = Constants.M_ORGANIZATION_PARTS)
    private Collection<IOrganization> partOf;

    @ManyToMany(targetEntity = Vehicle.class)
    private Collection<IVehicle> vehicles;


    @Override
    public void addPartOf(IOrganization partOf) {
        this.partOf.add(partOf);
    }

    @Override
    public Collection<IOrganization> getPartOf() {
        return partOf;
    }

    @Override
    public void setPartOf(Collection<IOrganization> partOf) {
        this.partOf = partOf;
    }

    @Override
    public void addPart(IOrganization part) {
        this.parts.add(part);
    }

    @Override
    public void addVehicle(IVehicle vehicle) {
        this.vehicles.add(vehicle);
    }

    @Override
    public void setVehicles(Collection<IVehicle> vehicles) {
        this.vehicles = vehicles;
    }

    @Override
    public Collection<IVehicle> getVehicles() {
        return vehicles;
    }

    @Override
    public void addEmployment(IEmployment employment) {
        this.employments.add(employment);
    }

    @Override
    public void setEmployments(Collection<IEmployment> employments) {
        this.employments = employments;
    }

    @Override
    public Collection<IEmployment> getEmployments() {
        return employments;
    }

    @Override
    public void setParts(Collection<IOrganization> parts) {
        this.parts = parts;
    }

    @Override
    public Collection<IOrganization> getParts() {
        return parts;
    }

    @Override
    public void setId(Long id) {
        this.id = id;
    }

    @Override
    public void setName(String name) {
        this.name = name;
    }

    @Override
    public Long getId() {
        return id;
    }

    @Override
    public String getName() {
        return name;
    }
}
