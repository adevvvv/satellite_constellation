package org.example;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

@SpringBootApplication
public class Main {
    public static void main(String[] args) {
        System.out.println("ЗАПУСК СИСТЕМЫ УПРАВЛЕНИЯ СПУТНИКОВОЙ ГРУППИРОВКОЙ");
        System.out.println("=".repeat(60));

        // Запускаем Spring контекст
        ConfigurableApplicationContext context = SpringApplication.run(Main.class, args);

        // Получаем бины из контекста
        ConstellationRepository constellationRepository = context.getBean(ConstellationRepository.class);
        SpaceOperationCenterService operationCenter = context.getBean(SpaceOperationCenterService.class);

        System.out.println("\nСОЗДАНИЕ СПЕЦИАЛИЗИРОВАННЫХ СПУТНИКОВ:");
        System.out.println("-".repeat(45));

        // Спутники связи
        CommunicationSatellite commSat1 = new CommunicationSatellite("Связь-1", 0.85, 500);
        CommunicationSatellite commSat2 = new CommunicationSatellite("Связь-2", 0.75, 1000);

        // Спутники ДЗЗ
        ImagingSatellite imgSat1 = new ImagingSatellite("ДЗЗ-1", 0.92, 2.5);
        ImagingSatellite imgSat2 = new ImagingSatellite("ДЗЗ-2", 0.45, 1.0);
        ImagingSatellite imgSat3 = new ImagingSatellite("ДЗЗ-3", 0.15, 0.5);

        System.out.println("-".repeat(45));

        // Создаем группировки
        System.out.println("\nСОЗДАНИЕ ГРУППИРОВОК:");
        System.out.println("-".repeat(35));
        operationCenter.createAndSaveConstellation("Орбита-1");
        operationCenter.createAndSaveConstellation("Орбита-2");
        System.out.println("-".repeat(45));

        // Добавляем спутники в группировки
        System.out.println("\n📡 ДОБАВЛЕНИЕ СПУТНИКОВ:");
        operationCenter.addSatelliteToConstellation("Орбита-1", commSat1);
        operationCenter.addSatelliteToConstellation("Орбита-1", imgSat1);
        operationCenter.addSatelliteToConstellation("Орбита-1", imgSat2);

        operationCenter.addSatelliteToConstellation("Орбита-2", commSat2);
        operationCenter.addSatelliteToConstellation("Орбита-2", imgSat3);
        System.out.println("-".repeat(35));

        // Показываем начальное состояние
        System.out.println("\n📊 НАЧАЛЬНОЕ СОСТОЯНИЕ:");
        operationCenter.showConstellationStatus("Орбита-1");
        operationCenter.showConstellationStatus("Орбита-2");

        // Активируем спутники в первой группировке и выполняем миссии
        operationCenter.activateAllSatellites("Орбита-1");
        operationCenter.executeConstellationMission("Орбита-1");

        // Показываем финальное состояние
        System.out.println("\n📊 ФИНАЛЬНОЕ СОСТОЯНИЕ:");
        operationCenter.showConstellationStatus("Орбита-1");

        // Показываем все группировки
        operationCenter.showAllConstellations();

        // Закрываем контекст
        context.close();
    }
}