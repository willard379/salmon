package jp.ambrosoli.salmon.utils;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

public class PlatformUtil {

    private static final Map<String, String> cache = new HashMap<>();

    public static boolean isWindows() {
        String osName = cache.computeIfAbsent("os.name", System::getProperty); //$NON-NLS-1$
        return osName.toLowerCase().contains("windows"); //$NON-NLS-1$
    }

    public static boolean isUnix() {
        return !isWindows();
    }

    public static String lineSeparator() {
        return cache.computeIfAbsent("line.separator", System::getProperty); //$NON-NLS-1$
    }

    public static String pathSeparator() {
        return cache.computeIfAbsent("path.separator", System::getProperty); //$NON-NLS-1$
    }

    public static File devNull() {
        if (isWindows()) {
            return new File("nul"); //$NON-NLS-1$
        } else {
            return new File("/dev/null"); //$NON-NLS-1$
        }
    }

    public static String pathKey() {
        if (isWindows()) {
            return "Path"; //$NON-NLS-1$
        } else {
            return "PATH"; //$NON-NLS-1$
        }
    }

}
