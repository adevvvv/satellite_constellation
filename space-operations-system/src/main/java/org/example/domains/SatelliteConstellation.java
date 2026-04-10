package org.example.domains;

import lombok.Getter;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;
import java.util.ArrayList;
import java.util.List;

@Getter
@ToString
@Slf4j
public class SatelliteConstellation {
    private final String constellationName;
    private final List<Satellite> satellites;

    // Упрощенный конструктор
    public SatelliteConstellation(String constellationName) {
        this.constellationName = constellationName;
        this.satellites = new ArrayList<>();
        log.info("✨ Создана группировка: {}", constellationName);
    }

    public void addSatellite(Satellite satellite) {
        if (satellite != null && !satellites.contains(satellite)) {
            satellites.add(satellite);
            log.info("➕ {} добавлен в '{}'", satellite.getName(), constellationName);
        }
    }

    public void executeAllMissions() {
        log.info("\n🚀 МИССИИ ГРУППИРОВКИ {}", constellationName.toUpperCase());
        log.info("=".repeat(50));
        for (Satellite satellite : satellites) {
            satellite.performMission();
        }
    }
}