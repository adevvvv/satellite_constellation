package org.example.domains;

import lombok.Getter;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;
import org.example.constants.SatelliteConstants;

@Getter
@ToString(callSuper = true)
@Slf4j
public class ImagingSatellite extends Satellite {
    private final double resolution;
    private int photosTaken;

    public ImagingSatellite(String name, double batteryLevel, double resolution) {
        super(name, batteryLevel);
        this.resolution = resolution;
        this.photosTaken = 0;
    }

    @Override
    public void performMission() {
        if (state.isActive()) {
            log.info("🛰️ {}: Съемка территории с разрешением {} м/пиксель", name, resolution);
            photosTaken++;
            log.info("📸 {}: Снимок #{} сделан!", name, photosTaken);
            energy.consume(SatelliteConstants.IMAGING_ENERGY_CONSUMPTION);
        } else {
            log.info("🛑 {}: Не может выполнить съемку - не активен", name);
        }
    }
}