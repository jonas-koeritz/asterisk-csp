package opencsp.wbm;

import spark.ModelAndView;
import spark.template.jade.JadeTemplateEngine;

import java.util.HashMap;
import java.util.Map;

import static spark.Spark.*;

public class Wbm {
    private int port = 8080;

    public void start() {
        port(port);

        Map<String, String> map = new HashMap<>();

        staticFileLocation("/public");
        get("/", (req, res) -> new ModelAndView(map, "main"), new JadeTemplateEngine());
    }

    public Wbm(int port) {
        this.port = port;
    }
}
