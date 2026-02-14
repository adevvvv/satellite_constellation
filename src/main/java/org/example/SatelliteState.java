package org.example;

public class SatelliteState {
    private boolean isActive = false;
    private String statusMessage;

    public SatelliteState() {
        this.statusMessage = "Не активирован";
    }

    public boolean activate(boolean hasSufficientPower) {
        if (hasSufficientPower && !isActive) {
            isActive = true;
            statusMessage = "Активен";
            return true;
        }
        statusMessage = hasSufficientPower ? "Уже активен" : "Недостаточно энергии";
        return false;
    }

    public void deactivate() {
        isActive = false;
        statusMessage = "Деактивирован";
    }

    public boolean isActive() {
        return isActive;
    }

    public String getStatus() {
        return statusMessage;
    }

    @Override
    public String toString() {
        return "SatelliteState{" +
                "isActive='" + isActive + '\'' +
                ", statusMessage='" + statusMessage + '\'' +
                '}';
    }
}