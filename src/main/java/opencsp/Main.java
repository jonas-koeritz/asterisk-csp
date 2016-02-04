package opencsp;


import opencsp.tcp.CSTATcpListener;
import opencsp.wbm.Wbm;

public class Main {

    public static void main(String[] args) {
        Wbm wbm = new Wbm(8080);
        wbm.start();

        CSTATcpListener cstaListener = new CSTATcpListener(8800);
        cstaListener.run();
    }
}
