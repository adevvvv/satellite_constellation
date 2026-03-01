package org.example.repository;

import org.example.domains.SatelliteConstellation;
import org.springframework.stereotype.Repository;
import java.util.HashMap;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;

@Repository
@Slf4j
public class ConstellationRepository {
    private final Map<String, SatelliteConstellation> constellations = new HashMap<>();

    public void addConstellation(SatelliteConstellation constellation) {
        constellations.put(constellation.getConstellationName(), constellation);
        log.info("Сохранена группировка: {}", constellation.getConstellationName());
    }

    public SatelliteConstellation getConstellation(String name) {
        SatelliteConstellation constellation = constellations.get(name);
        if (constellation == null) {
            throw new RuntimeException("Группировка не найдена: " + name);
        }
        return constellation;
    }

    public Map<String, SatelliteConstellation> getAllConstellations() {
        return new HashMap<>(constellations);
    }

    public boolean containsConstellation(String name) {
        return constellations.containsKey(name);
    }

    public void removeConstellation(String name) {
        constellations.remove(name);
        log.info("Удалена группировка: {}", name);
    }

    public void updateConstellation(SatelliteConstellation constellation) {
        if (constellations.containsKey(constellation.getConstellationName())) {
            constellations.put(constellation.getConstellationName(), constellation);
            log.info("Обновлена группировка: {}", constellation.getConstellationName());
        } else {
            throw new RuntimeException("Группировка не найдена для обновления: " +
                    constellation.getConstellationName());
        }
    }

    public void clear() {
        constellations.clear();
        log.info("Репозиторий очищен");
    }
}