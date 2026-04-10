package org.example.services.impl;

import org.example.domains.Satellite;
import org.example.exception.SpaceOperationException;
import org.example.factory.SatelliteFactory;
import org.example.params.SatelliteParam;
import org.example.services.SatelliteService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SatelliteServiceImpl implements SatelliteService {

    private final List<SatelliteFactory> factories;

    public SatelliteServiceImpl(List<SatelliteFactory> factories) {
        this.factories = factories;
    }

    @Override
    public Satellite createSatellite(SatelliteParam param) {
        SatelliteFactory selectedFactory = factories.stream()
                .filter(factory -> factory.isSatelliteTypeSupported(param.getType()))
                .findFirst()
                .orElseThrow(() -> new SpaceOperationException(
                        "Не найдена фабрика для типа спутника: " + param.getType()
                ));

        return selectedFactory.createSatelliteWithParameter(param);
    }
}