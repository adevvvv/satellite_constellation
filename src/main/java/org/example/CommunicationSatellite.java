package org.example;

public class CommunicationSatellite extends Satellite {
    private final double bandwidth;

    public CommunicationSatellite(String name, double batteryLevel, double bandwidth) {
        super(name, batteryLevel);
        this.bandwidth = bandwidth;
    }

    @Override
    public void performMission() {
        if (isActive) {
            System.out.println(name + ": Передача данных со скоростью " + bandwidth + " Мбит/с");
            sendData(bandwidth);
            consumeBattery(0.05);
        }
    }

    private void sendData(double dataAmount) {
        System.out.println(name + ": Отправил " + dataAmount + " Мбит данных!");
    }

    @Override
    public String toString() {
        return "CommunicationSatellite{bandwidth=" + bandwidth +
                ", name='" + name + "', isActive=" + isActive + ", batteryLevel=" + batteryLevel + "}";
    }
}