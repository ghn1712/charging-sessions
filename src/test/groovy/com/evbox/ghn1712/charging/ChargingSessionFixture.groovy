package com.evbox.ghn1712.charging

import com.evbox.ghn1712.charging.domains.ChargingSession
import com.evbox.ghn1712.charging.domains.StatusEnum

import java.time.LocalDateTime

class ChargingSessionFixture {

    static def createChargingSession() {
        new ChargingSession(UUID.fromString("d1a92174-ccd5-4f2d-b36b-c2b6fedc8722"), "stationId",
                LocalDateTime.of(2019, 5, 5, 5, 5), StatusEnum.IN_PROGRESS)
    }
}
