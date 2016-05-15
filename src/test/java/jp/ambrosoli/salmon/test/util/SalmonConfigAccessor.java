package jp.ambrosoli.salmon.test.util;

import static java.nio.file.StandardOpenOption.*;

import java.io.IOException;
import java.lang.reflect.Field;
import java.net.URISyntaxException;
import java.nio.channels.FileChannel;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Properties;

import jp.ambrosoli.salmon.config.SalmonConfig;
import jp.ambrosoli.salmon.config.SalmonInitializer;

@SuppressWarnings("nls")
public class SalmonConfigAccessor {

    public static long deploy(final Path path) throws IOException, URISyntaxException {
        try (FileChannel src = FileChannel.open(path, READ);
                FileChannel dest = FileChannel.open(getDestPath(), WRITE, CREATE)) {
            return dest.transferFrom(src, 0, src.size());
        }
    }

    public static long deploy(final Object obj, final String fileName) throws IOException, URISyntaxException {
        return deploy(Paths.get(obj.getClass().getResource(fileName).toURI()));
    }

    public static boolean undeploy() throws IOException, URISyntaxException {
        return Files.deleteIfExists(getDestPath());
    }

    public static void withTemporaryConfig(final String key, final Object value,
            final RunnableToThrowException runnable) throws Exception {
        SalmonInitializer.initialize();
        getProperties().put(key, value.toString());
        try {
            runnable.run();
        } finally {
            getProperties().remove(key);
        }
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

}
