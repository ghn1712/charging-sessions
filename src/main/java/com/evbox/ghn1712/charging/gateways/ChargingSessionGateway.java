package com.evbox.ghn1712.charging.gateways;

import com.evbox.ghn1712.charging.domains.ChargingSession;
import com.evbox.ghn1712.charging.domains.ChargingSessionSummary;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Optional;
import java.util.UUID;

public interface ChargingSessionGateway {
    ChargingSession createChargingSession(ChargingSession chargingSession);
    Optional<ChargingSession> stopChargingSession(UUID id);
    Collection<ChargingSession> getAllChargingSessions();
    ChargingSessionSummary getSummary(LocalDateTime fromDateTime);
    Optional<ChargingSession> getChargingSessionById(UUID id);
    Optional<ChargingSession> getChargingSessionByStartedAt(LocalDateTime startedAt);
    void drop();
}
