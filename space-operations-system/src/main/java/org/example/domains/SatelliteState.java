package org.example.domains;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
@AllArgsConstructor
public class SatelliteState {
    private boolean isActive;
    private String statusMessage;

    public String getStatus() {
        return statusMessage;
    }
}