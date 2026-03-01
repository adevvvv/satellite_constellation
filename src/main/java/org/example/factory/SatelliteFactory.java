package org.example.factory;

import org.example.domains.Satellite;

public interface SatelliteFactory {
    Satellite createSatellite(String name, double batteryLevel);
    Satellite createSatelliteWithParameter(String name, double batteryLevel, double parameter);
}