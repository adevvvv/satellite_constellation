package org.example.factory.impl;

import org.example.domains.ImagingSatellite;
import org.example.domains.Satellite;
import org.example.enums.SatelliteType;
import org.example.exception.SpaceOperationException;
import org.example.factory.SatelliteFactory;
import org.example.params.ImagingSatelliteParam;
import org.example.params.SatelliteParam;
import org.springframework.stereotype.Component;

@Component
public class ImagingSatelliteFactory implements SatelliteFactory {

    @Override
    public Satellite createSatelliteWithParameter(SatelliteParam param) {
        if (!(param instanceof ImagingSatelliteParam imageParam)) {
            throw new SpaceOperationException(
                    "ImagingSatelliteFactory ожидает ImagingSatelliteParam, получен: " +
                            param.getClass().getSimpleName()
            );
        }

        return new ImagingSatellite(
                imageParam.getName(),
                imageParam.getBatteryLevel(),
                imageParam.getResolution()
        );
    }

    @Override
    public boolean isSatelliteTypeSupported(SatelliteType type) {
        return type == SatelliteType.IMAGE;
    }
}