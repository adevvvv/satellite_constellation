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

    // Новые поля для телеметрии
    @Column(name = "internal_temperature")
    protected double internalTemperature = 25.0;

    @Column(name = "external_temperature")
    protected double externalTemperature = -50.0;

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
        this.internalTemperature = 25.0;
        this.externalTemperature = -50.0;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
        log.info("Создан спутник: {} (заряд: {}, внутр.темп: {}°C, внеш.темп: {}°C)",
                name, batteryLevel, internalTemperature, externalTemperature);
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

    // Методы для обновления телеметрии
    public void updateTelemetry(double internalTemp, double externalTemp, double batteryLevel) {
        this.internalTemperature = internalTemp;
        this.externalTemperature = externalTemp;
        this.batteryLevel = Math.max(0.0, Math.min(1.0, batteryLevel));
        log.debug("📊 Телеметрия обновлена для {}: внутр.темп={}°C, внеш.темп={}°C, заряд={}%",
                name, String.format("%.1f", internalTemp),
                String.format("%.1f", externalTemp),
                String.format("%.1f", batteryLevel * 100));
    }

    public SatelliteState getState() {
        return new SatelliteState(this.isActive, this.statusMessage);
    }

    public EnergySystem getEnergy() {
        return EnergySystem.of(batteryLevel);
    }

    public String getTelemetryInfo() {
        return String.format("%s: внутр.темп=%.1f°C, внеш.темп=%.1f°C, заряд=%.1f%%",
                name, internalTemperature, externalTemperature, batteryLevel * 100);
    }

    public abstract void performMission();
}