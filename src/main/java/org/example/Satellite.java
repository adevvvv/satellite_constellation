package org.example;

public abstract class Satellite {
    protected String name;
    protected SatelliteState state;
    protected EnergySystem energy;

    public Satellite(String name, double batteryLevel) {
        this.name = name;
        this.state = new SatelliteState();
        this.energy = new EnergySystem(batteryLevel);
        System.out.println("Создан спутник: " + name + " (заряд: " + energy.getBatteryLevel() + ")");
    }

    public String getName() {
        return name;
    }

    public SatelliteState getState() {
        return state;
    }

    public EnergySystem getEnergy() {
        return energy;
    }

    public boolean activate() {
        if (state.activate(energy.hasSufficientPower())) {
            System.out.println("✅ " + name + ": Активация успешна");
            return true;
        }
        System.out.println("🛑 " + name + ": Ошибка активации (заряд: " + 
            (int)(getEnergy().getBatteryLevel() * 100) + "%)");
        return false;
    }

    public void deactivate() {
        if (state.isActive()) {
            state.deactivate();
            System.out.println("⏹️ " + name + ": Деактивирован");
        }
    }

    public abstract void performMission();
}