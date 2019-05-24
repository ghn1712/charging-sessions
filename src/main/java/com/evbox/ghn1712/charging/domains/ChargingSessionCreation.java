package com.evbox.ghn1712.charging.domains;

import java.time.LocalDateTime;

public class ChargingSessionCreation {
    private String stationId;
    private LocalDateTime timestamp;

    public String getStationId() {
        return stationId;
    }

    public void setStationId(String stationId) {
        this.stationId = stationId;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }
}
