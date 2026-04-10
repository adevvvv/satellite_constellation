package org.example.controllers;

import lombok.RequiredArgsConstructor;
import org.example.requests.AddSatelliteRequest;
import org.example.requests.MissionRequestWithType;
import org.example.services.SpaceOperationCenterService;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class SpaceOperationController {

    private final SpaceOperationCenterService spaceOperationCenterService;

    @PostMapping("/add-satellites")
    public ResponseEntity<Void> addSatellites(@RequestBody AddSatelliteRequest request) {
        log.info("📡 Получен запрос на добавление спутников в группировку: {}", request.constellationName());
        spaceOperationCenterService.addSatellite(request);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/missions")
    public ResponseEntity<Void> executeMission(@RequestBody MissionRequestWithType request) {
        log.info("🚀 Получен запрос на выполнение миссии: targetType={}, constellation={}, satellite={}",
                request.targetType(),
                request.constellationName(),
                request.satelliteName() != null ? request.satelliteName() : "N/A");
        spaceOperationCenterService.executeMission(request);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/overview")
    public ResponseEntity<String> getSystemOverview() {
        log.info("📊 Получен запрос на системную сводку");
        String overview = spaceOperationCenterService.getSystemOverview();
        return ResponseEntity.ok(overview);
    }

    @DeleteMapping("/constellations/{constellationName}/satellites/{satelliteName}")
    public ResponseEntity<Void> decommissionSatellite(
            @PathVariable String constellationName,
            @PathVariable String satelliteName) {
        log.info("🗑️ Запрос на вывод спутника из эксплуатации: {}/{}", constellationName, satelliteName);
        spaceOperationCenterService.decommissionSatellite(constellationName, satelliteName);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/constellations/{constellationName}/statistics")
    public ResponseEntity<SpaceOperationCenterService.ConstellationStatistics> getStatistics(
            @PathVariable String constellationName) {
        log.info("📈 Получен запрос статистики для группировки: {}", constellationName);
        return ResponseEntity.ok(spaceOperationCenterService.getConstellationStatistics(constellationName));
    }

    @PostMapping("/constellations/{constellationName}/activate")
    public ResponseEntity<Void> activateAllSatellites(@PathVariable String constellationName) {
        log.info("🔛 Активация всех спутников в группировке: {}", constellationName);
        spaceOperationCenterService.activateAllSatellites(constellationName);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/constellations/{constellationName}/deactivate")
    public ResponseEntity<Void> deactivateAllSatellites(@PathVariable String constellationName) {
        log.info("🔴 Деактивация всех спутников в группировке: {}", constellationName);
        spaceOperationCenterService.deactivateAllSatellites(constellationName);
        return ResponseEntity.ok().build();
    }
}