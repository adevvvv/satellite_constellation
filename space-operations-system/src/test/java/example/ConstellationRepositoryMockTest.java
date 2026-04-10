package example;

import org.example.domains.CommunicationSatellite;
import org.example.domains.ImagingSatellite;
import org.example.domains.Satellite;
import org.example.domains.SatelliteConstellation;
import org.example.repository.ConstellationRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Мок-тесты для ConstellationRepository")
class ConstellationRepositoryMockTest {

    private static final String CONSTELLATION_NAME_1 = "Constellation-1";
    private static final String CONSTELLATION_NAME_2 = "Constellation-2";

    @Mock
    private ConstellationRepository mockRepository;

    private SatelliteConstellation testConstellation1;
    private SatelliteConstellation testConstellation2;
    private Map<String, SatelliteConstellation> testConstellations;

    @BeforeEach
    void setUp() {
        testConstellation1 = new SatelliteConstellation(CONSTELLATION_NAME_1);
        testConstellation2 = new SatelliteConstellation(CONSTELLATION_NAME_2);

        Satellite satellite1 = new CommunicationSatellite("Comm-1", 0.9, 500);
        Satellite satellite2 = new ImagingSatellite("Image-1", 0.8, 2.5);
        testConstellation1.addSatellite(satellite1);
        testConstellation1.addSatellite(satellite2);

        testConstellations = new HashMap<>();
        testConstellations.put(CONSTELLATION_NAME_1, testConstellation1);
        testConstellations.put(CONSTELLATION_NAME_2, testConstellation2);
    }

    @Nested
    @DisplayName("Тесты добавления группировок с моками")
    class AddConstellationMockTests {

        @Test
        @DisplayName("Добавление группировки должно вызывать соответствующий метод репозитория")
        void addConstellation_ShouldCallRepositoryMethod() {
            // Act
            mockRepository.addConstellation(testConstellation1);

            // Assert
            Mockito.verify(mockRepository, Mockito.times(1)).addConstellation(testConstellation1);
            Mockito.verify(mockRepository, Mockito.never()).addConstellation(testConstellation2);
        }

        @Test
        @DisplayName("Проверка добавления группировки через верификацию аргументов")
        void addConstellation_ShouldBeVerifiableWithArguments() {
            // Act
            mockRepository.addConstellation(testConstellation1);
            mockRepository.addConstellation(testConstellation2);

            // Assert
            Mockito.verify(mockRepository, Mockito.times(2)).addConstellation(ArgumentMatchers.any(SatelliteConstellation.class));
            Mockito.verify(mockRepository).addConstellation(ArgumentMatchers.argThat(c ->
                    c.getConstellationName().equals(CONSTELLATION_NAME_1)));
            Mockito.verify(mockRepository).addConstellation(ArgumentMatchers.argThat(c ->
                    c.getConstellationName().equals(CONSTELLATION_NAME_2)));
        }
    }

    @Nested
    @DisplayName("Тесты получения группировок с моками")
    class GetConstellationMockTests {

        @Test
        @DisplayName("Получение существующей группировки должно вернуть ожидаемый объект")
        void getExistingConstellation_ShouldReturnMockedObject() {
            // Arrange
            Mockito.when(mockRepository.getConstellation(CONSTELLATION_NAME_1))
                    .thenReturn(testConstellation1);

            // Act
            SatelliteConstellation result = mockRepository.getConstellation(CONSTELLATION_NAME_1);

            // Assert
            assertEquals(testConstellation1, result);
            assertEquals(2, result.getSatellites().size());
            Mockito.verify(mockRepository, Mockito.times(1)).getConstellation(CONSTELLATION_NAME_1);
        }

        @Test
        @DisplayName("Получение несуществующей группировки должно выбросить исключение")
        void getNonExistentConstellation_ShouldThrowException() {
            // Arrange
            String nonExistentName = "NonExistent";
            Mockito.when(mockRepository.getConstellation(nonExistentName))
                    .thenThrow(new RuntimeException("Группировка не найдена: " + nonExistentName));

            // Act & Assert
            RuntimeException exception = assertThrows(RuntimeException.class,
                    () -> mockRepository.getConstellation(nonExistentName));
            assertTrue(exception.getMessage().contains("Группировка не найдена"));
            Mockito.verify(mockRepository, Mockito.times(1)).getConstellation(nonExistentName);
        }

        @Test
        @DisplayName("Получение всех группировок должно вернуть мокированную карту")
        void getAllConstellations_ShouldReturnMockedMap() {
            // Arrange
            Mockito.when(mockRepository.getAllConstellations()).thenReturn(testConstellations);

            // Act
            Map<String, SatelliteConstellation> result = mockRepository.getAllConstellations();

            // Assert
            assertEquals(2, result.size());
            assertTrue(result.containsKey(CONSTELLATION_NAME_1));
            assertTrue(result.containsKey(CONSTELLATION_NAME_2));
            Mockito.verify(mockRepository, Mockito.times(1)).getAllConstellations();
        }
    }

