package org.example.factory;

import org.example.factory.impl.CommunicationSatelliteFactory;
import org.example.factory.impl.ImagingSatelliteFactory;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.example.domains.CommunicationSatellite;
import org.example.domains.ImagingSatellite;
import org.example.domains.Satellite;
import org.example.constants.SatelliteConstants;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class SatelliteFactoryTest {

    @Autowired
    private CommunicationSatelliteFactory communicationFactory;

    @Autowired
    private ImagingSatelliteFactory imagingFactory;

    @Test
    @DisplayName("Фабрика связи создает спутник с дефолтными параметрами")
    void communicationFactoryCreatesSatelliteWithDefaultParameters() {
        // Act
        Satellite satellite = communicationFactory.createSatellite("КомСат-1", 0.8);

        // Assert
        assertNotNull(satellite);
        assertTrue(satellite instanceof CommunicationSatellite);
        assertEquals("КомСат-1", satellite.getName());

        CommunicationSatellite commSat = (CommunicationSatellite) satellite;
        assertEquals(SatelliteConstants.DEFAULT_COMMUNICATION_BANDWIDTH, commSat.getBandwidth(), 0.001);
        assertEquals(0.8, commSat.getEnergy().getBatteryLevel(), 0.001);
    }

    @Test
    @DisplayName("Фабрика связи создает спутник с заданным параметром")
    void communicationFactoryCreatesSatelliteWithParameter() {
        // Act
        Satellite satellite = communicationFactory.createSatelliteWithParameter("КомСат-2", 0.9, 500.0);

        // Assert
        assertNotNull(satellite);
        assertTrue(satellite instanceof CommunicationSatellite);

        CommunicationSatellite commSat = (CommunicationSatellite) satellite;
        assertEquals(500.0, commSat.getBandwidth(), 0.001);
        assertEquals(0.9, commSat.getEnergy().getBatteryLevel(), 0.001);
    }

    @Test
    @DisplayName("Фабрика ДЗЗ создает спутник с дефолтными параметрами")
    void imagingFactoryCreatesSatelliteWithDefaultParameters() {
        // Act
        Satellite satellite = imagingFactory.createSatellite("ДЗЗ-1", 0.7);

        // Assert
        assertNotNull(satellite);
        assertTrue(satellite instanceof ImagingSatellite);

        ImagingSatellite imgSat = (ImagingSatellite) satellite;
        assertEquals(SatelliteConstants.DEFAULT_IMAGING_RESOLUTION, imgSat.getResolution(), 0.001);
        assertEquals(0.7, imgSat.getEnergy().getBatteryLevel(), 0.001);
    }

    @Test
    @DisplayName("Фабрика ДЗЗ создает спутник с заданным параметром")
    void imagingFactoryCreatesSatelliteWithParameter() {
        // Act
        Satellite satellite = imagingFactory.createSatelliteWithParameter("ДЗЗ-2", 0.85, 0.5);

        // Assert
        assertNotNull(satellite);
        assertTrue(satellite instanceof ImagingSatellite);

        ImagingSatellite imgSat = (ImagingSatellite) satellite;
        assertEquals(0.5, imgSat.getResolution(), 0.001);
        assertEquals(0.85, imgSat.getEnergy().getBatteryLevel(), 0.001);
    }
}