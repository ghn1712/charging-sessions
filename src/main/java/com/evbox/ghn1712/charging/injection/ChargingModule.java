package com.evbox.ghn1712.charging.injection;

import com.evbox.ghn1712.charging.controllers.ChargingSessionController;
import com.evbox.ghn1712.charging.gateways.ChargingSessionGateway;
import com.evbox.ghn1712.charging.gateways.ChargingSessionInMemoryRepository;
import com.evbox.ghn1712.charging.gateways.LocalTimeGateway;
import com.evbox.ghn1712.charging.gateways.TimeGateway;
import com.evbox.ghn1712.charging.usecases.CreateChargingSession;
import com.evbox.ghn1712.charging.usecases.GetChargingSessions;
import com.evbox.ghn1712.charging.usecases.StopChargingSession;
import com.google.inject.AbstractModule;
import com.google.inject.Scopes;

public class ChargingModule extends AbstractModule {

    @Override
    protected void configure() {
        bind(ChargingSessionController.class);
        bind(GetChargingSessions.class);
        bind(CreateChargingSession.class);
        bind(StopChargingSession.class);
        bind(ChargingSessionGateway.class).to(ChargingSessionInMemoryRepository.class).in(Scopes.SINGLETON);
        bind(TimeGateway.class).to(LocalTimeGateway.class);
    }
}
