package jp.ambrosoli.salmon.test.util;

import java.io.InputStream;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Paths;

public class TestResourceUtil {

    public static String getFilePath(final String resourceName) {
        URL resource = TestResourceUtil.class.getClassLoader().getResource(resourceName);
        if (resource == null) {
            return null;
        }
        try {
            return Paths.get(resource.toURI()).toString();
        } catch (URISyntaxException e) {
            return null;
        }
    }

    public static InputStream getResourceAsStream(final String resourceName) {
        return TestResourceUtil.class.getClassLoader().getResourceAsStream(resourceName);
    }
}
