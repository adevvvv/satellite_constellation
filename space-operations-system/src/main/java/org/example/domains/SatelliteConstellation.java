package org.example.domains;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "satellite_constellations")
@Getter
@Setter
@NoArgsConstructor
@ToString(exclude = "satellites")
@Slf4j
public class SatelliteConstellation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "constellation_name", nullable = false, unique = true, length = 255)
    private String constellationName;

    @OneToMany(mappedBy = "constellation", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<Satellite> satellites = new ArrayList<>();

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    public SatelliteConstellation(String constellationName) {
        this.constellationName = constellationName;
        this.satellites = new ArrayList<>();
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
        log.info("✨ Создана группировка: {}", constellationName);
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

    public void addSatellite(Satellite satellite) {
        if (satellite != null && !satellites.contains(satellite)) {
            satellites.add(satellite);
            satellite.setConstellation(this);
            log.info("➕ {} добавлен в '{}'", satellite.getName(), constellationName);
        }
    }

    public void removeSatellite(Satellite satellite) {
        satellites.remove(satellite);
        satellite.setConstellation(null);
        log.info("➖ {} удален из '{}'", satellite.getName(), constellationName);
    }

    public void executeAllMissions() {
        log.info("\n🚀 МИССИИ ГРУППИРОВКИ {}", constellationName.toUpperCase());
        log.info("=".repeat(50));
        for (Satellite satellite : satellites) {
            satellite.performMission();
        }
    }
}