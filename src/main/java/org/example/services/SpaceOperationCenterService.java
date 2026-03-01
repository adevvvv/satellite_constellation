package org.example.services;

import org.example.domains.Satellite;
import org.example.domains.SatelliteConstellation;
import org.example.repository.ConstellationRepository;
import org.springframework.stereotype.Service;

@Service
public class SpaceOperationCenterService {
    private final ConstellationRepository repository;

    public SpaceOperationCenterService(ConstellationRepository repository) {
        this.repository = repository;
    }

    public void createAndSaveConstellation(String name) {
        SatelliteConstellation constellation = new SatelliteConstellation(name);
        repository.addConstellation(constellation);
        System.out.println("Создана группировка: " + name);
    }

    public void addSatelliteToConstellation(String constellationName, Satellite satellite) {
        SatelliteConstellation constellation = repository.getConstellation(constellationName);
        constellation.addSatellite(satellite);
        System.out.println("Добавлен спутник " + satellite.getName() + " в " + constellationName);
        repository.updateConstellation(constellation);
    }

    public void executeConstellationMission(String constellationName) {
        SatelliteConstellation constellation = repository.getConstellation(constellationName);
        System.out.println("\n=== Миссия группировки: " + constellationName + " ===");
        constellation.executeAllMissions();
    }

    public void activateAllSatellites(String constellationName) {
        SatelliteConstellation constellation = repository.getConstellation(constellationName);
        System.out.println("\n=== Активация спутников в " + constellationName + " ===");
        for (Satellite satellite : constellation.getSatellites()) {
            satellite.activate();
        }
    }

    public void showConstellationStatus(String constellationName) {
        SatelliteConstellation constellation = repository.getConstellation(constellationName);
        System.out.println("\n=== Статус группировки: " + constellationName + " ===");
        System.out.println("Спутников: " + constellation.getSatellites().size());
        for (Satellite satellite : constellation.getSatellites()) {
            System.out.println(satellite.getName() + ": " + satellite.getState().getStatus());
        }
    }
}