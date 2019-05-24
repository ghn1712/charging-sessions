package com.evbox.ghn1712.charging.usecases;

import com.evbox.ghn1712.charging.domains.ChargingSession;
import com.evbox.ghn1712.charging.domains.ChargingSessionSummary;
import com.evbox.ghn1712.charging.gateways.ChargingSessionGateway;
import com.evbox.ghn1712.charging.gateways.TimeGateway;

import javax.inject.Inject;
import java.util.Collection;

public class GetChargingSessions {

    private final ChargingSessionGateway gateway;
    private final TimeGateway timeGateway;

    @Inject
    public GetChargingSessions(ChargingSessionGateway gateway, TimeGateway timeGateway) {
        this.gateway = gateway;
        this.timeGateway = timeGateway;
    }


    public Collection<ChargingSession> getAll() {
        return gateway.getAllChargingSessions();
    }

    public ChargingSessionSummary getLastMinuteSummary() {
        return gateway.getSummary(timeGateway.getNow().minusMinutes(1));
    }
}
