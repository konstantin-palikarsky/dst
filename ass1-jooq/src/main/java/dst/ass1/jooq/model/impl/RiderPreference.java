package dst.ass1.jooq.model.impl;

import dst.ass1.jooq.model.IRiderPreference;

import java.util.Map;

public class RiderPreference implements IRiderPreference {
    private String area;
    private String vehicleClass;
    private Long riderId;
    private Map<String, String> preferences;

    @Override
    public void setArea(String area) {
        this.area = area;
    }

    @Override
    public void setVehicleClass(String vehicleClass) {
        this.vehicleClass = vehicleClass;
    }

    @Override
    public void setRiderId(Long riderId) {
        this.riderId = riderId;
    }

    @Override
    public void setPreferences(Map<String, String> preferences) {
        this.preferences = preferences;
    }

    @Override
    public String getArea() {
        return area;
    }

    @Override
    public String getVehicleClass() {
        return vehicleClass;
    }

    @Override
    public Long getRiderId() {
        return riderId;
    }

    @Override
    public Map<String, String> getPreferences() {
        return preferences;
    }
}
