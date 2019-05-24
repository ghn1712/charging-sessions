package com.evbox.ghn1712.charging.controllers;

import com.evbox.ghn1712.charging.domains.ChargingSession;
import com.evbox.ghn1712.charging.domains.ChargingSessionCreation;
import com.evbox.ghn1712.charging.domains.ChargingSessionSummary;
import com.evbox.ghn1712.charging.usecases.CreateChargingSession;
import com.evbox.ghn1712.charging.usecases.GetChargingSessions;
import com.evbox.ghn1712.charging.usecases.StopChargingSession;
import io.javalin.BadRequestResponse;
import io.javalin.HttpResponseException;
import io.javalin.NotFoundResponse;
import org.eclipse.jetty.http.HttpStatus;

import javax.inject.Inject;
import java.util.Collection;
import java.util.Collections;
import java.util.UUID;

public class ChargingSessionController {

    private final CreateChargingSession createChargingSession;
    private final StopChargingSession stopChargingSession;
    private final GetChargingSessions getChargingSessions;


    @Inject
    public ChargingSessionController(CreateChargingSession createChargingSession, StopChargingSession stopChargingSession,
                                     GetChargingSessions getChargingSessions) {
        this.createChargingSession = createChargingSession;
        this.stopChargingSession = stopChargingSession;
        this.getChargingSessions = getChargingSessions;
    }

    public ChargingSession createChargingSession(ChargingSessionCreation chargingSessionCreation) {
        if (chargingSessionCreation.getStationId() == null || chargingSessionCreation.getTimestamp() == null) {
            throw new BadRequestResponse("fields can't be null");
        }
        return createChargingSession.execute(chargingSessionCreation);
    }

    public ChargingSession stopChargingSession(UUID id) {
        return stopChargingSession.execute(id).orElseThrow(() -> new NotFoundResponse("not found"));
    }

    public Collection<ChargingSession> getAllChargingSessions() {
        return getChargingSessions.getAll();
    }

    public ChargingSessionSummary getSummary() {
        return getChargingSessions.getLastMinuteSummary();
    }
}
