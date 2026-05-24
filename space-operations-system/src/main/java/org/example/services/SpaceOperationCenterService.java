package org.example.services;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.domains.*;
import org.example.requests.AddSatelliteRequest;
import org.example.requests.MissionRequestWithType;
import org.example.params.SatelliteParam;
import org.example.repository.SatelliteRepository;
import org.example.factory.SatelliteFactory;
import org.example.services.SatelliteEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class SpaceOperationCenterService {

    private final ConstellationService constellationService;
    private final List<SatelliteFactory> satelliteFactories;
    private final SatelliteRepository satelliteRepository;
    private final SatelliteEventPublisher eventPublisher;

    public void addSatellite(AddSatelliteRequest request) {
        log.info("🛰️ Добавление спутников в группировку: {}", request.constellationName());

        SatelliteConstellation constellation = constellationService.getConstellation(request.constellationName());

        for (SatelliteParam param : request.satelliteParams()) {
            Satellite satellite = createSatelliteFromParam(param);
            constellation.addSatellite(satellite);

            // ✅ Сохраняем спутник, чтобы получить ID
            Satellite savedSatellite = satelliteRepository.save(satellite);

            // ✅ ОТПРАВКА СОБЫТИЯ В KAFKA
            eventPublisher.publishSatelliteCreated(
                    savedSatellite.getId(),
                    savedSatellite.getName(),
                    constellation.getConstellationName()
            );

            log.info("✅ Спутник {} успешно добавлен в группировку {}",
                    savedSatellite.getName(), constellation.getConstellationName());
        }

        constellationService.updateConstellation(constellation.getId(), constellation);
    }

    private Satellite createSatelliteFromParam(SatelliteParam param) {
        for (SatelliteFactory factory : satelliteFactories) {
            try {
                Satellite satellite = factory.createSatellite(param);
                if (satellite != null) {
                    return satellite;
                }
            } catch (Exception e) {
                // Пропускаем неподходящую фабрику
                log.debug("Фабрика {} не подошла для параметра {}", factory.getClass().getSimpleName(), param);
            }
        }
        throw new IllegalArgumentException("No suitable factory found for param: " + param.getClass());
    }

    public void executeMission(MissionRequestWithType request) {
        log.info("🚀 Выполнение миссии: targetType={}, constellation={}, satellite={}",
                request.targetType(),
                request.constellationName(),
                request.satelliteName() != null ? request.satelliteName() : "все спутники");

        if (request.satelliteName() != null) {
            Satellite satellite = satelliteRepository
                    .findByNameAndConstellationConstellationName(request.satelliteName(), request.constellationName())
                    .orElseThrow(() -> new RuntimeException("Спутник не найден: " + request.satelliteName()));
            satellite.performMission();
            satelliteRepository.save(satellite);
            log.info("✅ Миссия выполнена спутником: {}", request.satelliteName());
        } else {
            SatelliteConstellation constellation = constellationService.getConstellation(request.constellationName());
            constellation.executeAllMissions();
            constellationService.updateConstellation(constellation.getId(), constellation);
            log.info("✅ Миссия выполнена всеми спутниками группировки: {}", request.constellationName());
        }
    }

    @Transactional(readOnly = true)
    public String getSystemOverview() {
        log.info("📊 Формирование системной сводки");
        return constellationService.getSystemOverview();
    }

    public void decommissionSatellite(String constellationName, String satelliteName) {
        log.info("🗑️ Вывод спутника из эксплуатации: {}/{}", constellationName, satelliteName);

        // Получаем спутник перед удалением, чтобы иметь его ID
        Satellite satellite = satelliteRepository
                .findByNameAndConstellationConstellationName(satelliteName, constellationName)
                .orElseThrow(() -> new RuntimeException("Спутник не найден: " + satelliteName));

        Long satelliteId = satellite.getId();

        // Удаляем спутник
        satelliteRepository.deleteByNameAndConstellationConstellationName(satelliteName, constellationName);

        // ✅ ОТПРАВКА СОБЫТИЯ В KAFKA
        eventPublisher.publishSatelliteDeleted(
                satelliteId,
                satelliteName,
                constellationName
        );

        log.info("✅ Спутник {} успешно выведен из эксплуатации", satelliteName);
    }

    @Transactional(readOnly = true)
    public ConstellationStatistics getConstellationStatistics(String constellationName) {
        log.info("📈 Получение статистики для группировки: {}", constellationName);

        SatelliteConstellation constellation = constellationService.getConstellation(constellationName);
        long activeCount = satelliteRepository.findActiveSatellitesByConstellationName(constellationName).size();

        ConstellationStatistics statistics = new ConstellationStatistics(
                constellation.getConstellationName(),
                constellation.getSatellites().size(),
                activeCount,
                constellation.getSatellites().size() - activeCount
        );

        log.info("📊 Статистика для {}: всего={}, активно={}, неактивно={}",
                constellationName, statistics.totalSatellites(),
                statistics.activeSatellites(), statistics.inactiveSatellites());

        return statistics;
    }

    public void activateAllSatellites(String constellationName) {
        log.info("🔛 Активация всех спутников в группировке: {}", constellationName);

        SatelliteConstellation constellation = constellationService.getConstellation(constellationName);
        constellation.getSatellites().forEach(satellite -> {
            satellite.activate();
            log.debug("🔛 Спутник {} активирован", satellite.getName());
        });
        constellationService.updateConstellation(constellation.getId(), constellation);

        log.info("✅ Все спутники в группировке {} активированы", constellationName);
    }

    public void deactivateAllSatellites(String constellationName) {
        log.info("🔴 Деактивация всех спутников в группировке: {}", constellationName);

        SatelliteConstellation constellation = constellationService.getConstellation(constellationName);
        constellation.getSatellites().forEach(satellite -> {
            satellite.deactivate();
            log.debug("🔴 Спутник {} деактивирован", satellite.getName());
        });
        constellationService.updateConstellation(constellation.getId(), constellation);

        log.info("✅ Все спутники в группировке {} деактивированы", constellationName);
    }

    /**
     * Статистика по группировке спутников
     */
    public record ConstellationStatistics(
            String constellationName,
            int totalSatellites,
            long activeSatellites,
            long inactiveSatellites
    ) {
        @Override
        public String toString() {
            return String.format(
                    "ConstellationStatistics{name='%s', total=%d, active=%d, inactive=%d}",
                    constellationName, totalSatellites, activeSatellites, inactiveSatellites
            );
        }
    }
}