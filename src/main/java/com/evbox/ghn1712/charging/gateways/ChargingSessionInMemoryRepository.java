package com.evbox.ghn1712.charging.gateways;

import com.evbox.ghn1712.charging.domains.ChargingSession;
import com.evbox.ghn1712.charging.domains.ChargingSessionSummary;
import com.evbox.ghn1712.charging.domains.StatusEnum;

import java.time.LocalDateTime;
import java.util.*;

public class ChargingSessionInMemoryRepository implements ChargingSessionGateway {

    private final Map<UUID, ChargingSession> uuidMap = Collections.synchronizedMap(new HashMap<>());
    private final SortedMap<LocalDateTime, ChargingSession> dateTimeMap = Collections
            .synchronizedSortedMap(new TreeMap<>());

    @Override
    public ChargingSession createChargingSession(ChargingSession chargingSession) {
        uuidMap.put(chargingSession.getId(), chargingSession);
        dateTimeMap.put(chargingSession.getStartedAt(), chargingSession);
        return chargingSession;
    }

    @Override
    public Optional<ChargingSession> stopChargingSession(UUID id) {
        ChargingSession chargingSession = uuidMap.get(id);
        if (chargingSession == null) return Optional.empty();
        chargingSession.setStatus(StatusEnum.FINISHED);
        dateTimeMap.get(chargingSession.getStartedAt()).setStatus(StatusEnum.FINISHED);
        return Optional.of(uuidMap.get(id));
    }

    @Override
    public Collection<ChargingSession> getAllChargingSessions() {
        return uuidMap.values();
    }

    @Override
    public ChargingSessionSummary getSummary(LocalDateTime fromDateTime) {
        ChargingSessionSummary summary = new ChargingSessionSummary();
        SortedMap<LocalDateTime, ChargingSession> chargingSessionsFromDateTime = dateTimeMap.tailMap(fromDateTime);
        summary.setTotalCount(chargingSessionsFromDateTime.size());
        int stoppedCount = Math.toIntExact(chargingSessionsFromDateTime.values().stream()
                .filter(chargingSession -> chargingSession.getStatus().equals(StatusEnum.FINISHED)).count());
        summary.setStoppedCount(stoppedCount);
        summary.setStartedCount(summary.getTotalCount() - stoppedCount);
        return summary;
    }

    @Override
    public Optional<ChargingSession> getChargingSessionById(UUID id) {
        return Optional.ofNullable(uuidMap.get(id));
    }

    @Override
    public Optional<ChargingSession> getChargingSessionByStartedAt(LocalDateTime startedAt) {
        return Optional.ofNullable(dateTimeMap.get(startedAt));
    }

    @Override
    public void drop() {
        uuidMap.clear();
        dateTimeMap.clear();
    }
}
