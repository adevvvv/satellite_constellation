package org.example;

import org.example.factory.impl.CommunicationSatelliteFactory;
import org.example.factory.impl.ImagingSatelliteFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.example.domains.Satellite;
import org.example.factory.SatelliteFactory;
import org.example.services.SpaceOperationCenterService;

@SpringBootApplication
public class Main {
    public static void main(String[] args) {
        ConfigurableApplicationContext context = SpringApplication.run(Main.class, args);

        SpaceOperationCenterService operationCenter = context.getBean(SpaceOperationCenterService.class);
        SatelliteFactory communicationFactory = context.getBean(CommunicationSatelliteFactory.class);
        SatelliteFactory imagingFactory = context.getBean(ImagingSatelliteFactory.class);

        System.out.println("СОЗДАНИЕ СПУТНИКОВ ЧЕРЕЗ ФАБРИКИ:");
        System.out.println("=================================");

        Satellite comSat1 = communicationFactory.createSatelliteWithParameter("Связь-1", 0.75, 500.0);
        Satellite comSat2 = communicationFactory.createSatellite("Связь-2", 0.85);
        Satellite imgSat1 = imagingFactory.createSatelliteWithParameter("ДЗЗ-1", 0.92, 2.5);
        Satellite imgSat2 = imagingFactory.createSatellite("ДЗЗ-2", 0.45);
        Satellite imgSat3 = imagingFactory.createSatellite("ДЗЗ-3", 0.15);

        System.out.println("\nСОЗДАНИЕ ГРУППИРОВОК ЧЕРЕЗ BUILDER:");
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