package org.example.requests;

import lombok.Getter;
import lombok.Builder;
import org.example.enums.SatelliteType;

import java.util.Set;

/**
 * Запрос на выполнение миссии
 */
public record MissionRequest(
        Set<String> constellationNames,
        Set<SatelliteType> satelliteTypes,
        boolean activateBeforeMission
) {
    public MissionRequest {
        if (constellationNames == null || constellationNames.isEmpty()) {
            throw new IllegalArgumentException("Список группировок не может быть пустым");
        }
    }

    public static MissionRequest forAllMissions(Set<String> constellationNames, boolean activate) {
        return new MissionRequest(constellationNames, null, activate);
    }

    public static MissionRequest forCommunicationOnly(Set<String> constellationNames, boolean activate) {
        return new MissionRequest(constellationNames, Set.of(SatelliteType.COMMUNICATION), activate);
    }

    public static MissionRequest forImagingOnly(Set<String> constellationNames, boolean activate) {
        return new MissionRequest(constellationNames, Set.of(SatelliteType.IMAGE), activate);
    }
}