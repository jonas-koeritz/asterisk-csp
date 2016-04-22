package opencsp;


import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.core.util.StatusPrinter;
import opencsp.asterisk.Asterisk;
import opencsp.csta.Provider;
import opencsp.csta.tcp.CSTATcpListener;
import opencsp.uacsta.UaCSTAListener;
import opencsp.uacsta.UaCSTAProvider;
import opencsp.wbm.Wbm;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Main {
    private static final String TAG = "AsteriskCSP";

    public static void main(String[] args) {
        LoggerContext lc = (LoggerContext)LoggerFactory.getILoggerFactory();
        StatusPrinter.print(lc);

        Provider cstaServiceProvider = Provider.getInstance("49", "4121", "2336");

        Wbm wbm = new Wbm(8080, cstaServiceProvider);
        wbm.start();

        try {
            Asterisk asterisk = new Asterisk("192.168.55.75", "cti", "cti", cstaServiceProvider);
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


        UaCSTAProvider uaCSTAProvider = new UaCSTAProvider();
        UaCSTAListener uaCSTAListener = new UaCSTAListener(6060, uaCSTAProvider);
        cstaServiceProvider.setUaCstaProvider(uaCSTAProvider);

        Runnable uaCstaListenerThread = new Runnable() {
            public void run() {
                uaCSTAListener.startListening();
                Log.d(TAG, "uaCstaListener returned.");
            }
        };

        ExecutorService listenerExecutor = Executors.newCachedThreadPool();
        listenerExecutor.submit(cstaListenerThread);
        listenerExecutor.submit(uaCstaListenerThread);
    }
}
