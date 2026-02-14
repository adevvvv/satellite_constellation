package org.example;

public class EnergySystem {
    private double batteryLevel;
    
    public static final double LOW_BATTERY_THRESHOLD = 0.2;
    private static final double MAX_BATTERY = 1.0;
    private static final double MIN_BATTERY = 0.0;

    public EnergySystem(double initialBattery) {
        this.batteryLevel = Math.max(MIN_BATTERY, Math.min(MAX_BATTERY, initialBattery));
    }

    public double getBatteryLevel() {
        return batteryLevel;
    }

    public boolean consume(double amount) {
        if (amount <= 0 || batteryLevel <= MIN_BATTERY) {
            return false;
        }

        batteryLevel = Math.max(MIN_BATTERY, batteryLevel - amount);
        return true;
    }

    public boolean hasSufficientPower() {
        return batteryLevel > LOW_BATTERY_THRESHOLD;
    }

    @Override
    public String toString() {
        return "EnergySystem{" +
                "batteryLevel=" + batteryLevel +
                '}';
    }
}