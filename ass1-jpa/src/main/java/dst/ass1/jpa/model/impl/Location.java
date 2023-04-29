package dst.ass1.jpa.model.impl;

import dst.ass1.jpa.model.ILocation;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

@Entity
public class Location implements ILocation {
    @Id
    @GeneratedValue
    private Long id;
    private String name;
    private Long locationalId;

    @Override
    public Long getId() {
        return id;
    }

    @Override
    public void setId(Long id) {
        this.id = id;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public void setName(String name) {
        this.name = name;
    }

    @Override
    public Long getLocationId() {
        return locationalId;
    }

    @Override
    public void setLocationId(Long locationId) {
        this.locationalId = locationId;
    }
}
