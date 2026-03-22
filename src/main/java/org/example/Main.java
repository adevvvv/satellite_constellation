package org.example;

import org.example.enums.SatelliteType;
import org.example.params.CommunicationSatelliteParam;
import org.example.params.ImagingSatelliteParam;
import org.example.params.SatelliteParam;
import org.example.requests.AddSatelliteRequest;
import org.example.requests.MissionRequest;
import org.example.requests.MissionRequestWithType;
import org.example.services.SatelliteService;
import org.example.services.SpaceOperationCenterService;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.example.domains.Satellite;
import org.example.services.ConstellationService;

import java.util.Arrays;
import java.util.List;
import java.util.Set;

@SpringBootApplication
public class Main {
    public static void main(String[] args) {
        ConfigurableApplicationContext context = SpringApplication.run(Main.class, args);

        SpaceOperationCenterService operationCenter = context.getBean(SpaceOperationCenterService.class);

        demonstrateFacadePattern(operationCenter);
    }

    private static void demonstrateFacadePattern(SpaceOperationCenterService operationCenter) {

        System.out.println("\n" + "=".repeat(60));
        System.out.println("ДЕМОНСТРАЦИЯ РАБОТЫ ФАСАДА SPACE OPERATION CENTER");
        System.out.println("=".repeat(60));

        // 1. Создание спутников через AddSatelliteRequest (автоматическое создание группировки)
        System.out.println("\n1. ДОБАВЛЕНИЕ СПУТНИКОВ С АВТОСОЗДАНИЕМ ГРУППИРОВКИ:");
        System.out.println("-".repeat(40));

        List<SatelliteParam> orbit1Satellites = Arrays.asList(
                new CommunicationSatelliteParam("Связь-1", 0.75, 500.0),
                new CommunicationSatelliteParam("Связь-2", 0.85, 100.0),
                new ImagingSatelliteParam("ДЗЗ-1", 0.92, 2.5)
        );

        // Группировка будет создана автоматически, так как ее нет
        operationCenter.addSatellite(new AddSatelliteRequest("Орбита-1", orbit1Satellites));

        // 2. Добавление спутников в существующую группировку
        System.out.println("\n2. ДОБАВЛЕНИЕ СПУТНИКОВ В СУЩЕСТВУЮЩУЮ ГРУППИРОВКУ:");
        System.out.println("-".repeat(40));

        List<SatelliteParam> orbit2Satellites = Arrays.asList(
                new ImagingSatelliteParam("ДЗЗ-2", 0.45, 1.0),
                new ImagingSatelliteParam("ДЗЗ-3", 0.15, 0.5)  // Низкий заряд для Observer
        );

        operationCenter.addSatellite(new AddSatelliteRequest("Орбита-2", orbit2Satellites));

        // 3. Выполнение миссии для всей группировки
        System.out.println("\n3. ВЫПОЛНЕНИЕ МИССИИ ДЛЯ ВСЕЙ ГРУППИРОВКИ:");
        System.out.println("-".repeat(40));
        operationCenter.executeMission(MissionRequestWithType.forConstellation("Орбита-1"));

        // 4. Выполнение миссии только для спутников связи
        System.out.println("\n4. ВЫПОЛНЕНИЕ МИССИИ ТОЛЬКО ДЛЯ СПУТНИКОВ СВЯЗИ:");
        System.out.println("-".repeat(40));
        operationCenter.executeMission(MissionRequestWithType.forConstellationWithTypes(
                "Орбита-1", Set.of(SatelliteType.COMMUNICATION)));

        // 5. Выполнение миссии для одного спутника
        System.out.println("\n5. ВЫПОЛНЕНИЕ МИССИИ ДЛЯ ОДНОГО СПУТНИКА:");
        System.out.println("-".repeat(40));
        operationCenter.executeMission(MissionRequestWithType.forSingleSatellite("Орбита-1", "ДЗЗ-1"));

        // 6. Получение статистики
        System.out.println("\n6. СТАТИСТИКА ГРУППИРОВОК:");
        System.out.println("-".repeat(40));
        System.out.println(operationCenter.getConstellationStatistics("Орбита-1"));
        System.out.println(operationCenter.getConstellationStatistics("Орбита-2"));

        // 7. Деактивация спутников
        System.out.println("\n7. ДЕАКТИВАЦИЯ СПУТНИКОВ:");
        System.out.println("-".repeat(40));
        operationCenter.deactivateAllSatellites("Орбита-2");

        // 8. Показ статуса
        System.out.println("\n8. СТАТУС ГРУППИРОВОК:");
        System.out.println("-".repeat(40));
        operationCenter.showConstellationStatus("Орбита-1");
        operationCenter.showConstellationStatus("Орбита-2");

        // 9. Системная сводка
        System.out.println(operationCenter.getSystemOverview());

        // 10. Демонстрация старого API
        System.out.println("\n9. ВЫПОЛНЕНИЕ МИССИИ (СТАРЫЙ API):");
        System.out.println("-".repeat(40));
        MissionRequest oldRequest = MissionRequest.forImagingOnly(
                Set.of("Орбита-1"),
                true
        );
        operationCenter.executeMission(oldRequest);

        // 11. Демонстрация миссии для всех группировок
        System.out.println("\n10. ВЫПОЛНЕНИЕ МИССИИ ДЛЯ ВСЕХ ГРУППИРОВОК:");
        System.out.println("-".repeat(40));
        operationCenter.executeMission(MissionRequestWithType.forAllConstellations());

        System.out.println("\n" + "=".repeat(60));
        System.out.println("ДЕМОНСТРАЦИЯ ЗАВЕРШЕНА");
        System.out.println("=".repeat(60));
    }
}