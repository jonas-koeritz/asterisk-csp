package opencsp.wbm;

import opencsp.Log;
import opencsp.csta.Provider;
import spark.ModelAndView;
import spark.template.jade.JadeTemplateEngine;

import java.util.HashMap;
import java.util.Map;

import static spark.Spark.*;

public class Wbm {
    private static final String TAG = "Wbm";

    private int port = 8080;
    private Provider provider;
    JadeTemplateEngine jade;


    public void start() {
        port(port);

        Map<String, Object> map = new HashMap<>();
        map.put("sessions", provider.getSessionManager().getSessions());
        map.put("devices", provider.getDevices());
        map.put("calls", provider.getCalls());

        staticFileLocation("/public");
        jade = new JadeTemplateEngine();
        Log.d(TAG, "Jade TemplateLoader: " + jade.configuration().getTemplateLoader().toString());
        get("/", (req, res) -> new ModelAndView(map, "main"), jade);
    }

    public Wbm(int port, Provider provider) {
        this.provider = provider;
        this.port = port;
    }
}
