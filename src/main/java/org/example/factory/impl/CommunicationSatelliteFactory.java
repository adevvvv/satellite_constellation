package org.example.factory.impl;

import org.example.domains.CommunicationSatellite;
import org.example.domains.Satellite;
import org.example.enums.SatelliteType;
import org.example.exception.SpaceOperationException;
import org.example.factory.SatelliteFactory;
import org.example.params.CommunicationSatelliteParam;
import org.example.params.SatelliteParam;
import org.springframework.stereotype.Component;

@Component
public class CommunicationSatelliteFactory implements SatelliteFactory {

    @Override
    public Satellite createSatelliteWithParameter(SatelliteParam param) {
        if (!(param instanceof CommunicationSatelliteParam commParam)) {
            throw new SpaceOperationException(
                    "CommunicationSatelliteFactory ожидает CommunicationSatelliteParam, получен: " +
                            param.getClass().getSimpleName()
            );
        }

        return new CommunicationSatellite(
                commParam.getName(),
                commParam.getBatteryLevel(),
                commParam.getBandwidth()
        );
    }

    @Override
    public boolean isSatelliteTypeSupported(SatelliteType type) {
        return type == SatelliteType.COMMUNICATION;
    }
}