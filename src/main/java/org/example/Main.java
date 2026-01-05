package org.example;

public class Main {
    public static void main(String[] args) {
        System.out.println("Тестирование спутника ДЗЗ:");
        ImagingSatellite imgSat = new ImagingSatellite("ДЗЗ-Тест", 0.9, 2.5);

        System.out.println("Активация спутника:");
        boolean activated = imgSat.activate();
        System.out.println("Активация " + (activated ? "успешна" : "не удалась"));

        if (activated) {
            System.out.println("\nВыполнение миссии:");
            imgSat.performMission();
        }

        System.out.println("\nСостояние спутника: " + imgSat.toString());
    }
}