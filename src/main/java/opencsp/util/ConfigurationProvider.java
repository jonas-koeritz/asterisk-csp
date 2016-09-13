package opencsp.util;

import opencsp.Log;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.nio.file.Paths;
import java.util.Properties;

public class ConfigurationProvider {
    private static final String TAG = "ConfigurationProvider";

    private static final String PROPERTIES_FILE_NAME = "config.properties";
    private Properties prop;

    public ConfigurationProvider() {
        try {
            Log.d(TAG, "Current Directory: " + Paths.get(".").toAbsolutePath().normalize().toString());
            InputStream inputStream = new FileInputStream(PROPERTIES_FILE_NAME);
            prop = new Properties();

            if (inputStream != null) {
                prop.load(inputStream);
            } else {
                throw new FileNotFoundException("Configuration not found");
            }

            inputStream.close();
        } catch (Exception ex) {
            System.err.println(ex.getMessage());
            ex.printStackTrace();
        }
    }

    public String getConfigurationValue(String key) {
        return prop.getProperty(key);
    }
}
