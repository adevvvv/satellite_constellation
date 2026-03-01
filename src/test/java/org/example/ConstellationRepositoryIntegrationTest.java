package org.example;

import org.example.domains.CommunicationSatellite;
import org.example.domains.ImagingSatellite;
import org.example.domains.Satellite;
import org.example.domains.SatelliteConstellation;
import org.example.repository.ConstellationRepository;
import org.example.services.SpaceOperationCenterService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@DisplayName("Интеграционные тесты для ConstellationRepository")
class ConstellationRepositoryIntegrationTest {

    private static final String CONSTELLATION_NAME = "Test-Constellation";
    private static final String CONSTELLATION_NAME_2 = "Test-Constellation-2";
    private static final String CONSTELLATION_NAME_3 = "Test-Constellation-3";
    private static final String NON_EXISTENT_CONSTELLATION = "NonExistent";

    @Autowired
    private ConstellationRepository repository;

    @Autowired
    private SpaceOperationCenterService operationCenter;

    private Satellite communicationSatellite;
    private Satellite imagingSatellite;

    @BeforeEach
    void setUp() {
        // Очищаем репозиторий перед каждым тестом
        repository.clear();

        communicationSatellite = new CommunicationSatellite("Test-Comm-1", 0.9, 500);
        imagingSatellite = new ImagingSatellite("Test-Image-1", 0.8, 2.5);
    }

    @Test
    @DisplayName("Полный жизненный цикл группировки: создание -> добавление спутников -> активация -> выполнение миссий")
    void completeConstellationLifecycle_ShouldWorkCorrectly() {
        // 1. Создание группировки
        operationCenter.createAndSaveConstellation(CONSTELLATION_NAME);

        // Проверка создания
        assertTrue(repository.containsConstellation(CONSTELLATION_NAME));
        SatelliteConstellation createdConstellation = repository.getConstellation(CONSTELLATION_NAME);
        assertNotNull(createdConstellation);
        assertEquals(CONSTELLATION_NAME, createdConstellation.getConstellationName());
        assertTrue(createdConstellation.getSatellites().isEmpty());

        // 2. Добавление спутников
        operationCenter.addSatelliteToConstellation(CONSTELLATION_NAME, communicationSatellite);
        operationCenter.addSatelliteToConstellation(CONSTELLATION_NAME, imagingSatellite);

        // Проверка добавления
        SatelliteConstellation afterAdd = repository.getConstellation(CONSTELLATION_NAME);
        List<Satellite> satellites = afterAdd.getSatellites();
        assertEquals(2, satellites.size());

        // Проверяем, что спутники добавились правильно
        assertTrue(satellites.stream().anyMatch(s -> s.getName().equals("Test-Comm-1")));
        assertTrue(satellites.stream().anyMatch(s -> s.getName().equals("Test-Image-1")));

        // Проверяем начальные состояния
        satellites.forEach(s -> {
            assertFalse(s.getState().isActive());
            assertEquals("Не активирован", s.getState().getStatus());
        });

        // 3. Активация всех спутников
        operationCenter.activateAllSatellites(CONSTELLATION_NAME);

        // Проверка активации
        SatelliteConstellation afterActivation = repository.getConstellation(CONSTELLATION_NAME);
        afterActivation.getSatellites().forEach(s -> {
            assertTrue(s.getState().isActive());
            assertEquals("Активен", s.getState().getStatus());
        });

        // 4. Выполнение миссий
        operationCenter.executeConstellationMission(CONSTELLATION_NAME);

        // Проверка после выполнения миссий (должен уменьшиться заряд)
        SatelliteConstellation afterMission = repository.getConstellation(CONSTELLATION_NAME);

        // CommunicationSatellite должен потратить 0.05 энергии
        Satellite commSat = afterMission.getSatellites().stream()
                .filter(s -> s instanceof CommunicationSatellite)
                .findFirst()
                .orElseThrow();
        assertTrue(commSat.getEnergy().getBatteryLevel() < 0.9);

        // ImagingSatellite должен потратить 0.08 энергии
        Satellite imgSat = afterMission.getSatellites().stream()
                .filter(s -> s instanceof ImagingSatellite)
                .findFirst()
                .orElseThrow();
        assertTrue(imgSat.getEnergy().getBatteryLevel() < 0.8);
    }

    @Nested
    @DisplayName("Тесты взаимодействия с репозиторием через сервис")
    class ServiceInteractionTests {

        @Test
        @DisplayName("Создание нескольких группировок и получение их всех")
        void multipleConstellations_ShouldBeRetrievable() {
            // Act
            operationCenter.createAndSaveConstellation(CONSTELLATION_NAME);
            operationCenter.createAndSaveConstellation(CONSTELLATION_NAME_2);
            operationCenter.createAndSaveConstellation(CONSTELLATION_NAME_3);

            // Assert
            Map<String, SatelliteConstellation> allConstellations = repository.getAllConstellations();
            assertEquals(3, allConstellations.size());
            assertTrue(allConstellations.containsKey(CONSTELLATION_NAME));
            assertTrue(allConstellations.containsKey(CONSTELLATION_NAME_2));
            assertTrue(allConstellations.containsKey(CONSTELLATION_NAME_3));
        }

