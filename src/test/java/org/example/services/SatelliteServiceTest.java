package org.example.services;

import org.example.domains.ImagingSatellite;
import org.example.domains.Satellite;
import org.example.exception.SpaceOperationException;
import org.example.params.CommunicationSatelliteParam;
import org.example.params.ImagingSatelliteParam;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@DisplayName("Интеграционные тесты для SatelliteServiceImpl")
class SatelliteServiceTest {

    @Autowired
    private SatelliteService satelliteService;

    @Test
    @DisplayName("Сервис создает спутник ДЗЗ через подходящую фабрику")
    void serviceCreatesImagingSatellite() {
        // Arrange
        ImagingSatelliteParam param = new ImagingSatelliteParam("ДЗЗ-Тест", 0.75, 2.0);

        // Act
        Satellite satellite = satelliteService.createSatellite(param);

        // Assert
        assertNotNull(satellite);
        assertTrue(satellite instanceof ImagingSatellite);
        assertEquals("ДЗЗ-Тест", satellite.getName());
        assertEquals(0.75, satellite.getEnergy().getBatteryLevel(), 0.001);

        ImagingSatellite imgSat = (ImagingSatellite) satellite;
        assertEquals(2.0, imgSat.getResolution(), 0.001);
    }

    @Test
    @DisplayName("Сервис создает спутник связи через подходящую фабрику")
    void serviceCreatesCommunicationSatellite() {
        // Arrange
        CommunicationSatelliteParam param = new CommunicationSatelliteParam("Связь-Тест", 0.85, 750.0);

        // Act
        Satellite satellite = satelliteService.createSatellite(param);

        // Assert
        assertNotNull(satellite);
        assertTrue(satellite instanceof org.example.domains.CommunicationSatellite);
        assertEquals("Связь-Тест", satellite.getName());
        assertEquals(0.85, satellite.getEnergy().getBatteryLevel(), 0.001);

        org.example.domains.CommunicationSatellite commSat =
                (org.example.domains.CommunicationSatellite) satellite;
        assertEquals(750.0, commSat.getBandwidth(), 0.001);
    }

    @Test
    @DisplayName("Сервис выбрасывает исключение при отсутствии подходящей фабрики")
    void serviceThrowsExceptionWhenNoFactoryFound() {
        // Arrange - создаем параметр с типом, которого нет в системе
        // Для теста создадим анонимный класс с неизвестным типом
        org.example.params.SatelliteParam unknownParam =
                new org.example.params.SatelliteParam(null, "Неизвестный", 0.5) {};

        // Act & Assert
        assertThrows(SpaceOperationException.class,
                () -> satelliteService.createSatellite(unknownParam));
    }

    @Test
    @DisplayName("Сервис корректно передает все характеристики спутнику ДЗЗ")
    void servicePassesAllImagingSatelliteProperties() {
        // Arrange
        String expectedName = "ДЗЗ-ВысокоеРазрешение";
        double expectedBattery = 0.95;
        double expectedResolution = 0.25;
        ImagingSatelliteParam param = new ImagingSatelliteParam(
                expectedName, expectedBattery, expectedResolution
        );

        // Act
        Satellite satellite = satelliteService.createSatellite(param);

        // Assert
        ImagingSatellite imgSat = (ImagingSatellite) satellite;
        assertEquals(expectedName, imgSat.getName());
        assertEquals(expectedBattery, imgSat.getEnergy().getBatteryLevel(), 0.001);
        assertEquals(expectedResolution, imgSat.getResolution(), 0.001);
    }

    @Test
    @DisplayName("Сервис корректно передает все характеристики спутнику связи")
    void servicePassesAllCommunicationSatelliteProperties() {
        // Arrange
        String expectedName = "Связь-ВысокаяПропускнаяСпособность";
        double expectedBattery = 0.88;
        double expectedBandwidth = 1200.0;
        CommunicationSatelliteParam param = new CommunicationSatelliteParam(
                expectedName, expectedBattery, expectedBandwidth
        );

        // Act
        Satellite satellite = satelliteService.createSatellite(param);

        // Assert
        org.example.domains.CommunicationSatellite commSat =
                (org.example.domains.CommunicationSatellite) satellite;
        assertEquals(expectedName, commSat.getName());
        assertEquals(expectedBattery, commSat.getEnergy().getBatteryLevel(), 0.001);
        assertEquals(expectedBandwidth, commSat.getBandwidth(), 0.001);
    }
}