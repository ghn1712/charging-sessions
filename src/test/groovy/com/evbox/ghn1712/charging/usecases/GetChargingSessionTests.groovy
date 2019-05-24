package com.evbox.ghn1712.charging.usecases

import com.evbox.ghn1712.charging.ChargingSessionFixture
import com.evbox.ghn1712.charging.gateways.ChargingSessionInMemoryRepository
import com.evbox.ghn1712.charging.gateways.LocalTimeGateway
import spock.lang.Specification

class GetChargingSessionTests extends Specification {

    def timeGateway = new LocalTimeGateway()
    def gateway = new ChargingSessionInMemoryRepository()
    def getChargingSession = new GetChargingSessions(gateway, timeGateway)

    def "get all charging sessions when there are sessions"() {
        given: "sessions are created"
        gateway.createChargingSession(ChargingSessionFixture.createChargingSession())
        when: "getting all sessions"
        def allChargeingSessions = getChargingSession.getAll()
        then: "there is one charging session"
        allChargeingSessions.size() == 1
    }

    def "get all charging session when there is no session"() {
        given: "no charging session"
        when: "getting all charging sessions"
        def allChargingSessions = getChargingSession.getAll()
        then: "there are no charging sessions"
        allChargingSessions.isEmpty()
    }

    def "get charging session summary when there is no session"() {
        given: "no charging sessions"
        when: "getting charging session summary"
        def summary = getChargingSession.getLastMinuteSummary()
        then: "total charging sessions is zero"
        summary.totalCount == 0
        and: "total in progress charging sessions is zero"
        summary.startedCount == 0
        and: "total finished charging sessions is zero"
        summary.stoppedCount == 0
    }

    def "get charging session summary when there are sessions"() {
        given: "there are charging sessions"
        def chargingSession = ChargingSessionFixture.createChargingSession()
        def now = timeGateway.now
        chargingSession.startedAt = now
        gateway.createChargingSession(chargingSession)
        def anotherChargingSession = ChargingSessionFixture.createChargingSession()
        anotherChargingSession.startedAt = now
        anotherChargingSession.id = UUID.fromString("8fc68a9b-d6b1-4fd4-a8b8-106bdb4169b5")
        gateway.createChargingSession(anotherChargingSession)
        gateway.stopChargingSession(UUID.fromString("d1a92174-ccd5-4f2d-b36b-c2b6fedc8722"))
        when: "getting charging session summary"
        def summary = getChargingSession.getLastMinuteSummary()
        then: "total count is two"
        summary.totalCount == 2
        and: "finished count is one"
        summary.stoppedCount == 1
        and: "in progress count is one"
        summary.startedCount == 1
    }

}
