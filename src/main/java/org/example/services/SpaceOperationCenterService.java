// org.example.services/SpaceOperationCenterService.java
package org.example.services;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.aop.LogExecutionTime;
import org.example.domains.CommunicationSatellite;
import org.example.domains.ImagingSatellite;
import org.example.domains.Satellite;
import org.example.domains.SatelliteConstellation;
import org.example.enums.SatelliteType;
import org.example.observer.Observer;
import org.example.params.SatelliteParam;
import org.example.requests.AddSatelliteRequest;
import org.example.requests.MissionRequest;
import org.example.requests.MissionRequestWithType;
import org.springframework.stereotype.Service;

import java.util.Set;

@Service
@RequiredArgsConstructor
@Slf4j
public class SpaceOperationCenterService {

    private final ConstellationService constellationService;
    private final SatelliteService satelliteService;
    private final Observer observer;

    /**
     * Создание группировки (делегирование)
     */
    @LogExecutionTime(name = "Создание группировки через фасад")
    public void createAndSaveConstellation(String name) {
        constellationService.createAndSaveConstellation(name);
    }

    /**
     * Добавляет спутники в указанные группировки.
     * Если группировка не существует, она будет создана автоматически.
     */
    @LogExecutionTime(name = "addSatellite", verbose = true)
    public void addSatellite(AddSatelliteRequest request) {
        log.info("📡 Начало добавления спутников в группировку: {}", request.constellationName());

        try {
            // Проверяем существование группировки, если нет - создаем
            try {
                constellationService.showConstellationStatus(request.constellationName());
                log.info("Группировка {} существует", request.constellationName());
            } catch (Exception e) {
                log.info("Группировка {} не найдена, создаем новую", request.constellationName());
                constellationService.createAndSaveConstellation(request.constellationName());
            }

            // Создаем и добавляем каждый спутник
            for (SatelliteParam param : request.satelliteParams()) {
                Satellite satellite = satelliteService.createSatellite(param);
                constellationService.addSatelliteToConstellation(request.constellationName(), satellite);
                log.info("✅ Спутник {} успешно добавлен", satellite.getName());

                // Проверка здоровья спутника через Observer
                observer.checkSatelliteHealth(satellite);
            }

            log.info("✅ Все спутники успешно добавлены в группировку {}", request.constellationName());
        } catch (Exception e) {
            log.error("❌ Ошибка при добавлении спутников: {}", e.getMessage());
            throw new RuntimeException("Не удалось добавить спутники: " + e.getMessage(), e);
        }
    }

    /**
     * Выполнение миссии с расширенными параметрами
     */
    @LogExecutionTime(name = "executeMission", verbose = true)
    public void executeMission(MissionRequestWithType request) {
        log.info("🚀 Начало выполнения миссии типа: {}", request.targetType());

        switch (request.targetType()) {
            case CONSTELLATION -> executeConstellationMission(request);
            case SINGLE_SATELLITE -> executeSingleSatelliteMission(request);
            case ALL_CONSTELLATIONS -> executeAllConstellationsMissions(request);
        }
    }

    /**
     * Выполнение миссии (совместимость со старым API)
     */
    @LogExecutionTime(name = "executeMission (legacy)")
    public void executeMission(MissionRequest request) {
        log.info("🚀 Выполнение миссии для группировок: {}", request.constellationNames());

        for (String constellationName : request.constellationNames()) {
            try {
                if (request.activateBeforeMission()) {
                    constellationService.activateAllSatellites(constellationName);
                }

                SatelliteConstellation constellation = constellationService.getConstellation(constellationName);

                if (request.satelliteTypes() == null || request.satelliteTypes().isEmpty()) {
                    constellation.executeAllMissions();
                } else {
                    executeFilteredMissions(constellation, request.satelliteTypes());
                }

                log.info("✅ Миссия выполнена для группировки: {}", constellationName);
            } catch (Exception e) {
                log.error("❌ Ошибка для группировки {}: {}", constellationName, e.getMessage());
            }
        }
    }

