import io.vertx.core.AbstractVerticle;
import io.vertx.core.Vertx;
import io.vertx.core.http.HttpHeaders;
import io.vertx.core.http.impl.VertxHttp2ClientUpgradeCodec;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.handler.BodyHandler;
import io.vertx.ext.web.handler.StaticHandler;
import io.vertx.ext.web.handler.TemplateHandler;
import io.vertx.ext.web.templ.HandlebarsTemplateEngine;
import io.vertx.ext.web.templ.TemplateEngine;

public class Main extends AbstractVerticle {
    private static int port = 8080;


    public void start() throws Exception {
        Vertx vertx = Vertx.vertx();
        Router router = Router.router(vertx);
        HandlebarsTemplateEngine engine = HandlebarsTemplateEngine.create();
        engine.setMaxCacheSize(0);

        // server up handle bar templates from the templates folder
        TemplateHandler handler = TemplateHandler.create(engine);
        router.getWithRegex(".+\\.hbs").handler(handler);

        // serve up static assets
        router.route("/assets/*").handler(StaticHandler.create("assets"));



        // Enable multipart form data parsing
        router.route().handler(BodyHandler.create());

        router.route("/").handler(routingContext -> {
            routingContext.response().putHeader("content-type", "text/html").end(
                    "<form action=\"/form\" method=\"post\">\n" +
                            "    <div>\n" +
                            "        <label for=\"name\">Enter your name:</label>\n" +
                            "        <input type=\"text\" id=\"name\" name=\"name\" />\n" +
                            "    </div>\n" +
                            "    <div class=\"button\">\n" +
                            "        <button type=\"submit\">Send</button>\n" +
                            "    </div>" +
                            "</form>"
            );
        });

        // handle the form
        router.post("/form").handler(ctx -> {
            ctx.response().putHeader(HttpHeaders.CONTENT_TYPE, "text/plain");
            // note the form attribute matches the html form element name.
            ctx.response().end("Hello " + ctx.request().getParam("name") + "!");
        });

        vertx.createHttpServer().requestHandler(router::accept).listen(port);
        System.out.println("Server up and running on http://localhost:8080");
    }


    public static void main(String[] args) throws  Exception  {
        Main main = new Main();
        main.start();
    }
}
