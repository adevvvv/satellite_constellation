package org.example.controllers;


import lombok.RequiredArgsConstructor;
import org.example.properties.SpaceCenterServiceProperties;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/scheduler")
@RequiredArgsConstructor
public class SchedulerStatusController {

    private final SpaceCenterServiceProperties properties;

    @GetMapping("/missions")
    public ResponseEntity<List<SpaceCenterServiceProperties.ConfiguredMissionConfig>> getScheduledMissions() {
        return ResponseEntity.ok(properties.missions());
    }

    @GetMapping("/status")
    public ResponseEntity<Map<String, Object>> getStatus() {
        return ResponseEntity.ok(Map.of(
                "status", "RUNNING",
                "targetService", properties.url(),
                "scheduledMissions", properties.missions().size()
        ));
    }
}