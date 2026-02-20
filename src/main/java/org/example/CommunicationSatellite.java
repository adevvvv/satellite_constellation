package org.example;

import lombok.Getter;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;

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
            sendData(bandwidth);
            energy.consume(0.05);
        } else {
            log.info("🛑 {}: Не может выполнить миссию - не активен (статус: {})",
                    name, state.getStatus());
        }
    }

    private void sendData(double dataAmount) {
        log.info("📤 {}: Отправил {} Мбит данных!", name, dataAmount);
    }
}