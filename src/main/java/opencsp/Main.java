package opencsp;


import opencsp.asterisk.Asterisk;
import opencsp.csta.Provider;
import opencsp.tcp.CSTATcpListener;
import opencsp.wbm.Wbm;

public class Main {
    private static final String TAG = "AsteriskCSP";

    public static void main(String[] args) {
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
        cstaListener.run();
    }
}
