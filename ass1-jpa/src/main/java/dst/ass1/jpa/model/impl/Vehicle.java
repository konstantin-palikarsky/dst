package dst.ass1.jpa.model.impl;


import dst.ass1.jpa.model.IVehicle;
import javax.persistence.*;


@Entity
public class Vehicle implements IVehicle {

    @Id
    private Long id;

    @Column(unique = true)
    private String license;

    private String color;

    private String type;


    @Override
    public void setId(Long id) {
        this.id = id;
    }

    @Override
    public void setLicense(String license) {
        this.license = license;
    }

    @Override
    public void setColor(String color) {
        this.color = color;
    }

    @Override
    public void setType(String type) {
        this.type = type;
    }


    @Override
    public Long getId() {
        return id;
    }

    @Override
    public String getLicense() {
        return license;
    }

    @Override
    public String getColor() {
        return color;
    }

    @Override
    public String getType() {
        return type;
    }


}
