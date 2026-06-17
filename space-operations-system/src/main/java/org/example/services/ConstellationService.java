package org.example.services;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.aop.LogExecutionTime;
import org.example.domains.Satellite;
import org.example.domains.SatelliteConstellation;
import org.example.repository.SatelliteConstellationRepository;
import org.example.repository.SatelliteRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class ConstellationService {

    private final SatelliteConstellationRepository constellationRepository;
    private final SatelliteRepository satelliteRepository;

    @LogExecutionTime(name = "Создание группировки")
    public SatelliteConstellation createAndSaveConstellation(String name) {
        if (constellationRepository.existsByConstellationName(name)) {
            throw new RuntimeException("Группировка с именем " + name + " уже существует");
        }
        SatelliteConstellation constellation = new SatelliteConstellation(name);
        SatelliteConstellation saved = constellationRepository.save(constellation);
        log.info("Создана группировка: {}", name);
        return saved;
    }

    @LogExecutionTime(name = "Добавление спутника в группировку")
    public void addSatelliteToConstellation(String constellationName, Satellite satellite) {
        SatelliteConstellation constellation = constellationRepository
                .findByConstellationName(constellationName)
                .orElseThrow(() -> new RuntimeException("Группировка не найдена: " + constellationName));

        constellation.addSatellite(satellite);
        constellationRepository.save(constellation);
        log.info("Добавлен спутник {} в {}", satellite.getName(), constellationName);
    }

    @LogExecutionTime(name = "Получение системной сводки", verbose = true)
    @Transactional(readOnly = true)
    public String getSystemOverview() {
        List<SatelliteConstellation> allConstellations = constellationRepository.findAllWithSatellites();
        StringBuilder sb = new StringBuilder("\n=== СИСТЕМНАЯ СВОДКА ===\n");
        sb.append("Всего группировок: ").append(allConstellations.size()).append("\n");

        allConstellations.forEach(cons -> {
            sb.append("\n[группировка ").append(cons.getConstellationName())
                    .append(": спутников ").append(cons.getSatellites().size()).append("]\n");
            cons.getSatellites().forEach(sat ->
                    sb.append("  - ").append(sat.getName())
                            .append(" [").append(sat.isActive() ? "Активен" : "Неактивен")
                            .append("], заряд: ").append((int)(sat.getBatteryLevel()*100)).append("%\n")
            );
        });
        return sb.toString();
    }

    @Transactional(readOnly = true)
    public List<SatelliteConstellation> getAllConstellations() {
        return constellationRepository.findAll();
    }

    @Transactional(readOnly = true)
    public SatelliteConstellation getConstellation(Long id) {
        return constellationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Группировка не найдена: " + id));
    }

    @Transactional(readOnly = true)
    public SatelliteConstellation getConstellation(String name) {
        return constellationRepository.findByConstellationName(name)
                .orElseThrow(() -> new RuntimeException("Группировка не найдена: " + name));
    }

    public SatelliteConstellation updateConstellation(Long id, SatelliteConstellation constellation) {
        SatelliteConstellation existing = getConstellation(id);
        existing.setConstellationName(constellation.getConstellationName());
        return constellationRepository.save(existing);
    }

    public void deleteConstellation(Long id) {
        constellationRepository.deleteById(id);
        log.info("Удалена группировка: {}", id);
    }

    public void deleteConstellation(String name) {
        constellationRepository.deleteByConstellationName(name);
        log.info("Удалена группировка: {}", name);
    }
}