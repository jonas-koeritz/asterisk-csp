package opencsp.util;

import opencsp.Log;
import opencsp.csta.types.CSTAMessage;
import org.reflections.Reflections;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class ClassFinder {
    private static final String TAG = "ClassFinder";

    public static List<Class<?>> findCstaMessageClasses() {
        Reflections r = new Reflections("opencsp");
        Set<Class<? extends CSTAMessage>> rClasses = r.getSubTypesOf(CSTAMessage.class);
        Log.d(TAG, "Found " + rClasses.size() + " CSTA Message Classes.");
        List<Class<?>> classes = new ArrayList<Class<?>>();
        classes.addAll(rClasses);
        return classes;
    }
}
