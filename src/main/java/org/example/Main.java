package org.example;

import org.example.enums.SatelliteType;
import org.example.params.CommunicationSatelliteParam;
import org.example.params.ImagingSatelliteParam;
import org.example.services.SatelliteService;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.example.domains.Satellite;
import org.example.services.SpaceOperationCenterService;

@SpringBootApplication
public class Main {
    public static void main(String[] args) {
        ConfigurableApplicationContext context = SpringApplication.run(Main.class, args);

        SpaceOperationCenterService operationCenter = context.getBean(SpaceOperationCenterService.class);
        SatelliteService satelliteService = context.getBean(SatelliteService.class);

        System.out.println("СОЗДАНИЕ СПУТНИКОВ ЧЕРЕЗ СЕРВИС:");
        System.out.println("=================================");

        // Создание спутников через единый сервис
        Satellite comSat1 = satelliteService.createSatellite(
                new CommunicationSatelliteParam("Связь-1", 0.75, 500.0));
        Satellite comSat2 = satelliteService.createSatellite(
                new CommunicationSatelliteParam("Связь-2", 0.85, 100.0));
        Satellite imgSat1 = satelliteService.createSatellite(
                new ImagingSatelliteParam("ДЗЗ-1", 0.92, 2.5));
        Satellite imgSat2 = satelliteService.createSatellite(
                new ImagingSatelliteParam("ДЗЗ-2", 0.45, 1.0));
        Satellite imgSat3 = satelliteService.createSatellite(
                new ImagingSatelliteParam("ДЗЗ-3", 0.15, 0.5));

        System.out.println("\nСОЗДАНИЕ ГРУППИРОВОК:");
        System.out.println("=================================");
        operationCenter.createAndSaveConstellation("Орбита-1");
        operationCenter.createAndSaveConstellation("Орбита-2");

        System.out.println("\nДОБАВЛЕНИЕ СПУТНИКОВ:");
        System.out.println("=================================");
        operationCenter.addSatelliteToConstellation("Орбита-1", comSat1);
        operationCenter.addSatelliteToConstellation("Орбита-1", comSat2);
        operationCenter.addSatelliteToConstellation("Орбита-1", imgSat1);
        operationCenter.addSatelliteToConstellation("Орбита-2", imgSat2);
        operationCenter.addSatelliteToConstellation("Орбита-2", imgSat3);

        operationCenter.activateAllSatellites("Орбита-1");
        operationCenter.executeConstellationMission("Орбита-1");
        operationCenter.showConstellationStatus("Орбита-1");
    }
}