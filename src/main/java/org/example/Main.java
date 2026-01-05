package org.example;

public class Main {
    public static void main(String[] args) {
        System.out.println("ЗАПУСК СИСТЕМЫ УПРАВЛЕНИЯ СПУТНИКОВОЙ ГРУППИРОВКОЙ");
        System.out.println("=".repeat(60));

        System.out.println("СОЗДАНИЕ СПЕЦИАЛИЗИРОВАННЫХ СПУТНИКОВ:");
        System.out.println("-".repeat(45));

        CommunicationSatellite commSat1 = new CommunicationSatellite("Связь-1", 0.85, 500);
        CommunicationSatellite commSat2 = new CommunicationSatellite("Связь-2", 0.75, 1000);
        ImagingSatellite imgSat1 = new ImagingSatellite("ДЗЗ-1", 0.92, 2.5);
        ImagingSatellite imgSat2 = new ImagingSatellite("ДЗЗ-2", 0.45, 1.0);
        ImagingSatellite imgSat3 = new ImagingSatellite("ДЗЗ-3", 0.15, 0.5);

        System.out.println("-".repeat(45));

        SatelliteConstellation constellation = new SatelliteConstellation("RU Basic");
        System.out.println("-".repeat(45));

        System.out.println("ФОРМИРОВАНИЕ ГРУППИРОВКИ:");
        System.out.println("-".repeat(35));
        constellation.addSatellite(commSat1);
        constellation.addSatellite(commSat2);
        constellation.addSatellite(imgSat1);
        constellation.addSatellite(imgSat2);
        constellation.addSatellite(imgSat3);
        System.out.println("-".repeat(35));

        System.out.println(constellation.getSatellites());
        System.out.println("-".repeat(35));

        System.out.println("АКТИВАЦИЯ СПУТНИКОВ:");
        System.out.println("-".repeat(25));
        commSat1.activate();
        commSat2.activate();
        imgSat1.activate();
        imgSat2.activate();
        imgSat3.activate();

        constellation.executeAllMissions();

        System.out.println("\nФИНАЛЬНОЕ СОСТОЯНИЕ СПУТНИКОВ:");
        System.out.println(constellation.getSatellites());
    }
}