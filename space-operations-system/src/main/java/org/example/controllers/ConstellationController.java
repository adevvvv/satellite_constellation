package org.example.controllers;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.domains.SatelliteConstellation;
import org.example.services.ConstellationService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/constellations")
@RequiredArgsConstructor
public class ConstellationController {

    private final ConstellationService constellationService;

    @PostMapping
    public ResponseEntity<SatelliteConstellation> createConstellation(@RequestBody CreateConstellationRequest request) {
        log.info("📡 Создание новой группировки: {}", request.name());
        SatelliteConstellation constellation = constellationService.createAndSaveConstellation(request.name());
        return ResponseEntity.status(HttpStatus.CREATED).body(constellation);
    }

    @GetMapping
    public ResponseEntity<List<SatelliteConstellation>> getAllConstellations() {
        log.info("📊 Получение всех группировок");
        return ResponseEntity.ok(constellationService.getAllConstellations());
    }

    @GetMapping("/{id}")
    public ResponseEntity<SatelliteConstellation> getConstellationById(@PathVariable Long id) {
        log.info("🔍 Получение группировки по ID: {}", id);
        return ResponseEntity.ok(constellationService.getConstellation(id));
    }

    @GetMapping("/name/{name}")
    public ResponseEntity<SatelliteConstellation> getConstellationByName(@PathVariable String name) {
        log.info("🔍 Получение группировки по имени: {}", name);
        return ResponseEntity.ok(constellationService.getConstellation(name));
    }

    @PutMapping("/{id}")
    public ResponseEntity<SatelliteConstellation> updateConstellation(
            @PathVariable Long id,
            @RequestBody UpdateConstellationRequest request) {
        log.info("📝 Обновление группировки: {}", id);
        SatelliteConstellation constellation = constellationService.getConstellation(id);
        constellation.setConstellationName(request.name());
        return ResponseEntity.ok(constellationService.updateConstellation(id, constellation));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteConstellation(@PathVariable Long id) {
        log.info("🗑️ Удаление группировки: {}", id);
        constellationService.deleteConstellation(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/statistics")
    public ResponseEntity<String> getSystemOverview() {
        log.info("📈 Получение системной статистики");
        return ResponseEntity.ok(constellationService.getSystemOverview());
    }
}

record CreateConstellationRequest(String name) {
    public CreateConstellationRequest {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Имя группировки не может быть пустым");
        }
    }
}

record UpdateConstellationRequest(String name) {
    public UpdateConstellationRequest {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Имя группировки не может быть пустым");
        }
    }
}