package org.example.params;

import org.example.enums.SatelliteType;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class ImagingSatelliteParam extends SatelliteParam {
    private final double resolution;

    @JsonCreator
    public ImagingSatelliteParam(
            @JsonProperty("name") String name,
            @JsonProperty("batteryLevel") double batteryLevel,
            @JsonProperty("resolution") double resolution) {
        super(SatelliteType.IMAGE, name, batteryLevel);
        this.resolution = resolution;
    }

    public double getResolution() {
        return resolution;
    }

    @Override
    public String toString() {
        return String.format("ImagingSatelliteParam{name='%s', batteryLevel=%.2f, resolution=%.2f}",
                getName(), getBatteryLevel(), resolution);
    }
}