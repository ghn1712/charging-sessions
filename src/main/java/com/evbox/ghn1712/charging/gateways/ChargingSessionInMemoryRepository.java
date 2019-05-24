package com.evbox.ghn1712.charging.gateways;

import com.evbox.ghn1712.charging.domains.ChargingSession;
import com.evbox.ghn1712.charging.domains.ChargingSessionSummary;
import com.evbox.ghn1712.charging.domains.StatusEnum;

import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class ChargingSessionInMemoryRepository implements ChargingSessionGateway {

    private final Map<UUID, ChargingSession> uuidMap = new ConcurrentHashMap<>();
    private final SortedMap<LocalDateTime, Map<UUID, ChargingSession>> dateTimeMap = Collections
            .synchronizedSortedMap(new TreeMap<>());

    @Override
    public ChargingSession createChargingSession(ChargingSession chargingSession) {
        uuidMap.put(chargingSession.getId(), chargingSession);
        ConcurrentHashMap<UUID, ChargingSession> chargingSessions = new ConcurrentHashMap<>();
        chargingSessions.put(chargingSession.getId(), chargingSession);
        Map<UUID, ChargingSession> dateTimeInsideMap = dateTimeMap.putIfAbsent(chargingSession.getStartedAt(), chargingSessions);
        if (dateTimeInsideMap != null) dateTimeInsideMap.put(chargingSession.getId(), chargingSession);
        return chargingSession;
    }

    @Override
    public Optional<ChargingSession> stopChargingSession(UUID id) {
        ChargingSession chargingSession = uuidMap.get(id);
        if (chargingSession == null) return Optional.empty();
        chargingSession.setStatus(StatusEnum.FINISHED);
        dateTimeMap.get(chargingSession.getStartedAt()).get(id).setStatus(StatusEnum.FINISHED);
        return Optional.of(uuidMap.get(id));
    }

    @Override
    public Collection<ChargingSession> getAllChargingSessions() {
        return uuidMap.values();
    }

    @Override
    public ChargingSessionSummary getSummary(LocalDateTime fromDateTime) {
        ChargingSessionSummary summary = new ChargingSessionSummary();
        SortedMap<LocalDateTime, Map<UUID, ChargingSession>> chargingSessionsFromDateTime = dateTimeMap
                .tailMap(fromDateTime);
        summary.setTotalCount(getTotal(chargingSessionsFromDateTime));
        int stoppedCount = getFinished(chargingSessionsFromDateTime);
        summary.setStoppedCount(stoppedCount);
        summary.setStartedCount(summary.getTotalCount() - stoppedCount);
        return summary;
    }

    private int getFinished(SortedMap<LocalDateTime, Map<UUID, ChargingSession>> chargingSessionsFromDateTime) {
        return Math.toIntExact(chargingSessionsFromDateTime.values().stream().map(map ->
                map.values().stream().filter(session -> session.getStatus() == StatusEnum.FINISHED).count())
                .reduce(0L, Long::sum));
    }

    private int getTotal(SortedMap<LocalDateTime, Map<UUID, ChargingSession>> chargingSessionsFromDateTime) {
        return chargingSessionsFromDateTime.values().stream().map(Map::size).reduce(0, Integer::sum);
    }

    @Override
    public Optional<ChargingSession> getChargingSessionById(UUID id) {
        return Optional.ofNullable(uuidMap.get(id));
    }

    @Override
    public Collection<ChargingSession> getChargingSessionByStartedAt(LocalDateTime startedAt) {
        Map<UUID, ChargingSession> uuidChargingSessionHashMap = dateTimeMap.get(startedAt);
        if (uuidChargingSessionHashMap == null) return Collections.emptyList();
        return uuidChargingSessionHashMap.values();
    }

    @Override
    public void drop() {
        uuidMap.clear();
        dateTimeMap.clear();
    }
}
