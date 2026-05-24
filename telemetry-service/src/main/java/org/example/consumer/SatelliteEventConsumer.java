package org.example.consumer;

import lombok.extern.slf4j.Slf4j;
import org.example.event.SatelliteEvent;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Component
public class SatelliteEventConsumer {

    private final Map<String, SatelliteEvent> knownSatellites = new ConcurrentHashMap<>();

    @KafkaListener(topics = "satellite-events", groupId = "telemetry-service")
    public void consume(SatelliteEvent event) {
        log.info("📥 Получено событие: {} - спутник {}", event.getEventType(), event.getSatelliteName());

        switch (event.getEventType()) {
            case "SATELLITE_CREATED":
                handleSatelliteCreated(event);
                break;
            case "SATELLITE_DELETED":
                handleSatelliteDeleted(event);
                break;
            default:
                log.warn("⚠️ Неизвестный тип события: {}", event.getEventType());
        }
    }

    private void handleSatelliteCreated(SatelliteEvent event) {
        knownSatellites.put(event.getSatelliteName(), event);
        log.info("🛰️ Зарегистрирован новый спутник: {} (группировка: {})",
                event.getSatelliteName(), event.getConstellationName());
    }

    private void handleSatelliteDeleted(SatelliteEvent event) {
        knownSatellites.remove(event.getSatelliteName());
        log.info("🗑️ Спутник удален из реестра: {}", event.getSatelliteName());
    }

    public Map<String, SatelliteEvent> getKnownSatellites() {
        return knownSatellites;
    }
}