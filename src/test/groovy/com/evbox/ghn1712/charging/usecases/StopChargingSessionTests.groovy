package com.evbox.ghn1712.charging.usecases

import com.evbox.ghn1712.charging.ChargingSessionFixture
import com.evbox.ghn1712.charging.domains.StatusEnum
import com.evbox.ghn1712.charging.gateways.ChargingSessionInMemoryRepository
import spock.lang.Specification

class StopChargingSessionTests extends Specification {

    def gateway = new ChargingSessionInMemoryRepository()
    def stopChargingSession = new StopChargingSession(gateway)

    def "stop charging session that doesn't exist"() {
        given: "charging session exists"
        def chargingSessionCreation = ChargingSessionFixture.createChargingSession()
        gateway.createChargingSession(chargingSessionCreation)
        when: "stopping another charging session"
        def stoppedChargingSession = stopChargingSession.execute(UUID.fromString("491ff76f-b85d-44dc-9d01-b9573c25bfd3"))
        then: "no charging session is stopped"
        !stoppedChargingSession.isPresent()
        !gateway.allChargingSessions.any { session -> session.status == StatusEnum.FINISHED }
    }

    def "stop charging session when session exists"() {
        given: "charging session exists"
        def chargingSessionCreation = ChargingSessionFixture.createChargingSession()
        gateway.createChargingSession(chargingSessionCreation)
        when: "stopping this charging session"
        def stoppedChargingSession = stopChargingSession.execute(UUID.fromString("d1a92174-ccd5-4f2d-b36b-c2b6fedc8722"))
        then: "charging session is stopped"
        stoppedChargingSession.isPresent()
        !gateway.allChargingSessions.any { session -> session.status == StatusEnum.IN_PROGRESS }
    }
}
