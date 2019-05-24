package com.evbox.ghn1712.charging.usecases

import com.evbox.ghn1712.charging.ChargingSessionFixture
import com.evbox.ghn1712.charging.domains.ChargingSessionSummary
import com.evbox.ghn1712.charging.domains.StatusEnum
import com.evbox.ghn1712.charging.gateways.ChargingSessionGateway
import com.evbox.ghn1712.charging.gateways.TimeGateway
import spock.lang.Specification

import java.time.LocalDateTime

class GetChargingSessionsUnitTests extends Specification {

    def timeGateway = Mock(TimeGateway)
    def chargingSessionGateway = Mock(ChargingSessionGateway)
    def getChargingSessions = new GetChargingSessions(chargingSessionGateway, timeGateway)

    def "get all charging session when there are no charging sessions"() {
        given: "there is no charging session created"
        chargingSessionGateway.getAllChargingSessions() >> []
        when: "getting all charging sessions"
        def allChargingSessions = getChargingSessions.getAll()
        then: "no charging session is returned"
        allChargingSessions.isEmpty()
    }

    def "get all charging sessions when there are sessions"() {
        given: "there are two charging sessions"
        def finishedChargingSession = ChargingSessionFixture.createChargingSession()
        finishedChargingSession.id = UUID.fromString("f70b895d-0052-499a-bcfc-bdb3d5b2a3d2")
        finishedChargingSession.startedAt = LocalDateTime.of(2019, 5, 5, 4, 4)
        finishedChargingSession.status = StatusEnum.FINISHED
        chargingSessionGateway.getAllChargingSessions() >> [ChargingSessionFixture.createChargingSession(),
                                                            finishedChargingSession]
        when: "getting all charging sessions"
        def allChargingSessions = getChargingSessions.getAll()
        then: "two charging sessions are returned"
        allChargingSessions.size() == 2
        allChargingSessions[0].id == UUID.fromString("d1a92174-ccd5-4f2d-b36b-c2b6fedc8722")
        allChargingSessions[1].id == UUID.fromString("f70b895d-0052-499a-bcfc-bdb3d5b2a3d2")
    }

    def "get last minute summary when there are no charging sessions in last minute"() {
        given: "there are no charging sessions in last minute"
        timeGateway.now >> LocalDateTime.of(2019, 5, 5, 5, 5)
        chargingSessionGateway.getSummary(timeGateway.now.minusMinutes(2)) >>
                new ChargingSessionSummary(2, 1, 1)
        chargingSessionGateway.getSummary(timeGateway.now.minusMinutes(1)) >>
                new ChargingSessionSummary(0, 0, 0)
        when: "getting charging sessions last minute summary"
        def summary = getChargingSessions.getLastMinuteSummary()
        then: "summary total is zero"
        summary.totalCount == 0
        and: "summary total started is zero"
        summary.startedCount == 0
        and: "summary total finished is zero"
        summary.stoppedCount == 0
    }

    def "get last minute summary when there are charging sessions in last minute"() {
        given: "there are two charging sessions in last minute"
        timeGateway.now >> LocalDateTime.of(2019, 5, 5, 5, 5)
        chargingSessionGateway.getSummary(timeGateway.now.minusMinutes(1)) >>
                new ChargingSessionSummary(2, 1, 1)
        when: "getting charging sessions last minute summary"
        def summary = getChargingSessions.getLastMinuteSummary()
        then: "summary total is two"
        summary.totalCount == 2
        and: "summary total started is one"
        summary.startedCount == 1
        and: "summary total finished is one"
        summary.stoppedCount == 1
    }
}
