package org.example.observer;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.domains.Satellite;
import org.example.domains.SatelliteConstellation;
import org.example.repository.SatelliteConstellationRepository;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class Observer {

    private final SatelliteConstellationRepository constellationRepository;

    public void observerLogic(Satellite satellite) {
        log.info("👁️ Наблюдение за спутником: {}", satellite.getName());
        log.info("   Статус: {}", satellite.getState().getStatus());
        log.info("   Заряд: {}%", (int)(satellite.getBatteryLevel() * 100));
    }

    public void checkSatelliteHealth(Satellite satellite) {
        if (!satellite.hasSufficientPower()) {
            log.warn("⚠️ Низкий заряд спутника: {}", satellite.getName());
        }
        if (!satellite.isActive()) {
            log.info("💤 Спутник неактивен: {}", satellite.getName());
        }
    }
}