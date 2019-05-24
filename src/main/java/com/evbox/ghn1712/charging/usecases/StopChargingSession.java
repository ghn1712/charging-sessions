package com.evbox.ghn1712.charging.usecases;

import com.evbox.ghn1712.charging.domains.ChargingSession;
import com.evbox.ghn1712.charging.gateways.ChargingSessionGateway;

import javax.inject.Inject;
import java.util.Optional;
import java.util.UUID;

public class StopChargingSession {

    private final ChargingSessionGateway gateway;

    @Inject
    public StopChargingSession(ChargingSessionGateway gateway) {
        this.gateway = gateway;
    }

    public Optional<ChargingSession> execute(UUID id) {
        return gateway.stopChargingSession(id);
    }
}
