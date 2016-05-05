package jp.ambrosoli.salmon.config;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class SalmonConfig {

    private static final String PROP_NAME = "salmon.properties"; //$NON-NLS-1$
    private static Properties prop;

    static void initialize() {
        prop = new Properties();
        try (InputStream stream = SalmonConfig.class.getClassLoader().getResourceAsStream(PROP_NAME)) {
            if (stream != null) {
                prop.load(stream);
            } else {
                loadDefault();
            }
        } catch (IOException e) {
            // do nothing
        }
    }

    private static void loadDefault() throws IOException {
        try (InputStream defaultStream = SalmonConfig.class.getResourceAsStream(PROP_NAME)) {
            prop.load(defaultStream);
        }
    }

    public static boolean isAutoMSDos() {
        return prop != null && Boolean.valueOf(prop.getProperty("auto.msdos", Boolean.FALSE.toString())).booleanValue(); //$NON-NLS-1$
    }
}
