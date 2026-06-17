package org.example.properties;


import org.example.domains.MissionTargetType;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.List;

@ConfigurationProperties(prefix = "app.space-center-service")
public record SpaceCenterServiceProperties(
        String url,
        List<ConfiguredMissionConfig> missions
) {
    public record ConfiguredMissionConfig(
            MissionTargetType targetType,
            String constellationName,
            String satelliteName,
            String cron
    ) {
        public ConfiguredMissionConfig {
            // Валидация конфигурации
            if (targetType == MissionTargetType.CONSTELLATION &&
                    (constellationName == null || constellationName.isBlank())) {
                throw new IllegalArgumentException(
                        "Для миссии типа CONSTELLATION необходимо указать constellationName"
                );
            }
            if (targetType == MissionTargetType.SINGLE_SATELLITE) {
                if (constellationName == null || constellationName.isBlank()) {
                    throw new IllegalArgumentException(
                            "Для миссии типа SINGLE_SATELLITE необходимо указать constellationName"
                    );
                }
                if (satelliteName == null || satelliteName.isBlank()) {
                    throw new IllegalArgumentException(
                            "Для миссии типа SINGLE_SATELLITE необходимо указать satelliteName"
                    );
                }
            }
        }
    }
}