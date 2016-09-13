package opencsp.util;

import opencsp.Log;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class ClassFinder {
    private static final String TAG = "ClassFinder";

    private static final char PKG_SEPARATOR = '.';

    private static final char DIR_SEPARATOR = '/';

    private static final String CLASS_FILE_SUFFIX = ".class";

    private static final String BAD_PACKAGE_ERROR = "Unable to get resources from path '%s'. Are you sure the package '%s' exists?";

    public static List<Class<?>> find(String scannedPackage) {
        Log.d(TAG, "scannedPackage=" + scannedPackage);
        String scannedPath = scannedPackage.replace(PKG_SEPARATOR, DIR_SEPARATOR);
        Log.d(TAG, "scannedPath=" + scannedPath);
        URL scannedUrl = Thread.currentThread().getContextClassLoader().getResource(scannedPath);
        Log.d(TAG, "scannedUrl=" + scannedUrl.toString());
        if (scannedUrl == null) {
            throw new IllegalArgumentException(String.format(BAD_PACKAGE_ERROR, scannedPath, scannedPackage));
        }

        File scannedDir = new File(scannedUrl.getFile());

        Log.d(TAG, "scannedDir=" + scannedDir.toString());

        List<Class<?>> classes = new ArrayList<Class<?>>();
        for (File file : scannedDir.listFiles()) {
            List<Class<?>> c = find(file, scannedPackage);
            classes.addAll(c);
        }

        return classes;
    }

    private static List<Class<?>> find(File file, String scannedPackage) {
        List<Class<?>> classes = new ArrayList<Class<?>>();
        String resource = scannedPackage + PKG_SEPARATOR + file.getName();
        if (file.isDirectory()) {
            for (File child : file.listFiles()) {
                classes.addAll(find(child, resource));
            }
        } else if (resource.endsWith(CLASS_FILE_SUFFIX)) {
            int endIndex = resource.length() - CLASS_FILE_SUFFIX.length();
            String className = resource.substring(0, endIndex);
            try {
                classes.add(Class.forName(className));
                Log.d(TAG, "Adding Class: " + className);
            } catch (ClassNotFoundException ignore) {
                Log.d(TAG, ignore.getMessage());
                ignore.printStackTrace();
            }
        }
        return classes;
    }
}
