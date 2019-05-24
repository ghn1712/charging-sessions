package com.evbox.ghn1712.charging.usecases;

import com.evbox.ghn1712.charging.domains.ChargingSessionCreation;
import com.evbox.ghn1712.charging.domains.StatusEnum;
import com.evbox.ghn1712.charging.domains.ChargingSession;
import com.evbox.ghn1712.charging.gateways.ChargingSessionGateway;

import javax.inject.Inject;
import java.util.UUID;

public class CreateChargingSession {

    private final ChargingSessionGateway gateway;

    @Inject
    public CreateChargingSession(ChargingSessionGateway gateway) {
        this.gateway = gateway;
    }

    public ChargingSession execute(ChargingSessionCreation chargingSessionCreation) {
        ChargingSession chargingSession = new ChargingSession();
        chargingSession.setStatus(StatusEnum.IN_PROGRESS);
        chargingSession.setId(UUID.randomUUID());
        chargingSession.setStartedAt(chargingSessionCreation.getTimestamp());
        chargingSession.setStationId(chargingSessionCreation.getStationId());
        return gateway.createChargingSession(chargingSession);
    }
}
