package org.example.services;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.aop.LogExecutionTime;
import org.example.domains.Satellite;
import org.example.domains.SatelliteConstellation;
import org.example.repository.ConstellationRepository;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class ConstellationService {

    private final ConstellationRepository repository;

    @LogExecutionTime(name = "Создание группировки")
    public void createAndSaveConstellation(String name) {
        SatelliteConstellation constellation = new SatelliteConstellation(name);
        repository.addConstellation(constellation);
        log.info("Создана группировка: {}", name);
    }

    @LogExecutionTime(name = "Добавление спутника в группировку")
    public void addSatelliteToConstellation(String constellationName, Satellite satellite) {
        SatelliteConstellation constellation = repository.getConstellation(constellationName);
        constellation.addSatellite(satellite);
        log.info("Добавлен спутник {} в {}", satellite.getName(), constellationName);
        repository.updateConstellation(constellation);
    }

    @LogExecutionTime(name = "Выполнение миссии группировки")
    public void executeConstellationMission(String constellationName) {
        SatelliteConstellation constellation = repository.getConstellation(constellationName);
        log.info("\n=== Миссия группировки: {} ===", constellationName);
        constellation.executeAllMissions();
    }

    @LogExecutionTime(name = "Активация всех спутников")
    public void activateAllSatellites(String constellationName) {
        SatelliteConstellation constellation = repository.getConstellation(constellationName);
        log.info("\n=== Активация спутников в {} ===", constellationName);
        for (Satellite satellite : constellation.getSatellites()) {
            satellite.activate();
        }
    }

    public void showConstellationStatus(String constellationName) {
        SatelliteConstellation constellation = repository.getConstellation(constellationName);
        System.out.println("\n=== СТАТУС ГРУППИРОВКИ: " + constellationName + " ===");
        System.out.println("КОЛИЧЕСТВО СПУТНИКОВ: " + constellation.getSatellites().size());
        for (Satellite satellite : constellation.getSatellites()) {
            System.out.println(" - " + satellite.getName() +
                    " [" + satellite.getState().getStatus() + "]" +
                    ", заряд: " + (int)(satellite.getEnergy().getBatteryLevel() * 100) + "%");
        }
    }

    public SatelliteConstellation getConstellation(String name) {
        return repository.getConstellation(name);
    }

    public Map<String, SatelliteConstellation> getAllConstellations() {
        return repository.getAllConstellations();
    }

    @LogExecutionTime(name = "Удаление группировки")
    public void removeConstellation(String name) {
        repository.removeConstellation(name);
    }

    @LogExecutionTime(name = "Получение системной сводки", verbose = true)
    public String getSystemOverview() {
        var allConstellations = repository.getAllConstellations();
        StringBuilder sb = new StringBuilder("\n=== СИСТЕМНАЯ СВОДКА ===\n");
        sb.append("Всего группировок: ").append(allConstellations.size()).append("\n");
        allConstellations.values().forEach(cons -> {
            sb.append("\n[группировка ").append(cons.getConstellationName())
                    .append(": спутников ").append(cons.getSatellites().size()).append("]\n");
            cons.getSatellites().forEach(sat ->
                    sb.append("  - ").append(sat.getName())
                            .append(" [").append(sat.getState().isActive() ? "Активен" : "Неактивен")
                            .append("], заряд: ").append((int)(sat.getEnergy().getBatteryLevel()*100)).append("%\n")
            );
        });
        return sb.toString();
    }
}