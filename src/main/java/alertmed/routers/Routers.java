package alertmed.routers;

import alertmed.model.Case;
import alertmed.services.CaseService;
import io.vertx.core.Vertx;
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

import java.util.List;

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
    CaseService caseService;

    @Bean
    public Router mainRouter() {
        Router router = Router.router(vertx);

        router.route().handler(BodyHandler.create().setBodyLimit(50 * MB));
        router.route().handler(LoggerHandler.create());
        router.route().handler(ResponseTimeHandler.create());

        router.route().blockingHandler(ctx -> {
            ctx.put("cases", caseService.list());
            ctx.next();
        });

        router.get("/cazuri/detaliat").blockingHandler(ctx -> {
            Long requestedCaseId = Long.parseLong(ctx.request().getParam("id"));
            List<Case> caseList = caseService.list();

            Case requestedCase = caseList.stream()
                    .filter(aCase -> aCase.getId() == requestedCaseId)
                    .findFirst()
                    .get();


            ctx.put("case", requestedCase);
            ctx.next();
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

}
