package jp.ambrosoli.salmon.config;

import static java.nio.file.StandardOpenOption.*;

import java.io.IOException;
import java.lang.reflect.Field;
import java.net.URISyntaxException;
import java.nio.channels.FileChannel;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Properties;

// FIXME putで変更した設定値を元通り復元する作業は、現状テストケース側に個別実装している。このクラスを改良して復元漏れの対処や手順の簡略化を図りたい。
// 対応案：高階関数にする
@SuppressWarnings("nls")
public class SalmonConfigAccessor {

    public static long deploy(final String fileName) throws IOException, URISyntaxException {
        try (FileChannel src = FileChannel.open(getSourcePath(fileName), READ);
                FileChannel dest = FileChannel.open(getDestPath(), WRITE, CREATE)) {
            return dest.transferFrom(src, 0, src.size());
        }
    }

    public static boolean undeploy() throws IOException, URISyntaxException {
        return Files.deleteIfExists(getDestPath());
    }

    public static void put(final String key, final boolean value) {
        put(key, Boolean.toString(value));
    }

    public static void put(final String key, final String value) {
        SalmonInitializer.initialize();
        getProperties().put(key, value);
    }

    public static void remove(final String key) {
        getProperties().remove(key);
    }

    private static Properties getProperties() {
        try {
            Field field = SalmonConfig.class.getDeclaredField("prop");
            field.setAccessible(true);
            return (Properties) field.get(null);
        } catch (NoSuchFieldException | SecurityException | IllegalArgumentException | IllegalAccessException e) {
            throw new IllegalStateException(e);
        }
    }

    private static Path getDestPath() throws URISyntaxException {
        ClassLoader classLoader = SalmonConfigAccessor.class.getClassLoader();
        Path path = Paths.get(classLoader.getResource(".").toURI());
        return path.resolve("salmon.properties");
    }

    private static Path getSourcePath(final String fileName) throws URISyntaxException {
        return Paths.get(SalmonConfigAccessor.class.getResource(fileName).toURI());
    }

}
