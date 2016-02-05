package opencsp.wbm;

import opencsp.csta.Provider;
import spark.ModelAndView;
import spark.template.jade.JadeTemplateEngine;

import java.util.HashMap;
import java.util.Map;

import static spark.Spark.*;

public class Wbm {
    private int port = 8080;
    private Provider provider;

    public void start() {
        port(port);

        Map<String, Object> map = new HashMap<>();
        map.put("sessions", provider.getSessionManager().getSessions());

        staticFileLocation("/public");
        get("/", (req, res) -> new ModelAndView(map, "main"), new JadeTemplateEngine());
    }

    public Wbm(int port, Provider provider) {
        this.provider = provider;
        this.port = port;
    }
}
