package org.example.domains;


public record MissionRequest(
        MissionTargetType targetType,
        String constellationName,
        String satelliteName
) {
    public MissionRequest {
        if (targetType == null) {
            throw new IllegalArgumentException("Тип миссии не может быть null");
        }
        if (targetType == MissionTargetType.CONSTELLATION &&
                (constellationName == null || constellationName.isBlank())) {
            throw new IllegalArgumentException(
                    "Для CONSTELLATION необходимо указать constellationName"
            );
        }
        if (targetType == MissionTargetType.SINGLE_SATELLITE) {
            if (constellationName == null || constellationName.isBlank()) {
                throw new IllegalArgumentException(
                        "Для SINGLE_SATELLITE необходимо указать constellationName"
                );
            }
            if (satelliteName == null || satelliteName.isBlank()) {
                throw new IllegalArgumentException(
                        "Для SINGLE_SATELLITE необходимо указать satelliteName"
                );
            }
        }
    }
}