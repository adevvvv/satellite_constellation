package org.example.services;

import org.example.domains.Satellite;
import org.example.params.SatelliteParam;

public interface SatelliteService {
    Satellite createSatellite(SatelliteParam param);
}