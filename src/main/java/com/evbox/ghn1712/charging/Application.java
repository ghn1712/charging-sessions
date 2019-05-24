package com.evbox.ghn1712.charging;

import com.evbox.ghn1712.charging.gateways.ApplicationServer;
import com.evbox.ghn1712.charging.injection.ChargingModule;
import com.google.inject.Guice;
import com.google.inject.Injector;

public class Application {


    public static void main(String[] args) {
        Injector injector = Guice.createInjector(new ChargingModule());
        startApplication(injector);
    }

    public static void startApplication(Injector injector) {
        ApplicationServer.startServer(injector);
    }
}
