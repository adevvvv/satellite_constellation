package org.example.requests;


import org.example.enums.SatelliteType;

import java.util.Set;

/**
 * Запрос на выполнение миссии с указанием типа цели
 */
public record MissionRequestWithType(
        MissionTargetType targetType,
        String constellationName,
        Set<SatelliteType> targetTypes,
        String satelliteName
) {
    public MissionRequestWithType {
        if (targetType == MissionTargetType.SINGLE_SATELLITE &&
                (satelliteName == null || satelliteName.isBlank())) {
            throw new IllegalArgumentException("Для SINGLE_SATELLITE необходимо указать satelliteName");
        }
        if (targetType == MissionTargetType.CONSTELLATION &&
                (constellationName == null || constellationName.isBlank())) {
            throw new IllegalArgumentException("Для CONSTELLATION необходимо указать constellationName");
        }
    }

    public static MissionRequestWithType forConstellation(String constellationName) {
        return new MissionRequestWithType(
                MissionTargetType.CONSTELLATION,
                constellationName,
                null,
                null
        );
    }

    public static MissionRequestWithType forConstellationWithTypes(
            String constellationName,
            Set<SatelliteType> targetTypes) {
        return new MissionRequestWithType(
                MissionTargetType.CONSTELLATION,
                constellationName,
                targetTypes,
                null
        );
    }

    public static MissionRequestWithType forSingleSatellite(
            String constellationName,
            String satelliteName) {
        return new MissionRequestWithType(
                MissionTargetType.SINGLE_SATELLITE,
                constellationName,
                null,
                satelliteName
        );
    }

    public static MissionRequestWithType forAllConstellations() {
        return new MissionRequestWithType(
                MissionTargetType.ALL_CONSTELLATIONS,
                null,
                null,
                null
        );
    }
}