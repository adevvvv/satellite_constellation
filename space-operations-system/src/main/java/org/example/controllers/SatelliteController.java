package org.example.controllers;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.domains.Satellite;
import org.example.repository.SatelliteRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/satellites")
@RequiredArgsConstructor
public class SatelliteController {

    private final SatelliteRepository satelliteRepository;

    @PostMapping
    public ResponseEntity<Satellite> createSatellite(@RequestBody Satellite satellite) {
        log.info("🛰️ Создание нового спутника");
        Satellite saved = satelliteRepository.save(satellite);
        return ResponseEntity.status(HttpStatus.CREATED).body(saved);
    }

    @GetMapping
    public ResponseEntity<List<Satellite>> getAllSatellites() {
        log.info("📊 Получение всех спутников");
        return ResponseEntity.ok(satelliteRepository.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Satellite> getSatelliteById(@PathVariable Long id) {
        log.info("🔍 Получение спутника по ID: {}", id);
        return satelliteRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/constellation/{constellationName}")
    public ResponseEntity<List<Satellite>> getSatellitesByConstellation(@PathVariable String constellationName) {
        log.info("🔍 Получение спутников группировки: {}", constellationName);
        return ResponseEntity.ok(satelliteRepository.findByConstellationConstellationName(constellationName));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Satellite> updateSatellite(@PathVariable Long id, @RequestBody Satellite satellite) {
        log.info("📝 Обновление спутника: {}", id);
        if (!satelliteRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        satellite.setId(id);
        return ResponseEntity.ok(satelliteRepository.save(satellite));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteSatellite(@PathVariable Long id) {
        log.info("🗑️ Удаление спутника: {}", id);
        satelliteRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}