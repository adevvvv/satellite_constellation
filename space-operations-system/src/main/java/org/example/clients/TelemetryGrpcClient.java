package org.example.clients;

import io.grpc.stub.StreamObserver;
import lombok.extern.slf4j.Slf4j;
import net.devh.boot.grpc.client.inject.GrpcClient;
import org.example.telemetry.TelemetryRequest;
import org.example.telemetry.TelemetryServiceGrpc;
import org.example.telemetry.TelemetryUpdate;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Service;

import java.util.concurrent.atomic.AtomicBoolean;

@Service
@Slf4j
public class TelemetryGrpcClient implements CommandLineRunner {

    @GrpcClient("telemetry-service")
    private TelemetryServiceGrpc.TelemetryServiceStub stub;

    private final AtomicBoolean started = new AtomicBoolean(false);

    @Override
    public void run(String... args) {
        if (started.compareAndSet(false, true)) {
            log.info("🚀 gRPC клиент: старт через 20 сек...");
            new Thread(() -> {
                try {
                    Thread.sleep(20000);
                    startStream();
                } catch (Exception e) {
                    log.error("Ошибка: {}", e.getMessage());
                }
            }).start();
        }
    }

    private void startStream() {
        log.info("📡 Подключение к gRPC...");

        TelemetryRequest req = TelemetryRequest.newBuilder()
                .setConstellationName("StarLink")
                .build();

        stub.streamTelemetry(req, new StreamObserver<TelemetryUpdate>() {
            public void onNext(TelemetryUpdate u) {
                log.info("📡 {} | вн:{:.1f}°C вш:{:.1f}°C зар:{:.0f}%",
                        u.getSatelliteName(),
                        u.getInternalTemperature(),
                        u.getExternalTemperature(),
                        u.getBatteryLevel() * 100);
            }
            public void onError(Throwable t) {
                log.warn("gRPC ошибка: {}", t.getMessage());
            }
            public void onCompleted() {
                log.info("gRPC завершен");
            }
        });

        log.info("✅ gRPC стрим запущен");
    }
}