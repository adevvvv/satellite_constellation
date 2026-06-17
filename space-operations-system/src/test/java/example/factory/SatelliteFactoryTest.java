package example.factory;

import org.example.exception.SpaceOperationException;
import org.example.factory.impl.CommunicationSatelliteFactory;
import org.example.factory.impl.ImagingSatelliteFactory;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.example.domains.CommunicationSatellite;
import org.example.domains.ImagingSatellite;
import org.example.domains.Satellite;
import org.example.params.CommunicationSatelliteParam;
import org.example.params.ImagingSatelliteParam;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class SatelliteFactoryTest {

    @Autowired
    private CommunicationSatelliteFactory communicationFactory;

    @Autowired
    private ImagingSatelliteFactory imagingFactory;

    @Test
    @DisplayName("Фабрика связи создает спутник с заданными параметрами")
    void communicationFactoryCreatesSatelliteWithParameter() {
        // Arrange
        CommunicationSatelliteParam param = new CommunicationSatelliteParam("КомСат-2", 0.9, 500.0);

        // Act
        Satellite satellite = communicationFactory.createSatelliteWithParameter(param);

        // Assert
        assertNotNull(satellite);
        assertTrue(satellite instanceof CommunicationSatellite);

        CommunicationSatellite commSat = (CommunicationSatellite) satellite;
        assertEquals("КомСат-2", commSat.getName());
        assertEquals(500.0, commSat.getBandwidth(), 0.001);
        assertEquals(0.9, commSat.getEnergy().getBatteryLevel(), 0.001);
    }

    @Test
    @DisplayName("Фабрика ДЗЗ создает спутник с заданными параметрами")
    void imagingFactoryCreatesSatelliteWithParameter() {
        // Arrange
        ImagingSatelliteParam param = new ImagingSatelliteParam("ДЗЗ-2", 0.85, 0.5);

        // Act
        Satellite satellite = imagingFactory.createSatelliteWithParameter(param);

        // Assert
        assertNotNull(satellite);
        assertTrue(satellite instanceof ImagingSatellite);

        ImagingSatellite imgSat = (ImagingSatellite) satellite;
        assertEquals("ДЗЗ-2", imgSat.getName());
        assertEquals(0.5, imgSat.getResolution(), 0.001);
        assertEquals(0.85, imgSat.getEnergy().getBatteryLevel(), 0.001);
    }

    @Test
    @DisplayName("Фабрика связи поддерживает только тип COMMUNICATION")
    void communicationFactorySupportsOnlyCommunicationType() {
        assertTrue(communicationFactory.isSatelliteTypeSupported(org.example.enums.SatelliteType.COMMUNICATION));
        assertFalse(communicationFactory.isSatelliteTypeSupported(org.example.enums.SatelliteType.IMAGE));
    }

    @Test
    @DisplayName("Фабрика ДЗЗ поддерживает только тип IMAGE")
    void imagingFactorySupportsOnlyImageType() {
        assertTrue(imagingFactory.isSatelliteTypeSupported(org.example.enums.SatelliteType.IMAGE));
        assertFalse(imagingFactory.isSatelliteTypeSupported(org.example.enums.SatelliteType.COMMUNICATION));
    }

    @Test
    @DisplayName("Фабрика связи выбрасывает исключение при неверном типе параметра")
    void communicationFactoryThrowsExceptionForWrongParameterType() {
        ImagingSatelliteParam wrongParam = new ImagingSatelliteParam("Неправильный", 0.8, 1.0);

        assertThrows(SpaceOperationException.class,
                () -> communicationFactory.createSatelliteWithParameter(wrongParam));
    }

    @Test
    @DisplayName("Фабрика ДЗЗ выбрасывает исключение при неверном типе параметра")
    void imagingFactoryThrowsExceptionForWrongParameterType() {
        CommunicationSatelliteParam wrongParam = new CommunicationSatelliteParam("Неправильный", 0.8, 100.0);

        assertThrows(SpaceOperationException.class,
                () -> imagingFactory.createSatelliteWithParameter(wrongParam));
    }
}