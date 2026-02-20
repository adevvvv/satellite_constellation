package org.example;

import org.example.repository.ConstellationRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Юнит-тесты для ConstellationRepository")
class ConstellationRepositoryUnitTest {

    private static final String CONSTELLATION_NAME_1 = "Constellation-1";
    private static final String CONSTELLATION_NAME_2 = "Constellation-2";
    private static final String NON_EXISTENT_CONSTELLATION = "NonExistent";

    private ConstellationRepository repository;
    private SatelliteConstellation testConstellation1;
    private SatelliteConstellation testConstellation2;

    @BeforeEach
    void setUp() {
        repository = new ConstellationRepository();
        testConstellation1 = new SatelliteConstellation(CONSTELLATION_NAME_1);
        testConstellation2 = new SatelliteConstellation(CONSTELLATION_NAME_2);

        // Добавляем тестовые спутники
        Satellite satellite1 = new CommunicationSatellite("Comm-1", 0.9, 500);
        Satellite satellite2 = new ImagingSatellite("Image-1", 0.8, 2.5);
        testConstellation1.addSatellite(satellite1);
        testConstellation1.addSatellite(satellite2);
    }

    @Nested
    @DisplayName("Тесты добавления группировок")
    class AddConstellationTests {

        @Test
        @DisplayName("Добавление новой группировки должно сохранить её в репозитории")
        void addNewConstellation_ShouldSaveConstellation() {
            // Act
            repository.addConstellation(testConstellation1);

            // Assert
            assertTrue(repository.containsConstellation(CONSTELLATION_NAME_1));
            assertEquals(testConstellation1, repository.getConstellation(CONSTELLATION_NAME_1));
        }

        @Test
        @DisplayName("Добавление группировки с существующим именем должно перезаписать старую")
        void addConstellationWithExistingName_ShouldOverwriteOldOne() {
            // Arrange
            repository.addConstellation(testConstellation1);
            SatelliteConstellation updatedConstellation = new SatelliteConstellation(CONSTELLATION_NAME_1);
            Satellite newSatellite = new CommunicationSatellite("Comm-2", 0.95, 1000);
            updatedConstellation.addSatellite(newSatellite);

            // Act
            repository.addConstellation(updatedConstellation);

            // Assert
            SatelliteConstellation retrieved = repository.getConstellation(CONSTELLATION_NAME_1);
            assertEquals(1, retrieved.getSatellites().size());
            assertEquals("Comm-2", retrieved.getSatellites().get(0).getName());
        }
    }

    @Nested
    @DisplayName("Тесты получения группировок")
    class GetConstellationTests {

        @BeforeEach
        void setUp() {
            repository.addConstellation(testConstellation1);
            repository.addConstellation(testConstellation2);
        }

        @Test
        @DisplayName("Получение существующей группировки должно вернуть правильный объект")
        void getExistingConstellation_ShouldReturnCorrectConstellation() {
            // Act
            SatelliteConstellation result = repository.getConstellation(CONSTELLATION_NAME_1);

            // Assert
            assertNotNull(result);
            assertEquals(CONSTELLATION_NAME_1, result.getConstellationName());
            assertEquals(2, result.getSatellites().size());
        }

        @Test
        @DisplayName("Получение несуществующей группировки должно выбросить исключение")
        void getNonExistentConstellation_ShouldThrowException() {
            // Act & Assert
            RuntimeException exception = assertThrows(RuntimeException.class,
                    () -> repository.getConstellation(NON_EXISTENT_CONSTELLATION));
            assertTrue(exception.getMessage().contains("Группировка не найдена"));
        }

        @Test
        @DisplayName("Получение всех группировок должно вернуть неизменяемую копию")
        void getAllConstellations_ShouldReturnImmutableCopy() {
            // Act
            Map<String, SatelliteConstellation> allConstellations = repository.getAllConstellations();

            // Assert
            assertEquals(2, allConstellations.size());
            assertTrue(allConstellations.containsKey(CONSTELLATION_NAME_1));
            assertTrue(allConstellations.containsKey(CONSTELLATION_NAME_2));

            // Проверяем, что изменение возвращенной карты не влияет на оригинал
            allConstellations.clear();
            assertEquals(2, repository.getAllConstellations().size());
        }
    }

    @Nested
    @DisplayName("Тесты обновления группировок")
    class UpdateConstellationTests {

        @BeforeEach
        void setUp() {
            repository.addConstellation(testConstellation1);
        }

        @Test
        @DisplayName("Обновление существующей группировки должно изменить её данные")
        void updateExistingConstellation_ShouldModifyData() {
            // Arrange
            SatelliteConstellation updatedConstellation = new SatelliteConstellation(CONSTELLATION_NAME_1);
            updatedConstellation.addSatellite(new CommunicationSatellite("New-Comm", 0.7, 750));

            // Act
            repository.updateConstellation(updatedConstellation);

            // Assert
            SatelliteConstellation result = repository.getConstellation(CONSTELLATION_NAME_1);
            assertEquals(1, result.getSatellites().size());
            assertEquals("New-Comm", result.getSatellites().get(0).getName());
        }

