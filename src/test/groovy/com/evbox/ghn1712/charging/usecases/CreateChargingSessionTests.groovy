package com.evbox.ghn1712.charging.usecases

import com.evbox.ghn1712.charging.domains.ChargingSessionCreation
import com.evbox.ghn1712.charging.domains.StatusEnum
import com.evbox.ghn1712.charging.gateways.ChargingSessionInMemoryRepository
import spock.lang.Specification

import java.time.LocalDateTime

class CreateChargingSessionTests extends Specification {

    def gateway = new ChargingSessionInMemoryRepository()
    def createChargingSession = new CreateChargingSession(gateway)

    def "create a charging session"() {
        given: "a new valid session"
        def sessionCreation = new ChargingSessionCreation()
        sessionCreation.stationId = "stationId"
        sessionCreation.timestamp = LocalDateTime.of(2019, 5, 5, 5, 5)
        when: "creating a charging session"
        def chargingSession = createChargingSession.execute(sessionCreation)
        then: "session is created successfully"
        def allChargingSessions = gateway.allChargingSessions
        allChargingSessions.size() == 1
        chargingSession.id == allChargingSessions[0].id
        chargingSession.stationId == sessionCreation.stationId
        chargingSession.startedAt == sessionCreation.timestamp
        chargingSession.status == StatusEnum.IN_PROGRESS

    }
}