    @Nested
    @DisplayName("Тесты обновления группировок с моками")
    class UpdateConstellationMockTests {

        @Test
        @DisplayName("Обновление группировки должно вызывать метод репозитория")
        void updateConstellation_ShouldCallRepositoryMethod() {
            // Act
            mockRepository.updateConstellation(testConstellation1);

            // Assert
            Mockito.verify(mockRepository, Mockito.times(1)).updateConstellation(testConstellation1);
        }

        @Test
        @DisplayName("Обновление несуществующей группировки должно выбросить исключение")
        void updateNonExistentConstellation_ShouldThrowException() {
            String nonExistentName = "NonExistent";
            SatelliteConstellation nonExistent = new SatelliteConstellation(nonExistentName);

            Mockito.doThrow(new RuntimeException("Группировка не найдена для обновления: " + nonExistentName))
                    .when(mockRepository).updateConstellation(nonExistent);

            // Act & Assert
            RuntimeException exception = assertThrows(RuntimeException.class,
                    () -> mockRepository.updateConstellation(nonExistent));
            assertTrue(exception.getMessage().contains("Группировка не найдена для обновления"));
            Mockito.verify(mockRepository, Mockito.times(1)).updateConstellation(nonExistent);
        }

        @Test
        @DisplayName("Обновление с проверкой наличия")
        void updateConstellation_WithExistenceCheck() {
            SatelliteConstellation updatedConstellation = new SatelliteConstellation(CONSTELLATION_NAME_1);

            Mockito.when(mockRepository.containsConstellation(CONSTELLATION_NAME_1)).thenReturn(true);
            Mockito.doNothing().when(mockRepository).updateConstellation(updatedConstellation);

            // Act - эмулируем логику сервиса
            if (mockRepository.containsConstellation(CONSTELLATION_NAME_1)) {
                mockRepository.updateConstellation(updatedConstellation);
            }

            // Assert
            Mockito.verify(mockRepository).containsConstellation(CONSTELLATION_NAME_1);
            Mockito.verify(mockRepository).updateConstellation(updatedConstellation);
        }
    }

    @Nested
    @DisplayName("Тесты удаления группировок с моками")
    class RemoveConstellationMockTests {

        @Test
        @DisplayName("Удаление группировки должно вызывать метод репозитория")
        void removeConstellation_ShouldCallRepositoryMethod() {
            // Act
            mockRepository.removeConstellation(CONSTELLATION_NAME_1);

            // Assert
            Mockito.verify(mockRepository, Mockito.times(1)).removeConstellation(CONSTELLATION_NAME_1);
        }

        @Test
        @DisplayName("Проверка наличия группировки должна использовать мокированный метод")
        void containsConstellation_ShouldUseMockedMethod() {
            // Arrange
            Mockito.when(mockRepository.containsConstellation(CONSTELLATION_NAME_1)).thenReturn(true);
            Mockito.when(mockRepository.containsConstellation("NonExistent")).thenReturn(false);

            // Act & Assert
            assertTrue(mockRepository.containsConstellation(CONSTELLATION_NAME_1));
            assertFalse(mockRepository.containsConstellation("NonExistent"));

            Mockito.verify(mockRepository, Mockito.times(1)).containsConstellation(CONSTELLATION_NAME_1);
            Mockito.verify(mockRepository, Mockito.times(1)).containsConstellation("NonExistent");
        }
    }

    @Nested
    @DisplayName("Тестирование взаимодействий")
    class InteractionTests {

        @Test
        @DisplayName("Проверка порядка вызовов методов")
        void shouldVerifyMethodCallOrder() {
            // Arrange
            Mockito.when(mockRepository.containsConstellation(CONSTELLATION_NAME_1)).thenReturn(false);

            // Act
            boolean exists = mockRepository.containsConstellation(CONSTELLATION_NAME_1);
            if (!exists) {
                mockRepository.addConstellation(testConstellation1);
            }

            // Assert
            InOrder inOrder = inOrder(mockRepository);
            inOrder.verify(mockRepository).containsConstellation(CONSTELLATION_NAME_1);
            inOrder.verify(mockRepository).addConstellation(testConstellation1);
        }

        @Test
        @DisplayName("Проверка отсутствия нежелательных взаимодействий")
        void shouldVerifyNoUnwantedInteractions() {
            // Act
            mockRepository.containsConstellation(CONSTELLATION_NAME_1);

            // Assert
            Mockito.verify(mockRepository).containsConstellation(CONSTELLATION_NAME_1);
            verifyNoMoreInteractions(mockRepository);
        }
    }
}