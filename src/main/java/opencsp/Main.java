package opencsp;


import opencsp.csta.Provider;
import opencsp.tcp.CSTATcpListener;
import opencsp.wbm.Wbm;

public class Main {

    public static void main(String[] args) {
        Provider cstaServiceProvider = new Provider();

        Wbm wbm = new Wbm(8080);
        wbm.start();

        CSTATcpListener cstaListener = new CSTATcpListener(8800, cstaServiceProvider);
        cstaListener.run();
    }
}
