package org.example;

import org.springframework.stereotype.Service;
import java.util.Map;

@Service
public class SpaceOperationCenterService {
    private final ConstellationRepository repository;

    public SpaceOperationCenterService(ConstellationRepository repository) {
        this.repository = repository;
    }

    public void createAndSaveConstellation(String name) {
        SatelliteConstellation constellation = new SatelliteConstellation(name);
        repository.addConstellation(constellation);
        System.out.println("Создана и сохранена группировка: " + name);
    }

    public void addSatelliteToConstellation(String constellationName, Satellite satellite) {
        SatelliteConstellation constellation = repository.getConstellation(constellationName);
        constellation.addSatellite(satellite);
        System.out.println("Добавлен спутник " + satellite.getName() +
                " в группировку " + constellationName);
        // Обновляем группировку в репозитории
        repository.updateConstellation(constellation);
    }

    public void executeConstellationMission(String constellationName) {
        SatelliteConstellation constellation = repository.getConstellation(constellationName);
        System.out.println("\n=== Выполнение миссии для группировки: " + constellationName + " ===");
        constellation.executeAllMissions();
        // Обновляем группировку после выполнения миссий
        repository.updateConstellation(constellation);
    }

    public void activateAllSatellites(String constellationName) {
        SatelliteConstellation constellation = repository.getConstellation(constellationName);
        System.out.println("\n=== АКТИВАЦИЯ СПУТНИКОВ В ГРУППИРОВКЕ: " + constellationName + " ===");

        for (Satellite satellite : constellation.getSatellites()) {
            satellite.activate();
        }
        // Обновляем группировку после активации
        repository.updateConstellation(constellation);
    }

    public void showConstellationStatus(String constellationName) {
        SatelliteConstellation constellation = repository.getConstellation(constellationName);
        System.out.println("\n=== СТАТУС ГРУППИРОВКИ: " + constellationName + " ====");
        System.out.println("Количество спутников: " + constellation.getSatellites().size());

        for (Satellite satellite : constellation.getSatellites()) {
            System.out.println(satellite.getState());
        }
    }

    public void showAllConstellations() {
        System.out.println("\n=== ВСЕ ГРУППИРОВКИ В РЕПОЗИТОРИИ ===");
        Map<String, SatelliteConstellation> allConstellations = repository.getAllConstellations();
        for (Map.Entry<String, SatelliteConstellation> entry : allConstellations.entrySet()) {
            System.out.println(entry.getKey() + ": " + entry.getValue());
        }
    }
}