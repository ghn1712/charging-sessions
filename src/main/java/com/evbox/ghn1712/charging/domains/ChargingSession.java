package com.evbox.ghn1712.charging.domains;

import java.time.LocalDateTime;
import java.util.UUID;

public class ChargingSession {
    private UUID id;
    private String stationId;
    private LocalDateTime startedAt;
    private StatusEnum status;

    public ChargingSession(){}

    public ChargingSession(UUID id, String stationId, LocalDateTime startedAt, StatusEnum status) {
        this.id = id;
        this.stationId = stationId;
        this.startedAt = startedAt;
        this.status = status;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getStationId() {
        return stationId;
    }

    public void setStationId(String stationId) {
        this.stationId = stationId;
    }

    public LocalDateTime getStartedAt() {
        return startedAt;
    }

    public void setStartedAt(LocalDateTime startedAt) {
        this.startedAt = startedAt;
    }

    public StatusEnum getStatus() {
        return status;
    }

    public void setStatus(StatusEnum status) {
        this.status = status;
    }
}
