package org.example.factory.impl;

import org.example.domains.ImagingSatellite;
import org.example.domains.Satellite;
import org.example.constants.SatelliteConstants;
import org.example.factory.SatelliteFactory;
import org.springframework.stereotype.Component;


@Component
public class ImagingSatelliteFactory implements SatelliteFactory {

    @Override
    public Satellite createSatellite(String name, double batteryLevel) {
        return new ImagingSatellite(name, batteryLevel,
                SatelliteConstants.DEFAULT_IMAGING_RESOLUTION);
    }

    @Override
    public Satellite createSatelliteWithParameter(String name, double batteryLevel, double parameter) {
        return new ImagingSatellite(name, batteryLevel, parameter);
    }
}