package jp.ambrosoli.salmon.utils;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.UncheckedIOException;
import java.nio.charset.Charset;

public class IOUtil {

    public static final Charset MS932 = Charset.forName("ms932"); //$NON-NLS-1$

    public static void closeSilently(final Closeable stream) {
        if (stream == null) {
            return;
        }
        try {
            stream.close();
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    public static BufferedReader toBufferedReader(final InputStream stream) {
        if (stream == null) {
            return null;
        }
        return new BufferedReader(new InputStreamReader(stream));
    }

    public static String readAll(final InputStream stream) throws IOException {
        return readAll(stream, Charset.defaultCharset());
    }

    public static String readAll(final InputStream stream, final Charset charset) throws IOException {
        try (InputStream in = stream; ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            byte[] buff = new byte[1024];
            int size = 0;
            while ((size = in.read(buff)) > 0) {
                out.write(buff, 0, size);
            }
            String charsetStr = charset != null ? charset.toString() : Charset.defaultCharset().toString();
            return out.toString(charsetStr);
        }
    }

    public static String readAllSilently(final InputStream stream) {
        return readAllSilently(stream, Charset.defaultCharset());
    }

    public static String readAllSilently(final InputStream stream, final Charset charset) {
        try {
            return readAll(stream, charset);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    public static void copy(final InputStream source, final OutputStream dest) throws IOException {
        byte[] buff = new byte[8192];
        int size = 0;
        try {
            while ((size = source.read(buff)) != -1) {
                dest.write(buff, 0, size);
            }
        } finally {
            try {
                source.close();
            } finally {
                dest.close();
            }
        }
    }

    public static void copySilently(final InputStream source, final OutputStream dest) {
        try {
            copy(source, dest);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

}
