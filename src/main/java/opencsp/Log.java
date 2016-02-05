package opencsp;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Log {
    private static DateFormat dateFormat = new SimpleDateFormat("[yyyy/MM/dd HH:mm:ss.SSS]");

    public static void e(String tag, String message) {
        System.err.println(dateFormat.format(new Date()) + "\tERROR\t" + tag + "\t" + message);
    }

    public static void d(String tag, String message) {
        System.out.println(dateFormat.format(new Date()) + "\tDEBUG\t" + tag + "\t" + message);
    }

    public static void i(String tag, String message) {
        System.out.println(dateFormat.format(new Date()) + "\tINFO\t" + tag + "\t" + message);
    }

    public static void w(String tag, String message) {
        System.err.println(dateFormat.format(new Date()) + "\tWARN\t" + tag + "\t" + message);
    }
}
