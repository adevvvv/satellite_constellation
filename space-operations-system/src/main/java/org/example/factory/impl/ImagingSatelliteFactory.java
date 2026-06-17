package org.example.factory.impl;

import org.example.domains.ImagingSatellite;
import org.example.domains.Satellite;
import org.example.factory.SatelliteFactory;
import org.example.params.ImagingSatelliteParam;
import org.example.params.SatelliteParam;
import org.springframework.stereotype.Component;

@Component
public class ImagingSatelliteFactory implements SatelliteFactory {

    @Override
    public Satellite createSatellite(SatelliteParam param) {
        if (param instanceof ImagingSatelliteParam imgParam) {
            return new ImagingSatellite(
                    imgParam.getName(),
                    imgParam.getBatteryLevel(),
                    imgParam.getResolution()
            );
        }
        throw new IllegalArgumentException("Invalid param type");
    }
}