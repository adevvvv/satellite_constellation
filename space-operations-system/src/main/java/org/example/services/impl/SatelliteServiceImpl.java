package org.example.services.impl;

import lombok.RequiredArgsConstructor;
import org.example.domains.CommunicationSatellite;
import org.example.domains.ImagingSatellite;
import org.example.domains.Satellite;
import org.example.params.CommunicationSatelliteParam;
import org.example.params.ImagingSatelliteParam;
import org.example.params.SatelliteParam;
import org.example.services.SatelliteService;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class SatelliteServiceImpl implements SatelliteService {

    @Override
    public Satellite createSatellite(SatelliteParam param) {
        if (param instanceof CommunicationSatelliteParam commParam) {
            return new CommunicationSatellite(
                    commParam.getName(),
                    commParam.getBatteryLevel(),
                    commParam.getBandwidth()
            );
        } else if (param instanceof ImagingSatelliteParam imgParam) {
            return new ImagingSatellite(
                    imgParam.getName(),
                    imgParam.getBatteryLevel(),
                    imgParam.getResolution()
            );
        }
        throw new IllegalArgumentException("Unknown satellite param type: " + param.getClass());
    }
}