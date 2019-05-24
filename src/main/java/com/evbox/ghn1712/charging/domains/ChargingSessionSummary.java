package com.evbox.ghn1712.charging.domains;

public class ChargingSessionSummary {
    private int totalCount;
    private int startedCount;
    private int stoppedCount;

    public ChargingSessionSummary() {
    }

    public ChargingSessionSummary(int totalCount, int startedCount, int stoppedCount) {
        this.totalCount = totalCount;
        this.stoppedCount = stoppedCount;
        this.startedCount = startedCount;
    }

    public int getStoppedCount() {
        return stoppedCount;
    }

    public void setStoppedCount(int stoppedCount) {
        this.stoppedCount = stoppedCount;
    }

    public int getStartedCount() {
        return startedCount;
    }

    public void setStartedCount(int startedCount) {
        this.startedCount = startedCount;
    }

    public int getTotalCount() {
        return totalCount;
    }

    public void setTotalCount(int totalCount) {
        this.totalCount = totalCount;
    }
}
