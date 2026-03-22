package org.example.factory;

import org.example.domains.Satellite;
import org.example.enums.SatelliteType;
import org.example.params.SatelliteParam;

public interface SatelliteFactory {
    Satellite createSatelliteWithParameter(SatelliteParam param);
    boolean isSatelliteTypeSupported(SatelliteType type);
}