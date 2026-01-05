package org.example;

public abstract class Satellite {
    protected String name;
    protected boolean isActive;
    protected double batteryLevel;

    public Satellite(String name, double batteryLevel) {
        this.name = name;
        this.batteryLevel = Math.max(0.0, Math.min(1.0, batteryLevel));
        this.isActive = false;
        System.out.println("Создан спутник: " + name + " (заряд: " + (int) (batteryLevel * 100) + "%)");
    }

    // Геттеры
    public String getName() {
        return name;
    }

    public boolean isActive() {
        return isActive;
    }

    public double getBatteryLevel() {
        return batteryLevel;
    }

    public boolean activate() {
        if (batteryLevel > 0.2 && !isActive) {
            isActive = true;
            System.out.println("✅ " + name + ": Активация успешна");
            return true;
        }
        System.out.println("🛑 " + name + ": Ошибка активации (заряд: " + (int) (batteryLevel * 100) + "%)");
        return false;
    }

    public void deactivate() {
        if (isActive) {
            isActive = false;
        }
    }

    protected void consumeBattery(double amount) {
        if (amount > 0) {
            batteryLevel = Math.max(0.0, batteryLevel - amount);
            if (batteryLevel <= 0.2 && isActive) {
                deactivate();
            }
        }
    }

    public abstract void performMission();
}