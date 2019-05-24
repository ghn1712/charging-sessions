package com.evbox.ghn1712.charging.controllers

import com.evbox.ghn1712.charging.domains.ChargingSessionCreation
import com.evbox.ghn1712.charging.domains.StatusEnum
import com.evbox.ghn1712.charging.gateways.ChargingSessionInMemoryRepository
import com.evbox.ghn1712.charging.gateways.LocalTimeGateway
import com.evbox.ghn1712.charging.usecases.CreateChargingSession
import com.evbox.ghn1712.charging.usecases.GetChargingSessions
import com.evbox.ghn1712.charging.usecases.StopChargingSession
import io.javalin.BadRequestResponse
import io.javalin.HttpResponseException
import io.javalin.NotFoundResponse
import spock.lang.Specification

class ChargingSessionControllerTests extends Specification {

    def timeGateway = new LocalTimeGateway()
    def gateway = new ChargingSessionInMemoryRepository()
    def getChargingSession = new GetChargingSessions(gateway, timeGateway)
    def stopChargingSession = new StopChargingSession(gateway)
    def createChargingSession = new CreateChargingSession(gateway)
    def chargingSessionController = new ChargingSessionController(createChargingSession, stopChargingSession,
            getChargingSession)

    def "create a charging session"() {
        given: "a charging session"
        def chargingSessionCreation = new ChargingSessionCreation()
        chargingSessionCreation.stationId = "stationId"
        chargingSessionCreation.timestamp = timeGateway.now
        when: "creating charging session"
        def chargingSession = chargingSessionController.createChargingSession(chargingSessionCreation)
        then: "charging session is created successfully"
        getChargingSession.all.size() == 1
        def actual = getChargingSession.all[0]
        chargingSession.id == actual.id
        chargingSessionCreation.stationId == actual.stationId
        chargingSessionCreation.timestamp == actual.startedAt
        StatusEnum.IN_PROGRESS == actual.status
    }

    def "create a invalid charging session"() {
        given: "an invalid charging session"
        def chargingSessionCreation = new ChargingSessionCreation()
        when: "creating this invalid session"
        chargingSessionController.createChargingSession(chargingSessionCreation)
        then: "bad request exception is thrown"
        thrown(BadRequestResponse.class)
    }

    def "stop a charging session"() {
        given: "a charging session is created"
        def chargingSessionCreation = new ChargingSessionCreation()
        chargingSessionCreation.stationId = "stationId"
        chargingSessionCreation.timestamp = timeGateway.now
        def createdChargingSession = chargingSessionController.createChargingSession(chargingSessionCreation)
        when: "stopping this charging session"
        chargingSessionController.stopChargingSession(createdChargingSession.id)
        then: "charging session is stopped"
        def allChargingSessions = chargingSessionController.getAllChargingSessions()
        allChargingSessions.size() == 1
        allChargingSessions[0].status == StatusEnum.FINISHED
    }

    def "stop a charging session that doesn't exist"() {
        given: "a charging session is created"
        def chargingSessionCreation = new ChargingSessionCreation()
        chargingSessionCreation.stationId = "stationId"
        chargingSessionCreation.timestamp = timeGateway.now
        chargingSessionController.createChargingSession(chargingSessionCreation)
        when: "stopping another charging session"
        chargingSessionController.stopChargingSession(UUID.fromString("64a1b25a-812c-4d57-ab22-93297e60f8fb"))
        then: "charging session is stopped"
        thrown(NotFoundResponse.class)
        def allChargingSessions = chargingSessionController.getAllChargingSessions()
        allChargingSessions.size() == 1
        allChargingSessions[0].status == StatusEnum.IN_PROGRESS
    }

    def "get all charging sessions when there are no sessions"() {
        given: "no charging session is created"
        when: "getting all charging sessions"
        def allChargingSessions = chargingSessionController.allChargingSessions
        then:"there are no charging sessions"
        allChargingSessions.isEmpty()
    }

    def "get summary when there are no charging sessions in last minute"() {
        given: "no charging session exists in last minute"
        def chargingSessionCreation = new ChargingSessionCreation()
        chargingSessionCreation.stationId = "stationId"
        chargingSessionCreation.timestamp = timeGateway.now.minusSeconds(61)
        chargingSessionController.createChargingSession(chargingSessionCreation)
        when: "getting summary"
        def summary = chargingSessionController.summary
        then: "total sessions is zero"
        summary.totalCount == 0
    }

    def "get summary when there are two charging sessions in last minute"() {
        given: "there are two charging sessions in last minute"
        def chargingSessionCreation = new ChargingSessionCreation()
        chargingSessionCreation.stationId = "stationId"
        chargingSessionCreation.timestamp = timeGateway.now.minusSeconds(58)
        def chargingSession = chargingSessionController.createChargingSession(chargingSessionCreation)
        chargingSessionCreation.timestamp = timeGateway.now.minusSeconds(59)
        chargingSessionController.createChargingSession(chargingSessionCreation)
        chargingSessionController.stopChargingSession(chargingSession.id)
        when: "getting summary"
        def summary = chargingSessionController.summary
        then: "total sessions is two"
        summary.totalCount == 2
        summary.startedCount == 1
        summary.stoppedCount == 1

    }
}
