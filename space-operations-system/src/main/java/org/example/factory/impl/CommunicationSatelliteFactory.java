package org.example.factory.impl;

import org.example.domains.CommunicationSatellite;
import org.example.domains.Satellite;
import org.example.factory.SatelliteFactory;
import org.example.params.CommunicationSatelliteParam;
import org.example.params.SatelliteParam;
import org.springframework.stereotype.Component;

@Component
public class CommunicationSatelliteFactory implements SatelliteFactory {

    @Override
    public Satellite createSatellite(SatelliteParam param) {
        if (param instanceof CommunicationSatelliteParam commParam) {
            return new CommunicationSatellite(
                    commParam.getName(),
                    commParam.getBatteryLevel(),
                    commParam.getBandwidth()
            );
        }
        throw new IllegalArgumentException("Invalid param type");
    }
}