    /**
     * Получение статистики по группировке
     */
    @LogExecutionTime(name = "Получение статистики")
    public ConstellationStatistics getConstellationStatistics(String constellationName) {
        SatelliteConstellation constellation = constellationService.getConstellation(constellationName);

        long activeSatellites = constellation.getSatellites().stream()
                .filter(s -> s.getState().isActive())
                .count();

        double avgBatteryLevel = constellation.getSatellites().stream()
                .mapToDouble(s -> s.getEnergy().getBatteryLevel())
                .average()
                .orElse(0.0);

        long lowBatteryCount = constellation.getSatellites().stream()
                .filter(s -> s.getEnergy().getBatteryLevel() < 0.2)
                .count();

        return new ConstellationStatistics(
                constellationName,
                constellation.getSatellites().size(),
                activeSatellites,
                avgBatteryLevel,
                lowBatteryCount
        );
    }

    /**
     * Деактивация всех спутников в группировке
     */
    @LogExecutionTime(name = "Деактивация всех спутников")
    public void deactivateAllSatellites(String constellationName) {
        SatelliteConstellation constellation = constellationService.getConstellation(constellationName);
        log.info("\n=== Деактивация спутников в {} ===", constellationName);

        for (Satellite satellite : constellation.getSatellites()) {
            satellite.deactivate();
        }
    }

    /**
     * Активация всех спутников в группировке
     */
    @LogExecutionTime(name = "Активация всех спутников через фасад")
    public void activateAllSatellites(String constellationName) {
        constellationService.activateAllSatellites(constellationName);
    }

    /**
     * Показ статуса группировки
     */
    public void showConstellationStatus(String constellationName) {
        constellationService.showConstellationStatus(constellationName);
    }

    /**
     * Получение системной сводки
     */
    public String getSystemOverview() {
        return constellationService.getSystemOverview();
    }

    /**
     * Выполнение миссии для группировки с фильтрацией
     */
    private void executeConstellationMission(MissionRequestWithType request) {
        SatelliteConstellation constellation = constellationService.getConstellation(request.constellationName());

        // Активируем все спутники перед миссией
        constellationService.activateAllSatellites(request.constellationName());

        if (request.targetTypes() == null || request.targetTypes().isEmpty()) {
            constellation.executeAllMissions();
        } else {
            executeFilteredMissions(constellation, request.targetTypes());
        }
    }

    /**
     * Выполнение миссии для одного спутника
     */
    private void executeSingleSatelliteMission(MissionRequestWithType request) {
        SatelliteConstellation constellation = constellationService.getConstellation(request.constellationName());

        Satellite satellite = constellation.getSatellites().stream()
                .filter(s -> s.getName().equals(request.satelliteName()))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Спутник не найден: " + request.satelliteName()));

        satellite.activate();
        satellite.performMission();

        // Проверка здоровья спутника после миссии
        observer.checkSatelliteHealth(satellite);
    }

    /**
     * Выполнение миссий для всех группировок
     */
    private void executeAllConstellationsMissions(MissionRequestWithType request) {
        var allConstellations = constellationService.getAllConstellations();

        for (SatelliteConstellation constellation : allConstellations.values()) {
            constellationService.activateAllSatellites(constellation.getConstellationName());

            if (request.targetTypes() == null || request.targetTypes().isEmpty()) {
                constellation.executeAllMissions();
            } else {
                executeFilteredMissions(constellation, request.targetTypes());
            }
        }
    }

    /**
     * Выполнение миссий только для определенных типов спутников
     */
    private void executeFilteredMissions(SatelliteConstellation constellation, Set<SatelliteType> targetTypes) {
        constellation.getSatellites().stream()
                .filter(satellite -> {
                    if (targetTypes.contains(SatelliteType.COMMUNICATION) &&
                            satellite instanceof CommunicationSatellite) {
                        return true;
                    }
                    if (targetTypes.contains(SatelliteType.IMAGE) &&
                            satellite instanceof ImagingSatellite) {
                        return true;
                    }
                    return false;
                })
                .forEach(satellite -> {
                    satellite.performMission();
                    observer.checkSatelliteHealth(satellite);
                });
    }

    /**
     * Класс для статистики группировки
     */
    public record ConstellationStatistics(
            String name,
            long totalSatellites,
            long activeSatellites,
            double avgBatteryLevel,
            long lowBatteryCount
    ) {
        @Override
        public String toString() {
            return String.format("Группировка: %s | Всего: %d | Активных: %d | Низкий заряд: %d | Средний заряд: %.2f%%",
                    name, totalSatellites, activeSatellites, lowBatteryCount, avgBatteryLevel * 100);
        }
    }
}