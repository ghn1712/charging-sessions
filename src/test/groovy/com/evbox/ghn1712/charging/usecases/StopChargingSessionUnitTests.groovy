package com.evbox.ghn1712.charging.usecases

import com.evbox.ghn1712.charging.ChargingSessionFixture
import com.evbox.ghn1712.charging.domains.StatusEnum
import com.evbox.ghn1712.charging.gateways.ChargingSessionGateway
import spock.lang.Specification

class StopChargingSessionUnitTests extends Specification {

    def gateway = Mock(ChargingSessionGateway)
    def stopChargingSession = new StopChargingSession(gateway)

    def "stop charging session that doesn't exist"() {
        given: "charging session doesn't exist"
        gateway.stopChargingSession(UUID.fromString("a43a68b2-6eda-47c2-9ce9-36373fe8016e")) >> Optional.empty()
        when: "stopping charging session"
        def chargingSession = stopChargingSession.execute(UUID.fromString("a43a68b2-6eda-47c2-9ce9-36373fe8016e"))
        then: "charging session is not stopped"
        !chargingSession.isPresent()
    }

    def "stop charging session when session exists"() {
        given: "charging session exists"
        def chargingSession = ChargingSessionFixture.createChargingSession()
        chargingSession.status = StatusEnum.FINISHED
        gateway.stopChargingSession(UUID.fromString("d1a92174-ccd5-4f2d-b36b-c2b6fedc8722")) >>
                Optional.of(chargingSession)
        when: "stopping charging session"
        def stoppedChargingSesison = stopChargingSession.execute(UUID.fromString("d1a92174-ccd5-4f2d-b36b-c2b6fedc8722"))
        then: "charging session is stopped"
        stoppedChargingSesison.isPresent()
        stoppedChargingSesison.get().status == StatusEnum.FINISHED
        stoppedChargingSesison.get().id == UUID.fromString("d1a92174-ccd5-4f2d-b36b-c2b6fedc8722")
        stoppedChargingSesison.get().stationId == chargingSession.stationId
        stoppedChargingSesison.get().startedAt == chargingSession.startedAt
    }
}
