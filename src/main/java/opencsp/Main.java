package opencsp;


import opencsp.tcp.CstaTcpListener;
import opencsp.wbm.Wbm;

public class Main {

    public static void main(String[] args) {
        Wbm wbm = new Wbm(8080);
        wbm.start();

        CstaTcpListener cstaListener = new CstaTcpListener(8800);
        cstaListener.run();
    }
}
