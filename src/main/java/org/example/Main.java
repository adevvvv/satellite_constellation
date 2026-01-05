package org.example;

public class Main {
    public static void main(String[] args) {
        System.out.println("Тестирование спутниковой группировки:");

        // Создаем спутники
        CommunicationSatellite commSat1 = new CommunicationSatellite("Связь-1", 0.85, 500);
        CommunicationSatellite commSat2 = new CommunicationSatellite("Связь-2", 0.75, 1000);
        ImagingSatellite imgSat1 = new ImagingSatellite("ДЗЗ-1", 0.92, 2.5);
        ImagingSatellite imgSat2 = new ImagingSatellite("ДЗЗ-2", 0.45, 1.0);

        // Создаем группировку
        SatelliteConstellation constellation = new SatelliteConstellation("Test Group");

        // Добавляем спутники
        constellation.addSatellite(commSat1);
        constellation.addSatellite(commSat2);
        constellation.addSatellite(imgSat1);
        constellation.addSatellite(imgSat2);

        // Активируем
        System.out.println("\nАктивация спутников:");
        for (Satellite sat : constellation.getSatellites()) {
            System.out.println(sat.name + ": " + (sat.activate() ? "Активация успешна" : "Ошибка активации"));
        }

        // Выполняем миссии
        System.out.println("\nВыполнение всех миссий группировки:");
        constellation.executeAllMissions();

        System.out.println("\nФинальное состояние:");
        for (Satellite sat : constellation.getSatellites()) {
            System.out.println(sat.toString());
        }
    }
}