package org.example.event;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.Instant;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SatelliteEvent {
    private String eventType;
    private Long satelliteId;
    private String satelliteName;
    private String constellationName;
    private Instant timestamp;
}