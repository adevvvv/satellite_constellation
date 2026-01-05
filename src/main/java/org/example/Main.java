package org.example;

public class Main {
    public static void main(String[] args) {
        System.out.println("Тестирование обоих типов спутников:");

        // Спутник связи
        CommunicationSatellite commSat = new CommunicationSatellite("Связь-Тест", 0.8, 500);

        // Спутник ДЗЗ
        ImagingSatellite imgSat = new ImagingSatellite("ДЗЗ-Тест", 0.6, 1.0);

        System.out.println("\nАктивация спутников:");
        System.out.println("Связь-Тест: " + (commSat.activate() ? "Активация успешна" : "Ошибка активации"));
        System.out.println("ДЗЗ-Тест: " + (imgSat.activate() ? "Активация успешна" : "Ошибка активации"));

        System.out.println("\nВыполнение миссий:");
        commSat.performMission();
        imgSat.performMission();

        System.out.println("\nСостояние спутников:");
        System.out.println(commSat.toString());
        System.out.println(imgSat.toString());
    }
}