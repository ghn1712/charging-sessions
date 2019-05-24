package com.evbox.ghn1712.charging

import com.evbox.ghn1712.charging.domains.ChargingSession
import com.evbox.ghn1712.charging.domains.ChargingSessionCreation
import com.evbox.ghn1712.charging.domains.ChargingSessionSummary
import com.evbox.ghn1712.charging.domains.StatusEnum
import com.evbox.ghn1712.charging.gateways.ChargingSessionGateway
import com.evbox.ghn1712.charging.injection.ChargingModule
import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.SerializationFeature
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.google.inject.Guice
import com.mashape.unirest.http.Unirest
import org.eclipse.jetty.http.HttpStatus
import spock.lang.Shared
import spock.lang.Specification

import java.time.LocalDateTime

class ApplicationTests extends Specification {

    def serializer = new ObjectMapper()
    def gateway = injector.getInstance(ChargingSessionGateway.class)
    @Shared
    def injector = Guice.createInjector(new ChargingModule())
    @Shared
    def app = Application.startApplication(injector)

    def setup() {
        serializer.registerModule(new JavaTimeModule())
        serializer.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
        gateway.drop()
    }

    def "create a charging session"() {
        given: "a valid charging session creation"
        def chargingSessionCreation = new ChargingSessionCreation()
        chargingSessionCreation.stationId = "stationId"
        chargingSessionCreation.timestamp = LocalDateTime.of(2019, 5, 5, 5, 5)
        when: "creating this charging session"
        def postResponse = Unirest.post("http://localhost:8080/chargingSessions")
                .body(serializer.writeValueAsString(chargingSessionCreation)).asString()
        then: "session is created"
        postResponse.status == HttpStatus.CREATED_201
        def createdChargingSession = serializer.readValue(postResponse.body, ChargingSession.class)
        createdChargingSession.stationId == chargingSessionCreation.stationId
        createdChargingSession.startedAt == chargingSessionCreation.timestamp
        def getResponse = Unirest.get("http://localhost:8080/chargingSessions").asString()
        Collection<ChargingSession> allChargingSessions = serializer.readValue(getResponse.body,
                new TypeReference<Collection<ChargingSession>>() {})
        allChargingSessions.size() == 1
        allChargingSessions[0].stationId == chargingSessionCreation.stationId
        allChargingSessions[0].startedAt == chargingSessionCreation.timestamp
        allChargingSessions[0].status == StatusEnum.IN_PROGRESS
        allChargingSessions[0].id == createdChargingSession.id
    }


    def "get a charging session when there is no session"() {
        given: "no charging session is created"
        when: "getting all charging sessions"
        def response = Unirest.get("http://localhost:8080/chargingSessions").asString()
        then: "charging session list is empty"
        response.status == HttpStatus.OK_200
        Collection<ChargingSession> allChargingSessions = serializer.readValue(response.body,
                new TypeReference<Collection<ChargingSession>>() {})
        allChargingSessions.isEmpty()
    }

    def "stop a charging session when this session exists"() {
        given: "a charging session exists"
        def chargingSessionCreation = new ChargingSessionCreation()
        chargingSessionCreation.stationId = "stationId"
        chargingSessionCreation.timestamp = LocalDateTime.of(2019, 5, 5, 5, 5)
        def postResponse = Unirest.post("http://localhost:8080/chargingSessions")
                .body(serializer.writeValueAsString(chargingSessionCreation)).asString()
        def createdChargingSession = serializer.readValue(postResponse.body, ChargingSession.class)
        when: "stopping this session"
        def stopResponse = Unirest.put("http://localhost:8080/chargingSessions/" + createdChargingSession.id).asString()
        then: "session is stopped successfully"
        stopResponse.status == HttpStatus.OK_200
        def getResponse = Unirest.get("http://localhost:8080/chargingSessions").asString()
        Collection<ChargingSession> allChargingSessions = serializer.readValue(getResponse.body,
                new TypeReference<Collection<ChargingSession>>() {})
        allChargingSessions.size() == 1
        allChargingSessions[0].stationId == chargingSessionCreation.stationId
        allChargingSessions[0].startedAt == chargingSessionCreation.timestamp
        allChargingSessions[0].status == StatusEnum.FINISHED
        allChargingSessions[0].id == createdChargingSession.id
    }

