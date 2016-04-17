package alertmed.routers;

import alertmed.model.Alert;
import alertmed.services.AlertsService;
import io.vertx.core.Vertx;
import io.vertx.core.http.HttpHeaders;
import io.vertx.core.http.HttpServerRequest;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.handler.BodyHandler;
import io.vertx.ext.web.handler.LoggerHandler;
import io.vertx.ext.web.handler.ResponseTimeHandler;
import io.vertx.ext.web.handler.StaticHandler;
import io.vertx.ext.web.handler.TemplateHandler;
import io.vertx.ext.web.templ.ThymeleafTemplateEngine;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Configuration
public class Routers {

    public static final String PREFIX = "/_alertmed";
    private static final int KB = 1024;
    private static final int MB = 1024 * KB;
    @Autowired
    Vertx vertx;

    @Autowired
    ThymeleafTemplateEngine thymeleafTemplateEngine;

    @Value("${ALERTMED_HOME}")
    String homeDirectory;

    @Autowired
    AlertsService caseService;

    @Bean
    public Router mainRouter() {
        Router router = Router.router(vertx);

        router.route().handler(BodyHandler.create().setBodyLimit(50 * MB));
        router.route().handler(LoggerHandler.create());
        router.route().handler(ResponseTimeHandler.create());

        router.route().blockingHandler(ctx -> {
            List<Alert> alerts = caseService.list().stream()
                    .sorted((o1, o2) -> o2.getSubmitTime().compareTo(o1.getSubmitTime()))
                    .collect(Collectors.toList());
            ctx.put("alerts", alerts);
            ctx.next();
        });

        router.get("/cazuri/detaliat").blockingHandler(ctx -> {
            Long requestedCaseId = Long.parseLong(ctx.request().getParam("id"));
            List<Alert> alertList = caseService.list();

            Alert requestedAlert = alertList.stream()
                    .filter(aCase -> aCase.getId() == requestedCaseId)
                    .findFirst()
                    .get();

            ctx.put("case", requestedAlert);
            ctx.next();
        });

        router.post("/cazuri").blockingHandler(ctx -> {
            Alert alert = buildFromRequest(ctx.request());
            caseService.addAlert(alert);
            ctx.response()
                    .putHeader(HttpHeaders.LOCATION, "/")
                    .setStatusCode(303)
                    .end();
        });

        router.route("/static/*")
                .handler(StaticHandler.create()
                        .setAllowRootFileSystemAccess(true)
                        .setWebRoot("webroot")
                        .setIndexPage("index.html")
                        .setCachingEnabled(false));

        router.route().handler(TemplateHandler.create(thymeleafTemplateEngine, "", "text/html"));
        return router;
    }

    private Alert buildFromRequest(HttpServerRequest request) {
        return Alert.builder()
                .id(caseService.size() + 1)
                .name(request.getParam("name"))
                .email(request.getParam("email"))
                .location(request.getParam("location"))
                .phone(request.getParam("phone"))
                .problem(request.getParam("problem"))
                .submitTime(LocalDateTime.now())
                .build();
    }

}
