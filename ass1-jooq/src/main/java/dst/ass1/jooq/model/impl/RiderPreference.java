package dst.ass1.jooq.model.impl;

import dst.ass1.jooq.model.IRiderPreference;

import java.util.Map;
import java.util.Objects;

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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        RiderPreference that = (RiderPreference) o;

        if (!Objects.equals(area, that.area)) return false;
        if (!Objects.equals(vehicleClass, that.vehicleClass)) return false;
        if (!Objects.equals(riderId, that.riderId)) return false;
        return Objects.equals(preferences, that.preferences);
    }

    @Override
    public int hashCode() {
        int result = area != null ? area.hashCode() : 0;
        result = 31 * result + (vehicleClass != null ? vehicleClass.hashCode() : 0);
        result = 31 * result + (riderId != null ? riderId.hashCode() : 0);
        result = 31 * result + (preferences != null ? preferences.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "RiderPreference{" +
                "area='" + area + '\'' +
                ", vehicleClass='" + vehicleClass + '\'' +
                ", riderId=" + riderId +
                ", preferences=" + preferences +
                '}';
    }
}
