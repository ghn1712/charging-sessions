package com.evbox.ghn1712.charging.gateways;

import java.time.LocalDateTime;

public class LocalTimeGateway implements TimeGateway {

    @Override
    public LocalDateTime getNow() {
        return LocalDateTime.now();
    }
}
