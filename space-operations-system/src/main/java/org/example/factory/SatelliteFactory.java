package org.example.factory;

import org.example.domains.Satellite;
import org.example.params.SatelliteParam;

public interface SatelliteFactory {
    Satellite createSatellite(SatelliteParam param);
}