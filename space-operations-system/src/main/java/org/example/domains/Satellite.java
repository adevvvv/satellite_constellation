package org.example.domains;

import lombok.Getter;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;

@Getter
@ToString
@Slf4j
public abstract class Satellite {
    protected String name;
    protected SatelliteState state;
    protected EnergySystem energy;

    public Satellite(String name, double batteryLevel) {
        this.name = name;
        this.state = new SatelliteState();
        this.energy = EnergySystem.of(batteryLevel);
        log.info("Создан спутник: {} (заряд: {})", name, energy.getBatteryLevel());
    }

    public void activate() {
        if (state.activate(energy.hasSufficientPower())) {
            log.info("✅ {}: Активация успешна", name);
        } else {
            log.info("🛑 {}: Ошибка активации (заряд: {}%)",
                    name, (int)(energy.getBatteryLevel() * 100));
        }
    }

    public void deactivate() {
        if (state.isActive()) {
            state.deactivate();
            log.info("⏹️ {}: Деактивирован", name);
        }
    }

    public abstract void performMission();
}