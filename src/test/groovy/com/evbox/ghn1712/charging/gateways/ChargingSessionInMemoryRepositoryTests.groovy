package com.evbox.ghn1712.charging.gateways

import com.evbox.ghn1712.charging.ChargingSessionFixture
import com.evbox.ghn1712.charging.domains.ChargingSession
import com.evbox.ghn1712.charging.domains.StatusEnum
import com.evbox.ghn1712.charging.gateways.ChargingSessionInMemoryRepository
import spock.lang.Specification

import java.time.LocalDateTime

class ChargingSessionInMemoryRepositoryTests extends Specification {

    def repository = new ChargingSessionInMemoryRepository()

    def "insert a charging session"() {
        given: "a charging session"
        def chargingSession = ChargingSessionFixture.createChargingSession()
        when: "inserting new charging session"
        repository.createChargingSession(chargingSession)
        then: "charging session is inserted"
        def getById = repository.getChargingSessionById(chargingSession.id)
        def getByDate = repository.getChargingSessionByStartedAt(chargingSession.startedAt)
        getById.isPresent()
        getByDate.size() == 1
        def chargingSessionByDate = getByDate[0]
        def chargingSessionById = getById.get()
        chargingSessionById.id == chargingSession.id
        chargingSessionByDate.id == chargingSession.id
        chargingSessionById.startedAt == chargingSession.startedAt
        chargingSessionByDate.startedAt == chargingSession.startedAt
        chargingSessionById.stationId == chargingSession.stationId
        chargingSessionByDate.stationId == chargingSession.stationId
        chargingSessionById.status == chargingSession.status
        chargingSessionByDate.status == chargingSession.status
        !repository.getChargingSessionById(UUID.fromString("fcb97b3c-0275-4df3-9969-b49a14a23a65")).isPresent()
        repository.getChargingSessionByStartedAt(LocalDateTime.of(2019, 5, 5, 5,
                4)).isEmpty()
    }

    def "stop a charging session that doesn't exist"() {
        given: "no charging session with id fcb97b3c-0275-4df3-9969-b49a14a23a65 exists"
        repository.createChargingSession(ChargingSessionFixture.createChargingSession())
        when: "stopping charging session with id fcb97b3c-0275-4df3-9969-b49a14a23a65"
        def response = repository.stopChargingSession(UUID.fromString("fcb97b3c-0275-4df3-9969-b49a14a23a65"))
        then: "no charging session is finished"
        !response.isPresent()
        repository.allChargingSessions.size() == 1
        !repository.allChargingSessions.any { session -> session.status == StatusEnum.FINISHED }
    }

    def "stop a charging session that exists"() {
        given: "a charging session started exists"
        def chargingSession = ChargingSessionFixture.createChargingSession()
        repository.createChargingSession(chargingSession)
        when: "stopping this charging session"
        def response = repository.stopChargingSession(chargingSession.id)
        then: "this charging session is finished"
        response.isPresent()
        repository.allChargingSessions.size() == 1
        repository.getChargingSessionById(chargingSession.id).get().status == StatusEnum.FINISHED
        repository.getChargingSessionByStartedAt(chargingSession.startedAt)[0].status == StatusEnum.FINISHED
        !repository.allChargingSessions.any { session -> session.status == StatusEnum.IN_PROGRESS }
    }

    def "get summary when no charging session was created since from date time"() {
        given: "no charging session was created since from date time"
        repository.createChargingSession(ChargingSessionFixture.createChargingSession())
        when: "getting summary from 2019-05-06"
        def summary = repository.getSummary(LocalDateTime.of(2019, 5, 6, 5, 5))
        then: "summary total is zero"
        summary.totalCount == 0
        summary.stoppedCount == 0
        summary.startedCount == 0
    }

    def "get summary when there is one in progress charging session"() {
        given: "one charging session was created since from date time"
        repository.createChargingSession(ChargingSessionFixture.createChargingSession())
        when: "getting summary from 2019-05-05"
        def summary = repository.getSummary(LocalDateTime.of(2019, 5, 5, 5, 5))
        then: "summary total is one"
        summary.totalCount == 1
        and: "summary stopped is zero"
        summary.stoppedCount == 0
        and: "summary started is one"
        summary.startedCount == 1
    }

    def "get summary when there is one finished and another started charging session"() {
        given: "two charging session was created since from date time"
        repository.createChargingSession(ChargingSessionFixture.createChargingSession())
        def chargingSession = new ChargingSession(UUID.fromString("63abf086-0784-4f09-96a7-6be1cfad7200"),
                "anotherStation", LocalDateTime.of(2019, 5, 5, 5, 5),
                StatusEnum.IN_PROGRESS)
        repository.createChargingSession(chargingSession)
        and: "one charging session was finished"
        repository.stopChargingSession(chargingSession.id)
        when: "getting summary from 2019-05-06"
        def summary = repository.getSummary(LocalDateTime.of(2019, 5, 5, 5, 5))
        then: "summary total is two"
        summary.totalCount == 2
        and: "summary stopped is one"
        summary.stoppedCount == 1
        and: "summary started is one"
        summary.startedCount == 1
    }

    def "get summary when there is one finished charging session"() {
        given: "one charging session was created since from date time"
        def chargingSession = new ChargingSession(UUID.fromString("63abf086-0784-4f09-96a7-6be1cfad7200"),
                "anotherStation", LocalDateTime.of(2019, 5, 5, 5, 5),
                StatusEnum.IN_PROGRESS)
        repository.createChargingSession(chargingSession)
        repository.stopChargingSession(chargingSession.id)
        when: "getting summary from 2019-05-06"
        def summary = repository.getSummary(LocalDateTime.of(2019, 5, 5, 5, 5))
        then: "summary total is zero"
        summary.totalCount == 1
        and: "summary stopped is one"
        summary.stoppedCount == 1
        and: "summary started is zero"
        summary.startedCount == 0
    }
}
