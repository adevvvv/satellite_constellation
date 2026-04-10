package org.example.requests;

import org.example.params.SatelliteParam;

import java.util.List;

/**
 * Запрос на добавление спутников в группировку
 */
public record AddSatelliteRequest(
        String constellationName,
        List<SatelliteParam> satelliteParams
) {
    public AddSatelliteRequest {
        if (constellationName == null || constellationName.isBlank()) {
            throw new IllegalArgumentException("Имя группировки не может быть пустым");
        }
        if (satelliteParams == null || satelliteParams.isEmpty()) {
            throw new IllegalArgumentException("Список спутников не может быть пустым");
        }
    }
}