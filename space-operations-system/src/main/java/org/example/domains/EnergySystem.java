package org.example.domains;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;
import org.example.constants.EnergySystemConstants;

@Getter
@ToString
@Builder
public class EnergySystem {
    private double batteryLevel;

    public static EnergySystem of(double initialBattery) {
        return EnergySystem.builder()
                .batteryLevel(Math.max(EnergySystemConstants.MIN_BATTERY,
                        Math.min(EnergySystemConstants.MAX_BATTERY, initialBattery)))
                .build();
    }

    public void consume(double amount) {
        if (amount <= 0 || batteryLevel <= EnergySystemConstants.MIN_BATTERY) {
            return;
        }
        batteryLevel = Math.max(EnergySystemConstants.MIN_BATTERY, batteryLevel - amount);
    }

    public boolean hasSufficientPower() {
        return batteryLevel > EnergySystemConstants.LOW_BATTERY_THRESHOLD;
    }
}