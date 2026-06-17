package org.example.clients;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.domains.MissionRequest;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

@Slf4j
@Service
@RequiredArgsConstructor
public class SpaceOperationClient {

    private final RestClient spaceOperationRestClient;

    /**
     * Выполнение миссии через API основного сервиса
     * @param request запрос на выполнение миссии
     */
    public void executeMission(MissionRequest request) {
        log.info("🚀 Отправка запроса на выполнение миссии: targetType={}, constellation={}, satellite={}",
                request.targetType(),
                request.constellationName(),
                request.satelliteName() != null ? request.satelliteName() : "N/A");

        try {
            spaceOperationRestClient.post()
                    .uri("/missions")
                    .body(request)
                    .retrieve()
                    .toBodilessEntity();

            log.info("✅ Миссия успешно выполнена: {}", request);
        } catch (RestClientException e) {
            log.error("❌ Ошибка при выполнении миссии {}: {}", request, e.getMessage());
            throw new RuntimeException("Ошибка при вызове основного сервиса: " + e.getMessage(), e);
        }
    }
}