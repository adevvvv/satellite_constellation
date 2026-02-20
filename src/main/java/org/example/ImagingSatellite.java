package org.example;

import lombok.Getter;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;

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
            takePhoto();
            energy.consume(0.08);
        } else {
            log.info("🛑 {}: Не может выполнить съемку - не активен (статус: {})",
                    name, getState().getStatus());
        }
    }

    private void takePhoto() {
        if (state.isActive()) {
            photosTaken++;
            log.info("📸 {}: Снимок #{} сделан!", name, photosTaken);
        }
    }
}