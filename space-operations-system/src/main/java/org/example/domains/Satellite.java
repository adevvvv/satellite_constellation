package org.example.domains;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDateTime;

@Entity
@Table(name = "satellites")
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "satellite_type", discriminatorType = DiscriminatorType.STRING)
@Getter
@Setter
@NoArgsConstructor
@ToString(exclude = "constellation")
@Slf4j
public abstract class Satellite {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 255)
    protected String name;

    @Column(name = "battery_level", nullable = false)
    protected double batteryLevel;

    @Column(name = "is_active")
    protected boolean isActive = false;

    @Column(name = "status_message", length = 255)
    protected String statusMessage = "Не активирован";

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "constellation_id")
    protected SatelliteConstellation constellation;

    @Column(name = "created_at")
    protected LocalDateTime createdAt;

    @Column(name = "updated_at")
    protected LocalDateTime updatedAt;

    public Satellite(String name, double batteryLevel) {
        this.name = name;
        this.batteryLevel = batteryLevel;
        this.isActive = false;
        this.statusMessage = "Не активирован";
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
        log.info("Создан спутник: {} (заряд: {})", name, batteryLevel);
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    public void activate() {
        if (hasSufficientPower() && !isActive) {
            isActive = true;
            statusMessage = "Активен";
            log.info("✅ {}: Активация успешна", name);
        } else {
            statusMessage = isActive ? "Уже активен" : "Недостаточно энергии";
            log.info("🛑 {}: Ошибка активации (заряд: {}%)",
                    name, (int)(batteryLevel * 100));
        }
    }

    public void deactivate() {
        if (isActive) {
            isActive = false;
            statusMessage = "Деактивирован";
            log.info("⏹️ {}: Деактивирован", name);
        }
    }

    public boolean hasSufficientPower() {
        return batteryLevel > 0.1;
    }

    public void consumeEnergy(double amount) {
        if (amount <= 0 || batteryLevel <= 0.0) {
            return;
        }
        batteryLevel = Math.max(0.0, batteryLevel - amount);
    }

    public SatelliteState getState() {
        return new SatelliteState(this.isActive, this.statusMessage);
    }

    public EnergySystem getEnergy() {
        return EnergySystem.of(batteryLevel);
    }

    public abstract void performMission();
}