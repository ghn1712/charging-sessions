package com.evbox.ghn1712.charging.gateways;

import com.evbox.ghn1712.charging.controllers.ChargingSessionController;
import com.evbox.ghn1712.charging.domains.ChargingSessionCreation;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.inject.Injector;
import io.javalin.Javalin;
import io.javalin.json.JavalinJackson;
import org.eclipse.jetty.http.HttpStatus;

import java.util.UUID;

public class ApplicationServer {

    public static void startServer(Injector injector) {
        ObjectMapper serializer = injector.getInstance(ObjectMapper.class);
        JavalinJackson.configure(serializer);
        Javalin server = Javalin.create().enableCaseSensitiveUrls().start(8080);
        createRoutes(server, injector);
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
