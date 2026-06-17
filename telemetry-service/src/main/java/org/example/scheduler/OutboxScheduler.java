package org.example.scheduler;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.entity.OutboxMessage;
import org.example.repository.OutboxRepository;
import org.example.services.SatelliteEventPublisher;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class OutboxScheduler {

    private final OutboxRepository outboxRepository;
    private final SatelliteEventPublisher eventPublisher;
    private int consecutiveFailures = 0;
    private static final int MAX_CONSECUTIVE_FAILURES = 10;

    @Scheduled(fixedDelay = 5000)
    @Transactional
    public void processOutboxMessages() {
        if (consecutiveFailures >= MAX_CONSECUTIVE_FAILURES) {
            log.warn("⚠️ Слишком много последовательных ошибок ({}). Пауза в обработке.", consecutiveFailures);
            return;
        }

        List<OutboxMessage> pendingMessages = outboxRepository.findPendingMessages();

        if (pendingMessages.isEmpty()) {
            return;
        }

        log.debug("📬 Найдено {} сообщений для отправки", pendingMessages.size());

        boolean hasFailures = false;

        for (OutboxMessage message : pendingMessages) {
            try {
                eventPublisher.sendEventToKafka(message);
                message.setStatus(OutboxMessage.OutboxStatus.SENT);
                outboxRepository.save(message);
                log.info("✅ Сообщение отправлено: {}", message.getId());
                consecutiveFailures = 0; // Сбрасываем счетчик при успехе
            } catch (Exception e) {
                log.error("❌ Ошибка при отправке сообщения {}: {}", message.getId(), e.getMessage());
                hasFailures = true;

                // Если сообщение слишком старое, помечаем как отправленное, чтобы избежать бесконечных попыток
                if (message.getCreatedAt().plusHours(24).isBefore(LocalDateTime.now())) {
                    log.warn("⚠️ Сообщение {} старше 24 часов, пропускаем", message.getId());
                    message.setStatus(OutboxMessage.OutboxStatus.SENT);
                    outboxRepository.save(message);
                }
            }
        }

        if (hasFailures) {
            consecutiveFailures++;
        }
    }
}