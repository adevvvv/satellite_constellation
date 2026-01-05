package org.example;

public abstract class Satellite {
    protected String name;
    protected boolean isActive;
    protected double batteryLevel;

    public Satellite(String name, double batteryLevel) {
        this.name = name;
        this.batteryLevel = Math.max(0.0, Math.min(1.0, batteryLevel));
        this.isActive = false;
    }

    public boolean activate() {
        if (batteryLevel > 0.2 && !isActive) {
            isActive = true;
            return true;
        }
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