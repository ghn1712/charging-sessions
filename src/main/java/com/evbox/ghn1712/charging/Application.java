package com.evbox.ghn1712.charging;

import com.evbox.ghn1712.charging.controllers.ChargingSessionController;
import com.evbox.ghn1712.charging.domains.ChargingSessionCreation;
import com.evbox.ghn1712.charging.injection.ChargingModule;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.google.inject.Guice;
import com.google.inject.Injector;
import io.javalin.Javalin;
import io.javalin.json.JavalinJackson;
import org.eclipse.jetty.http.HttpStatus;

import java.util.UUID;

public class Application {


    public static void main(String[] args) {
        Injector injector = Guice.createInjector(new ChargingModule());
        startApplication(injector);

    }

    public static void startApplication(Injector injector) {
        Javalin server = buildServer();
        createRoutes(server, injector);
    }

    private static Javalin buildServer() {
        ObjectMapper serializer = new ObjectMapper();
        serializer.registerModule(new JavaTimeModule());
        serializer.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        JavalinJackson.configure(serializer);
        return Javalin.create().enableCaseSensitiveUrls().start(8080);
    }

    private static void createRoutes(Javalin server, Injector injector) {
        ChargingSessionController controller = injector.getInstance(ChargingSessionController.class);
        server.post("/chargingSessions", ctx -> ctx.json(controller
                .createChargingSession(ctx.bodyAsClass(ChargingSessionCreation.class))).status(HttpStatus.CREATED_201));
        server.get("/chargingSessions", ctx -> ctx.json(controller.getAllChargingSessions()));
        server.get("/chargingSessions/summary", ctx -> ctx.json(controller.getSummary()));
        server.put("/chargingSessions/:id", ctx -> ctx.json(controller
                .stopChargingSession(UUID.fromString(ctx.pathParam("id")))));

    }
}
