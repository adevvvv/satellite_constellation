package org.example.domains;

import lombok.Getter;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;
import org.example.constants.SatelliteConstants;

@Getter
@ToString(callSuper = true)
@Slf4j
public class CommunicationSatellite extends Satellite {
    private final double bandwidth;

    public CommunicationSatellite(String name, double batteryLevel, double bandwidth) {
        super(name, batteryLevel);
        this.bandwidth = bandwidth;
    }

    @Override
    public void performMission() {
        if (state.isActive()) {
            log.info("📡 {}: Передача данных со скоростью {} Мбит/с", name, bandwidth);
            energy.consume(SatelliteConstants.COMMUNICATION_ENERGY_CONSUMPTION);
        } else {
            log.info("🛑 {}: Не может выполнить миссию - не активен", name);
        }
    }
}