        @Test
        @DisplayName("Обновление несуществующей группировки должно выбросить исключение")
        void updateNonExistentConstellation_ShouldThrowException() {
            // Arrange
            SatelliteConstellation nonExistent = new SatelliteConstellation(NON_EXISTENT_CONSTELLATION);

            // Act & Assert
            RuntimeException exception = assertThrows(RuntimeException.class,
                    () -> repository.updateConstellation(nonExistent));
            assertTrue(exception.getMessage().contains("Группировка не найдена для обновления"));
        }

        @Test
        @DisplayName("Обновление группировки с добавлением спутников должно сохранить изменения")
        void updateConstellationWithAddedSatellites_ShouldPreserveChanges() {
            // Arrange
            Satellite newSatellite = new ImagingSatellite("Image-2", 0.6, 1.8);
            testConstellation1.addSatellite(newSatellite);

            // Act
            repository.updateConstellation(testConstellation1);

            // Assert
            SatelliteConstellation result = repository.getConstellation(CONSTELLATION_NAME_1);
            assertEquals(3, result.getSatellites().size());
            assertTrue(result.getSatellites().stream()
                    .anyMatch(s -> s.getName().equals("Image-2")));
        }
    }

    @Nested
    @DisplayName("Тесты удаления группировок")
    class RemoveConstellationTests {

        @BeforeEach
        void setUp() {
            repository.addConstellation(testConstellation1);
            repository.addConstellation(testConstellation2);
        }

        @Test
        @DisplayName("Удаление существующей группировки должно убрать её из репозитория")
        void removeExistingConstellation_ShouldRemoveFromRepository() {
            // Act
            repository.removeConstellation(CONSTELLATION_NAME_1);

            // Assert
            assertFalse(repository.containsConstellation(CONSTELLATION_NAME_1));
            assertTrue(repository.containsConstellation(CONSTELLATION_NAME_2));
        }

        @Test
        @DisplayName("Удаление несуществующей группировки не должно влиять на другие")
        void removeNonExistentConstellation_ShouldNotAffectOthers() {
            // Act
            repository.removeConstellation(NON_EXISTENT_CONSTELLATION);

            // Assert
            assertTrue(repository.containsConstellation(CONSTELLATION_NAME_1));
            assertTrue(repository.containsConstellation(CONSTELLATION_NAME_2));
        }

        @Test
        @DisplayName("Проверка наличия группировки должна корректно работать")
        void containsConstellation_ShouldWorkCorrectly() {
            // Assert
            assertTrue(repository.containsConstellation(CONSTELLATION_NAME_1));
            assertTrue(repository.containsConstellation(CONSTELLATION_NAME_2));
            assertFalse(repository.containsConstellation(NON_EXISTENT_CONSTELLATION));
            assertFalse(repository.containsConstellation(null));
        }
    }

    @Nested
    @DisplayName("Граничные случаи")
    class BoundaryTests {

        @Test
        @DisplayName("Репозиторий должен корректно работать с очень длинными именами группировок")
        void repositoryShouldWorkWithVeryLongConstellationNames() {
            // Arrange
            String veryLongName = "A".repeat(1000);
            SatelliteConstellation longNameConstellation = new SatelliteConstellation(veryLongName);

            // Act
            repository.addConstellation(longNameConstellation);

            // Assert
            assertTrue(repository.containsConstellation(veryLongName));
            assertEquals(longNameConstellation, repository.getConstellation(veryLongName));
        }

        @Test
        @DisplayName("Репозиторий должен корректно обрабатывать null в качестве имени группировки")
        void repositoryShouldHandleNullConstellationName() {
            // Act & Assert - проверяем getConstellation с null
            RuntimeException exception = assertThrows(RuntimeException.class,
                    () -> repository.getConstellation(null));
            assertTrue(exception.getMessage().contains("Группировка не найдена: null"));

            // Проверяем containsConstellation с null
            assertFalse(repository.containsConstellation(null));

            // Проверяем removeConstellation с null (не должно выбрасывать исключение)
            assertDoesNotThrow(() -> repository.removeConstellation(null));
        }

        @Test
        @DisplayName("Репозиторий должен корректно работать с максимальным количеством группировок")
        void repositoryShouldHandleMaximumNumberOfConstellations() {
            // Arrange
            int maxConstellations = 1000;

            // Act
            for (int i = 0; i < maxConstellations; i++) {
                repository.addConstellation(new SatelliteConstellation("Constellation-" + i));
            }

            // Assert
            assertEquals(maxConstellations, repository.getAllConstellations().size());
        }
    }
}