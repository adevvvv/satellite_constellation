package org.example.services;


import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.clients.SpaceOperationClient;
import org.example.domains.MissionRequest;
import org.example.properties.SpaceCenterServiceProperties;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.support.CronTrigger;
import org.springframework.stereotype.Service;

import java.time.ZoneId;
import java.util.TimeZone;

@Slf4j
@Service
@RequiredArgsConstructor
public class ConfiguredMissionScheduler {

    private final SpaceOperationClient spaceClient;
    private final SpaceCenterServiceProperties properties;
    private final TaskScheduler taskScheduler;

    /**
     * Инициализация планировщика при старте приложения
     */
    @PostConstruct
    public void init() {
        log.info("🔄 Инициализация планировщика миссий...");

        if (properties.missions() == null || properties.missions().isEmpty()) {
            log.warn("⚠️ Нет сконфигурированных миссий в application.yaml");
            return;
        }

        for (SpaceCenterServiceProperties.ConfiguredMissionConfig config : properties.missions()) {
            scheduleMission(config);
        }

        log.info("✅ Планировщик инициализирован. Запланировано миссий: {}", properties.missions().size());
    }

    /**
     * Планирование отдельной миссии
     */
    private void scheduleMission(SpaceCenterServiceProperties.ConfiguredMissionConfig config) {
        try {
            MissionRequest request = new MissionRequest(
                    config.targetType(),
                    config.constellationName(),
                    config.satelliteName()
            );

            CronTrigger trigger = new CronTrigger(
                    config.cron(),
                    TimeZone.getTimeZone(ZoneId.systemDefault())
            );

            taskScheduler.schedule(() -> executeMissionSafely(request, config), trigger);

            log.info("📅 Запланирована миссия: тип={}, группировка={}, cron={}",
                    config.targetType(),
                    config.constellationName(),
                    config.cron());

        } catch (Exception e) {
            log.error("❌ Ошибка при планировании миссии {}: {}", config, e.getMessage());
        }
    }

    /**
     * Безопасное выполнение миссии с обработкой ошибок
     */
    private void executeMissionSafely(MissionRequest request,
                                      SpaceCenterServiceProperties.ConfiguredMissionConfig config) {
        log.info("⏰ Запуск запланированной миссии по расписанию: {}", config.cron());

        try {
            spaceClient.executeMission(request);
        } catch (Exception e) {
            log.error("❌ Ошибка при выполнении запланированной миссии: {}", e.getMessage());
            // Не прерываем работу планировщика при ошибке
        }
    }
}