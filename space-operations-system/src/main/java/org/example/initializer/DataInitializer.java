package org.example.initializer;


import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.params.CommunicationSatelliteParam;
import org.example.params.ImagingSatelliteParam;
import org.example.params.SatelliteParam;
import org.example.requests.AddSatelliteRequest;
import org.example.services.SpaceOperationCenterService;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class DataInitializer {

    private final SpaceOperationCenterService operationCenter;

    @PostConstruct
    public void init() {
        log.info("🚀 Инициализация начальных данных...");

        // Проверяем и создаем группировки только если их нет
        createConstellationIfNotExists("GeoStationary",
                Arrays.asList(
                        new CommunicationSatelliteParam("GeoCom-1", 0.85, 1000.0),
                        new CommunicationSatelliteParam("GeoCom-2", 0.90, 800.0)
                ));

        createConstellationIfNotExists("LowOrbit",
                Arrays.asList(
                        new ImagingSatelliteParam("Sat-1", 0.75, 1.5),
                        new ImagingSatelliteParam("Sat-2", 0.80, 2.0)
                ));

        createConstellationIfNotExists("TestConstellation",
                Arrays.asList(
                        new CommunicationSatelliteParam("TestCom-1", 0.95, 500.0),
                        new ImagingSatelliteParam("TestImg-1", 0.88, 1.0)
                ));

        log.info("✅ Инициализация завершена");
    }

    private void createConstellationIfNotExists(String name, List<SatelliteParam> satellites) {
        try {
            operationCenter.showConstellationStatus(name);
            log.info("📌 Группировка {} уже существует", name);
        } catch (Exception e) {
            operationCenter.addSatellite(new AddSatelliteRequest(name, satellites));
            log.info("✅ Создана группировка {} с {} спутниками", name, satellites.size());
        }
    }
}