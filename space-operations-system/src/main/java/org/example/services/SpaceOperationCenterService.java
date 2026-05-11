package org.example.services;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.domains.*;
import org.example.requests.AddSatelliteRequest;
import org.example.requests.MissionRequestWithType;
import org.example.params.SatelliteParam;
import org.example.repository.SatelliteRepository;
import org.example.factory.SatelliteFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class SpaceOperationCenterService {

    private final ConstellationService constellationService;
    private final List<SatelliteFactory> satelliteFactories;
    private final SatelliteRepository satelliteRepository;

    public void addSatellite(AddSatelliteRequest request) {
        SatelliteConstellation constellation = constellationService.getConstellation(request.constellationName());

        for (SatelliteParam param : request.satelliteParams()) {
            Satellite satellite = createSatelliteFromParam(param);
            constellation.addSatellite(satellite);
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
            }
        }
        throw new IllegalArgumentException("No suitable factory found for param: " + param.getClass());
    }

    public void executeMission(MissionRequestWithType request) {
        if (request.satelliteName() != null) {
            Satellite satellite = satelliteRepository
                    .findByNameAndConstellationConstellationName(request.satelliteName(), request.constellationName())
                    .orElseThrow(() -> new RuntimeException("Спутник не найден: " + request.satelliteName()));
            satellite.performMission();
            satelliteRepository.save(satellite);
        } else {
            SatelliteConstellation constellation = constellationService.getConstellation(request.constellationName());
            constellation.executeAllMissions();
            constellationService.updateConstellation(constellation.getId(), constellation);
        }
    }

    @Transactional(readOnly = true)
    public String getSystemOverview() {
        return constellationService.getSystemOverview();
    }

    public void decommissionSatellite(String constellationName, String satelliteName) {
        satelliteRepository.deleteByNameAndConstellationConstellationName(satelliteName, constellationName);
        log.info("🗑️ Спутник {} выведен из эксплуатации", satelliteName);
    }

    @Transactional(readOnly = true)
    public ConstellationStatistics getConstellationStatistics(String constellationName) {
        SatelliteConstellation constellation = constellationService.getConstellation(constellationName);
        long activeCount = satelliteRepository.findActiveSatellitesByConstellationName(constellationName).size();

        return new ConstellationStatistics(
                constellation.getConstellationName(),
                constellation.getSatellites().size(),
                activeCount,
                constellation.getSatellites().size() - activeCount
        );
    }

    public void activateAllSatellites(String constellationName) {
        SatelliteConstellation constellation = constellationService.getConstellation(constellationName);
        constellation.getSatellites().forEach(Satellite::activate);
        constellationService.updateConstellation(constellation.getId(), constellation);
    }

    public void deactivateAllSatellites(String constellationName) {
        SatelliteConstellation constellation = constellationService.getConstellation(constellationName);
        constellation.getSatellites().forEach(Satellite::deactivate);
        constellationService.updateConstellation(constellation.getId(), constellation);
    }

    public record ConstellationStatistics(
            String constellationName,
            int totalSatellites,
            long activeSatellites,
            long inactiveSatellites
    ) {}
}