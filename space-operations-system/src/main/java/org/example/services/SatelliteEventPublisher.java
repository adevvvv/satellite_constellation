package org.example.services;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.event.SatelliteEvent;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Slf4j
@Service
@RequiredArgsConstructor
public class SatelliteEventPublisher {

    private final KafkaTemplate<String, SatelliteEvent> kafkaTemplate;
    private static final String TOPIC = "satellite-events";

    public void publishSatelliteCreated(Long satelliteId, String satelliteName, String constellationName) {
        SatelliteEvent event = SatelliteEvent.builder()
                .eventType("SATELLITE_CREATED")
                .satelliteId(satelliteId)
                .satelliteName(satelliteName)
                .constellationName(constellationName)
                .timestamp(Instant.now())
                .build();

        kafkaTemplate.send(TOPIC, satelliteName, event)
                .whenComplete((result, ex) -> {
                    if (ex == null) {
                        log.info("📤 Отправлено событие создания спутника: {} [offset: {}]",
                                satelliteName, result.getRecordMetadata().offset());
                    } else {
                        log.error("❌ Ошибка отправки события: {}", ex.getMessage());
                    }
                });
    }

    public void publishSatelliteDeleted(Long satelliteId, String satelliteName, String constellationName) {
        SatelliteEvent event = SatelliteEvent.builder()
                .eventType("SATELLITE_DELETED")
                .satelliteId(satelliteId)
                .satelliteName(satelliteName)
                .constellationName(constellationName)
                .timestamp(Instant.now())
                .build();

        kafkaTemplate.send(TOPIC, satelliteName, event)
                .whenComplete((result, ex) -> {
                    if (ex == null) {
                        log.info("📤 Отправлено событие удаления спутника: {} [offset: {}]",
                                satelliteName, result.getRecordMetadata().offset());
                    } else {
                        log.error("❌ Ошибка отправки события: {}", ex.getMessage());
                    }
                });
    }
}