    def "stop a charging session that doesn't exist"() {
        given: "a charging session exists"
        def chargingSessionCreation = new ChargingSessionCreation()
        chargingSessionCreation.stationId = "stationId"
        chargingSessionCreation.timestamp = LocalDateTime.of(2019, 5, 5, 5, 5)
        def postResponse = Unirest.post("http://localhost:8080/chargingSessions")
                .body(serializer.writeValueAsString(chargingSessionCreation)).asString()
        def createdChargingSession = serializer.readValue(postResponse.body, ChargingSession.class)
        when: "stopping another session"
        def stopResponse = Unirest.put("http://localhost:8080/chargingSessions/"
                + "fc195b89-acf1-4e13-827c-c3fc52015b17").asString()
        then: "session is stopped successfully"
        stopResponse.status == HttpStatus.NOT_FOUND_404
        def getResponse = Unirest.get("http://localhost:8080/chargingSessions").asString()
        Collection<ChargingSession> allChargingSessions = serializer.readValue(getResponse.body,
                new TypeReference<Collection<ChargingSession>>() {})
        allChargingSessions.size() == 1
        allChargingSessions[0].stationId == chargingSessionCreation.stationId
        allChargingSessions[0].startedAt == chargingSessionCreation.timestamp
        allChargingSessions[0].status == StatusEnum.IN_PROGRESS
        allChargingSessions[0].id == createdChargingSession.id
    }

    def "get summary when there is no session created in last minute"() {
        given: "a charging session was created more than one minute ago"
        def chargingSessionCreation = new ChargingSessionCreation()
        chargingSessionCreation.stationId = "stationId"
        chargingSessionCreation.timestamp = LocalDateTime.now().minusSeconds(61)
        Unirest.post("http://localhost:8080/chargingSessions").body(serializer
                .writeValueAsString(chargingSessionCreation))
        when: "getting summary"
        def summaryResponse = Unirest.get("http://localhost:8080/chargingSessions/summary").asString()
        then: "summary total is zero"
        summaryResponse.status == HttpStatus.OK_200
        def summary = serializer.readValue(summaryResponse.body, ChargingSessionSummary.class)
        summary.totalCount == 0
        summary.stoppedCount == 0
        summary.startedCount == 0
    }

    def "get summary when there are two sessions created in last minute"() {
        given: "two charging session was created in last minute"
        def chargingSessionCreation = new ChargingSessionCreation()
        chargingSessionCreation.stationId = "stationId"
        chargingSessionCreation.timestamp = LocalDateTime.now().minusSeconds(58)
        Unirest.post("http://localhost:8080/chargingSessions").body(serializer
                .writeValueAsString(chargingSessionCreation)).asString()
        def sessionCreation = new ChargingSessionCreation()
        sessionCreation.stationId = "anotherStation"
        sessionCreation.timestamp = LocalDateTime.now().minusSeconds(59)
        def stopSession = Unirest.post("http://localhost:8080/chargingSessions").body(serializer
                .writeValueAsString(sessionCreation)).asString()
        def stopSessionId = serializer.readValue(stopSession.body, ChargingSession.class).id
        Unirest.put("http://localhost:8080/chargingSessions/" + stopSessionId).asString()
        when: "getting summary"
        def summaryResponse = Unirest.get("http://localhost:8080/chargingSessions/summary").asString()
        then: "summary total is two"
        summaryResponse.status == HttpStatus.OK_200
        def summary = serializer.readValue(summaryResponse.body, ChargingSessionSummary.class)
        summary.totalCount == 2
        summary.stoppedCount == 1
        summary.startedCount == 1
    }
}
