package org.example;

public class ImagingSatellite extends Satellite {
    private final double resolution;
    private int photosTaken;

    public ImagingSatellite(String name, double batteryLevel, double resolution) {
        super(name, batteryLevel);
        this.resolution = resolution;
        this.photosTaken = 0;
    }

    @Override
    public void performMission() {
        if (state.isActive()) {
            System.out.println("🛰️ " + name + ": Съемка территории с разрешением " + resolution + " м/пиксель");
            takePhoto();
            energy.consume(0.08);
        } else {
            System.out.println("🛑 " + name + ": Не может выполнить съемку - не активен (статус: " + getState().getStatus() + ")");
        }
    }

    private void takePhoto() {
        if (state.isActive()) {
            photosTaken++;
            System.out.println("📸 " + name + ": Снимок #" + photosTaken + " сделан!");
        }
    }

    @Override
    public String toString() {
        return "ImagingSatellite{resolution=" + resolution + ", photosTaken=" + photosTaken +
                ", name='" + name + "', state=" + state +
                ", energy=" + energy + "}";
    }
}