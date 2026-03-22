package org.example.observer;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.domains.Satellite;
import org.example.repository.ConstellationRepository;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class Observer {

    private final ConstellationRepository repository;

    public void observerLogic(Satellite satellite) {
        log.info("🔍 Observer logic for satellite: {}", satellite.getName());
        log.info("⚠️ Спутник {} будет деактивирован наблюдателем из-за критического состояния",
                satellite.getName());

        satellite.getState().deactivate();

        log.info("✅ Спутник {} деактивирован наблюдателем", satellite.getName());
    }

    public void checkSatelliteHealth(Satellite satellite) {
        double batteryLevel = satellite.getEnergy().getBatteryLevel();

        if (batteryLevel < 0.2) {
            log.warn("⚠️ КРИТИЧЕСКИЙ НИЗКИЙ ЗАРЯД у спутника {}: {}%",
                    satellite.getName(),
                    Math.round(batteryLevel * 100));
            observerLogic(satellite);
        } else if (batteryLevel < 0.5) {
            log.info("📊 Низкий заряд у спутника {}: {}%",
                    satellite.getName(),
                    Math.round(batteryLevel * 100));
        } else {
            log.debug("✅ Спутник {} в норме: {}%",
                    satellite.getName(),
                    Math.round(batteryLevel * 100));
        }
    }
}