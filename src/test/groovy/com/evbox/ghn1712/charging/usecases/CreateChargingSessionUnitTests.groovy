package com.evbox.ghn1712.charging.usecases


import com.evbox.ghn1712.charging.domains.ChargingSessionCreation
import com.evbox.ghn1712.charging.domains.StatusEnum
import com.evbox.ghn1712.charging.gateways.ChargingSessionGateway
import spock.lang.Specification

import java.time.LocalDateTime

class CreateChargingSessionUnitTests extends Specification {

    def gateway = Mock(ChargingSessionGateway)
    def createChargingSession = new CreateChargingSession(gateway)

    def "create a charging session"() {
        given: "a new valid session creation"
        def chargingSession
        def sessionCreation = new ChargingSessionCreation()
        sessionCreation.stationId = "stationId"
        sessionCreation.timestamp = LocalDateTime.of(2019, 5, 5, 5, 5)
        gateway.createChargingSession(_) >> { args ->
            chargingSession = args[0]
            chargingSession
        }
        when: "creating a charging session"
        def actualChargingSession = createChargingSession.execute(sessionCreation)
        then: "charging session is created"
        actualChargingSession.id == chargingSession.id
        actualChargingSession.stationId == "stationId"
        actualChargingSession.startedAt == LocalDateTime.of(2019, 5, 5, 5, 5)
        actualChargingSession.status == StatusEnum.IN_PROGRESS
    }
}
