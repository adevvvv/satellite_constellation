package org.example.factory.impl;

import org.example.domains.CommunicationSatellite;
import org.example.domains.Satellite;
import org.example.constants.SatelliteConstants;
import org.example.factory.SatelliteFactory;
import org.springframework.stereotype.Component;

@Component
public class CommunicationSatelliteFactory implements SatelliteFactory {

    @Override
    public Satellite createSatellite(String name, double batteryLevel) {
        return new CommunicationSatellite(name, batteryLevel,
                SatelliteConstants.DEFAULT_COMMUNICATION_BANDWIDTH);
    }

    @Override
    public Satellite createSatelliteWithParameter(String name, double batteryLevel, double parameter) {
        return new CommunicationSatellite(name, batteryLevel, parameter);
    }
}