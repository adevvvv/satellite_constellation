package org.example.domains;

import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;
import org.example.constants.SatelliteConstants;

@Entity
@DiscriminatorValue("COMMUNICATION")
@Getter
@Setter
@NoArgsConstructor
@ToString(callSuper = true)
@Slf4j
public class CommunicationSatellite extends Satellite {

    @Column
    private Double bandwidth;

    public CommunicationSatellite(String name, double batteryLevel, double bandwidth) {
        super(name, batteryLevel);
        this.bandwidth = bandwidth;
    }

    @Override
    public void performMission() {
        if (isActive) {
            log.info("📡 {}: Передача данных со скоростью {} Мбит/с", name, bandwidth);
            consumeEnergy(SatelliteConstants.COMMUNICATION_ENERGY_CONSUMPTION);
        } else {
            log.info("🛑 {}: Не может выполнить миссию - не активен", name);
        }
    }
}