        @Test
        @DisplayName("Обновление группировки через сервис должно сохраняться в репозитории")
        void updateViaService_ShouldPersistInRepository() {
            // Arrange
            operationCenter.createAndSaveConstellation(CONSTELLATION_NAME);

            // Act - добавляем спутник через сервис
            operationCenter.addSatelliteToConstellation(CONSTELLATION_NAME, communicationSatellite);

            // Assert - проверяем прямое получение из репозитория
            SatelliteConstellation fromRepo = repository.getConstellation(CONSTELLATION_NAME);
            assertEquals(1, fromRepo.getSatellites().size());
            assertEquals("Test-Comm-1", fromRepo.getSatellites().get(0).getName());
        }

        @Test
        @DisplayName("Удаление группировки через репозиторий должно работать")
        void removeConstellation_ShouldWork() {
            // Arrange
            operationCenter.createAndSaveConstellation(CONSTELLATION_NAME);
            assertTrue(repository.containsConstellation(CONSTELLATION_NAME));

            // Act
            repository.removeConstellation(CONSTELLATION_NAME);

            // Assert
            assertFalse(repository.containsConstellation(CONSTELLATION_NAME));
        }
    }

    @Nested
    @DisplayName("Тесты граничных случаев и ошибок")
    class ErrorHandlingTests {

        @Test
        @DisplayName("Попытка получить несуществующую группировку должна выбросить исключение")
        void getNonExistentConstellation_ShouldThrowException() {
            // Act & Assert
            RuntimeException exception = assertThrows(RuntimeException.class,
                    () -> repository.getConstellation(NON_EXISTENT_CONSTELLATION));
            assertTrue(exception.getMessage().contains("Группировка не найдена"));
        }

        @Test
        @DisplayName("Попытка обновить несуществующую группировку должна выбросить исключение")
        void updateNonExistentConstellation_ShouldThrowException() {
            SatelliteConstellation nonExistent = new SatelliteConstellation(NON_EXISTENT_CONSTELLATION);

            // Act & Assert
            RuntimeException exception = assertThrows(RuntimeException.class,
                    () -> repository.updateConstellation(nonExistent));
            assertTrue(exception.getMessage().contains("Группировка не найдена для обновления"));
        }

        @Test
        @DisplayName("Добавление спутника в несуществующую группировку должно выбросить исключение")
        void addSatelliteToNonExistentConstellation_ShouldThrowException() {
            // Act & Assert
            assertThrows(RuntimeException.class,
                    () -> operationCenter.addSatelliteToConstellation(NON_EXISTENT_CONSTELLATION, communicationSatellite));
        }

        @Test
        @DisplayName("Проверка contains для несуществующей группировки должна вернуть false")
        void containsNonExistentConstellation_ShouldReturnFalse() {
            // Act & Assert
            assertFalse(repository.containsConstellation(NON_EXISTENT_CONSTELLATION));
        }
    }

    @Nested
    @DisplayName("Тесты состояний спутников")
    class SatelliteStateTests {

        @Test
        @DisplayName("Активация спутников с низким зарядом должна быть отклонена")
        void activateSatellitesWithLowBattery_ShouldFail() {
            // Arrange
            Satellite lowBatterySat = new CommunicationSatellite("LowBattery", 0.1, 500);
            operationCenter.createAndSaveConstellation(CONSTELLATION_NAME);
            operationCenter.addSatelliteToConstellation(CONSTELLATION_NAME, lowBatterySat);

            // Act
            operationCenter.activateAllSatellites(CONSTELLATION_NAME);

            // Assert
            SatelliteConstellation result = repository.getConstellation(CONSTELLATION_NAME);
            Satellite satellite = result.getSatellites().get(0);
            assertFalse(satellite.getState().isActive());
            assertEquals("Недостаточно энергии", satellite.getState().getStatus());
        }

        @Test
        @DisplayName("Деактивация спутников должна работать правильно")
        void deactivateSatellites_ShouldWork() {
            // Arrange
            operationCenter.createAndSaveConstellation(CONSTELLATION_NAME);
            operationCenter.addSatelliteToConstellation(CONSTELLATION_NAME, communicationSatellite);
            operationCenter.activateAllSatellites(CONSTELLATION_NAME);

            // Проверяем, что спутник активирован
            SatelliteConstellation beforeDeactivation = repository.getConstellation(CONSTELLATION_NAME);
            assertTrue(beforeDeactivation.getSatellites().get(0).getState().isActive());

            // Act - деактивируем через прямое обращение к спутнику
            SatelliteConstellation constellation = repository.getConstellation(CONSTELLATION_NAME);
            constellation.getSatellites().forEach(Satellite::deactivate);
            repository.updateConstellation(constellation);

            // Assert
            SatelliteConstellation afterDeactivation = repository.getConstellation(CONSTELLATION_NAME);
            afterDeactivation.getSatellites().forEach(s -> {
                assertFalse(s.getState().isActive());
                assertEquals("Деактивирован", s.getState().getStatus());
            });
        }
    }
}