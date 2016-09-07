package opencsp.util;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.Properties;

public class ConfigurationProvider {
    private static final String PROPERTIES_FILE_NAME = "config.properties";
    private Properties prop;

    public ConfigurationProvider() {
        try {
            InputStream inputStream = getClass().getClassLoader().getResourceAsStream(PROPERTIES_FILE_NAME);
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
