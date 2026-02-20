package org.example;

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
    private final List<Satellite> satellites = new ArrayList<>();

    public SatelliteConstellation(String constellationName) {
        this.constellationName = constellationName;
        log.info("✨ Создана спутниковая группировка: {}", constellationName);
    }

    public void addSatellite(Satellite satellite) {
        if (satellite != null && !satellites.contains(satellite)) {
            satellites.add(satellite);
            log.info("➕ {} добавлен в группировку '{}'", satellite.getName(), constellationName);
        }
    }

    public void executeAllMissions() {
        log.info("\n🚀 ВЫПОЛНЕНИЕ МИССИЙ ГРУППИРОВКИ {}", constellationName.toUpperCase());
        log.info("=".repeat(60));
        for (Satellite satellite : satellites) {
            satellite.performMission();
        }
        log.info("=".repeat(60));
    }

    public List<Satellite> getSatellites() {
        return new ArrayList<>(satellites);
    }
}