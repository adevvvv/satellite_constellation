package org.example.params;

import org.example.enums.SatelliteType;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class CommunicationSatelliteParam extends SatelliteParam {
    private final double bandwidth;

    @JsonCreator
    public CommunicationSatelliteParam(
            @JsonProperty("name") String name,
            @JsonProperty("batteryLevel") double batteryLevel,
            @JsonProperty("bandwidth") double bandwidth) {
        super(SatelliteType.COMMUNICATION, name, batteryLevel);
        this.bandwidth = bandwidth;
    }

    public double getBandwidth() {
        return bandwidth;
    }

    @Override
    public String toString() {
        return String.format("CommunicationSatelliteParam{name='%s', batteryLevel=%.2f, bandwidth=%.2f}",
                getName(), getBatteryLevel(), bandwidth);
    }
}