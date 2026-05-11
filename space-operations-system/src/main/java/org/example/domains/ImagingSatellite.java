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
@DiscriminatorValue("IMAGING")
@Getter
@Setter
@NoArgsConstructor
@ToString(callSuper = true)
@Slf4j
public class ImagingSatellite extends Satellite {

    @Column
    private Double resolution;

    @Column(name = "photos_taken")
    private Integer photosTaken = 0;

    public ImagingSatellite(String name, double batteryLevel, double resolution) {
        super(name, batteryLevel);
        this.resolution = resolution;
        this.photosTaken = 0;
    }

    @Override
    public void performMission() {
        if (isActive) {
            log.info("🛰️ {}: Съемка территории с разрешением {} м/пиксель", name, resolution);
            photosTaken++;
            log.info("📸 {}: Снимок #{} сделан!", name, photosTaken);
            consumeEnergy(SatelliteConstants.IMAGING_ENERGY_CONSUMPTION);
        } else {
            log.info("🛑 {}: Не может выполнить съемку - не активен", name);
        }
    }
}