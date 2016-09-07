package opencsp;


import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.core.util.StatusPrinter;
import opencsp.asterisk.Asterisk;
import opencsp.csta.Provider;
import opencsp.csta.tcp.CSTATcpListener;
import opencsp.uacsta.UaCSTAListener;
import opencsp.uacsta.UaCSTAProvider;
import opencsp.util.ConfigurationProvider;
import opencsp.wbm.Wbm;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Main {
    private static final String TAG = "AsteriskCSP";

    public static void main(String[] args) {
        LoggerContext lc = (LoggerContext)LoggerFactory.getILoggerFactory();
        StatusPrinter.print(lc);

        ConfigurationProvider config = new ConfigurationProvider();

        Provider cstaServiceProvider = Provider.getInstance(config);

        Wbm wbm = new Wbm(8080, cstaServiceProvider);
        wbm.start();

        try {
            Asterisk asterisk = new Asterisk(
                    config.getConfigurationValue("asterisk_host"),
                    config.getConfigurationValue("asterisk_user"),
                    config.getConfigurationValue("asterisk_pass"),
                    cstaServiceProvider);

            cstaServiceProvider.setAsterisk(asterisk);
        } catch (Exception ex) {
            Log.e(TAG, ex.getMessage());
            return;
        }
        CSTATcpListener cstaListener = new CSTATcpListener(8800, cstaServiceProvider);

        Runnable cstaListenerThread = new Runnable() {
            public void run() {
                cstaListener.startListening();
                Log.d(TAG, "cstaListener returned.");
            }
        };


        ExecutorService listenerExecutor = Executors.newCachedThreadPool();
        listenerExecutor.submit(cstaListenerThread);
    }
}
