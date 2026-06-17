package org.example.initializer;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.domains.CommunicationSatellite;
import org.example.domains.ImagingSatellite;
import org.example.domains.SatelliteConstellation;
import org.example.services.ConstellationService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class DataInitializer implements CommandLineRunner {

    private final ConstellationService constellationService;

    @Override
    public void run(String... args) {
        if (constellationService.getAllConstellations().isEmpty()) {
            log.info("📡 Инициализация тестовых данных...");

            SatelliteConstellation constellation = constellationService.createAndSaveConstellation("StarLink");

            CommunicationSatellite commSat = new CommunicationSatellite("CommSat-1", 0.9, 100.0);
            ImagingSatellite imgSat = new ImagingSatellite("ImgSat-1", 0.85, 0.5);

            constellationService.addSatelliteToConstellation("StarLink", commSat);
            constellationService.addSatelliteToConstellation("StarLink", imgSat);

            log.info("✅ Тестовые данные загружены в БД");
        }
    }
}