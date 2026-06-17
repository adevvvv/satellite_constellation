package org.example.service;

import io.grpc.stub.StreamObserver;
import lombok.extern.slf4j.Slf4j;
import net.devh.boot.grpc.server.service.GrpcService;
import org.example.telemetry.TelemetryServiceGrpc;
import org.example.telemetry.TelemetryRequest;
import org.example.telemetry.TelemetryUpdate;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@GrpcService
@Slf4j
public class TelemetryGrpcService extends TelemetryServiceGrpc.TelemetryServiceImplBase {

    private final Random random = new Random();
    private final List<SatelliteSimulator> satellites = new ArrayList<>();

    public TelemetryGrpcService() {
        // Инициализация симулированных спутников
        satellites.add(new SatelliteSimulator("CommSat-1", "COMMUNICATION", 0.9));
        satellites.add(new SatelliteSimulator("ImgSat-1", "IMAGING", 0.85));
        satellites.add(new SatelliteSimulator("NavSat-1", "COMMUNICATION", 0.95));
    }

    @Override
    public void streamTelemetry(TelemetryRequest request, StreamObserver<TelemetryUpdate> responseObserver) {
        log.info("📡 Начат стриминг телеметрии для группировки: {}", request.getConstellationName());

        ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor();

        executor.scheduleAtFixedRate(() -> {
            try {
                for (SatelliteSimulator satellite : satellites) {
                    // Обновляем состояние спутника
                    satellite.updateState();

                    TelemetryUpdate update = TelemetryUpdate.newBuilder()
                            .setSatelliteName(satellite.getName())
                            .setSatelliteType(satellite.getType())
                            .setInternalTemperature(satellite.getInternalTemp())
                            .setExternalTemperature(satellite.getExternalTemp())
                            .setBatteryLevel(satellite.getBatteryLevel())
                            .setIsActive(satellite.isActive())
                            .setTimestamp(System.currentTimeMillis())
                            .build();

                    responseObserver.onNext(update);
                    log.debug("📊 Отправлена телеметрия для {}: внутр.темп={}°C, внеш.темп={}°C, заряд={}%",
                            satellite.getName(),
                            String.format("%.1f", satellite.getInternalTemp()),
                            String.format("%.1f", satellite.getExternalTemp()),
                            String.format("%.1f", satellite.getBatteryLevel() * 100));
                }
            } catch (Exception e) {
                log.error("❌ Ошибка при отправке телеметрии: {}", e.getMessage());
                responseObserver.onError(e);
                executor.shutdown();
            }
        }, 0, 2, TimeUnit.SECONDS);

        // Завершаем стрим при отмене
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            responseObserver.onCompleted();
            executor.shutdown();
            log.info("📡 Стриминг телеметрии завершен");
        }));
    }

    // Внутренний класс для симуляции спутника
    private static class SatelliteSimulator {
        private final String name;
        private final String type;
        private double batteryLevel;
        private double internalTemp = 25.0;
        private double externalTemp = -50.0;
        private boolean active = true;
        private final Random random = new Random();

        public SatelliteSimulator(String name, String type, double batteryLevel) {
            this.name = name;
            this.type = type;
            this.batteryLevel = batteryLevel;
        }

        public void updateState() {
            // Симулируем изменения температуры
            internalTemp += (random.nextDouble() - 0.5) * 2.0;
            internalTemp = Math.max(15.0, Math.min(35.0, internalTemp));

            externalTemp += (random.nextDouble() - 0.5) * 3.0;
            externalTemp = Math.max(-100.0, Math.min(-20.0, externalTemp));

            // Симулируем расход батареи
            batteryLevel -= random.nextDouble() * 0.001;
            batteryLevel = Math.max(0.0, batteryLevel);

            // Случайно меняем активность
            if (random.nextDouble() < 0.01) {
                active = !active;
            }
        }

        public String getName() { return name; }
        public String getType() { return type; }
        public double getBatteryLevel() { return batteryLevel; }
        public double getInternalTemp() { return internalTemp; }
        public double getExternalTemp() { return externalTemp; }
        public boolean isActive() { return active; }
    }
}