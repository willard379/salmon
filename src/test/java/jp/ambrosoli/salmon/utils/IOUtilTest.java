package jp.ambrosoli.salmon.utils;

import static jp.ambrosoli.salmon.utils.IOUtil.*;
import static jp.ambrosoli.salmon.utils.PlatformUtil.*;
import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.collection.IsIterableContainingInOrder.*;
import static org.junit.gen5.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UncheckedIOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.stream.Collectors;

import org.junit.gen5.api.Test;
import org.junit.gen5.junit4.runner.JUnit5;
import org.junit.runner.RunWith;

import jp.ambrosoli.salmon.test.util.TestResourceUtil;

@RunWith(JUnit5.class)
@SuppressWarnings("nls")
class IOUtilTest {

    //*******************************************************
    // closeSilentry()
    //*******************************************************
    @Test
    void closeSilentry_IOExceptionが発生しない() throws Exception {
        // Set up fixture
        InputStream stream = mock(InputStream.class);

        // Exercise SUT
        IOUtil.closeSilently(stream);
    }

    @Test
    void closeSilentry_IOExceptionが発生する() throws Exception {
        // Set up fixture
        InputStream stream = mock(InputStream.class);
        doThrow(new IOException()).when(stream).close();

        // Exercise SUT & Verify outcome
        expectThrows(UncheckedIOException.class, () -> {
            IOUtil.closeSilently(stream);
        });
    }

    @Test
    void closeSilentry_null() throws Exception {
        // Set up fixture
        InputStream stream = null;

        // Exercise SUT
        IOUtil.closeSilently(stream);
    }

    @Test
    void closeSilentry_他の例外が発生する() throws Exception {
        // Set up fixture
        InputStream stream = mock(InputStream.class);
        doThrow(new RuntimeException()).when(stream).close();

        // Exercise SUT & Verify outcome
        expectThrows(RuntimeException.class, () -> {
            IOUtil.closeSilently(stream);
        });
    }

    @Test
    void closeSilentry_closeされている() throws Exception {
        // Set up fixture
        InputStream stream = TestResourceUtil.getResourceAsStream("assets/empty.txt");
        stream.close();

        // Exercise SUT
        IOUtil.closeSilently(stream);
    }

    //*******************************************************
    // toBufferedReader()
    //*******************************************************
    @Test
    void toBufferedReader_一行() throws Exception {
        // Set up fixture
        InputStream stream = new ByteArrayInputStream("I am a pen.".getBytes(Charset.defaultCharset()));

        // Exercise SUT
        BufferedReader actual = IOUtil.toBufferedReader(stream);

        // Verify outcome
        assertThat(actual.readLine(), is("I am a pen."));
    }

    @Test
    void toBufferedReader_複数行() throws Exception {
        // Set up fixture
        StringBuilder builder = new StringBuilder();
        builder.append("I am a pen.").append(lineSeparator()).append("He is an apple.").append(lineSeparator())
                .append("She is a book.").append(lineSeparator()).append("My father is a tyrannosaurus.");
        InputStream stream = new ByteArrayInputStream(builder.toString().getBytes(Charset.defaultCharset()));

        // Exercise SUT
        BufferedReader reader = IOUtil.toBufferedReader(stream);

        // Verify outcome
        assertThat(reader.lines().collect(Collectors.toList()),
                is(contains("I am a pen.", "He is an apple.", "She is a book.", "My father is a tyrannosaurus.")));
    }

    @Test
    void toBufferedReader_null() throws Exception {
        // Set up fixture
        InputStream stream = null;

        // Exercise SUT
        BufferedReader reader = IOUtil.toBufferedReader(stream);

        // Verify outcome
        assertThat(reader, is(nullValue()));
    }

    @Test
    void toBufferedReader_closeされている() throws Exception {
        // Set up fixture
        InputStream stream = TestResourceUtil.getResourceAsStream("assets/empty.txt");
        stream.close();

        // Exercise SUT
        BufferedReader reader = IOUtil.toBufferedReader(stream);

        // Verify outcome
        assertThat(reader.ready(), is(false));
    }

    //*******************************************************
    // readAll(InputStream)
    //*******************************************************
    @Test
    void readAll_InputStream_空() throws Exception {
        // Set up fixture
        InputStream stream = new ByteArrayInputStream(new byte[0]);

        // Exercise SUT
        String actual = IOUtil.readAll(stream);

        // Verify outcome
        assertThat(actual, is(""));
    }

    @Test
    void readAll_InputStream_単一行() throws Exception {
        // Set up fixture
        InputStream stream = new ByteArrayInputStream("hoge".getBytes());

        // Exercise SUT
        String actual = IOUtil.readAll(stream);

        // Verify outcome
        assertThat(actual, is("hoge"));
    }

    @Test
    void readAll_InputStream_複数行() throws Exception {
        // Set up fixture
        InputStream stream = new ByteArrayInputStream("hoge\nfoo\nbar".getBytes());

        // Exercise SUT
        String actual = IOUtil.readAll(stream);

        // Verify outcome
        assertThat(actual, is("hoge\nfoo\nbar"));
    }

    @Test
    void readAll_InputStream_null() throws Exception {
        // Set up fixture
        InputStream stream = null;

        // Exercise SUT & Verify outcome
        expectThrows(NullPointerException.class, () -> {
            IOUtil.readAll(stream);
        });
    }

    @Test
    void readAll_InputStream_close済み() throws Exception {
        // Set up fixture
        InputStream stream = TestResourceUtil.getResourceAsStream("assets/empty.txt");
        stream.close();

        // Exercise SUT & Verify outcome
        expectThrows(IOException.class, () -> {
            IOUtil.readAll(stream);
        });
    }

    //*******************************************************
    // readAll(InputStream, Charset)
    //*******************************************************
    @Test
    void readAll_InputStream_Charset_文字コード一致_MS932() throws Exception {
        // Set up fixture
        InputStream stream = spy(new ByteArrayInputStream("私はペンです。".getBytes(MS932)));

        // Exercise SUT
        String actual = IOUtil.readAll(stream, MS932);

        // Verify outcome
        assertThat(actual, is("私はペンです。"));
        verify(stream).close();
    }

    @Test
    void readAll_InputStream_Charset_文字コード一致_GB2312() throws Exception {
        // Set up fixture
        InputStream stream = spy(new ByteArrayInputStream("我一支钢笔".getBytes(Charset.forName("GB2312"))));

        // Exercise SUT
        String actual = IOUtil.readAll(stream, Charset.forName("GB2312"));

        // Verify outcome
        assertThat(actual, is("我一支钢笔"));
        verify(stream).close();
    }

    @Test
    void readAll_InputStream_Charset_文字コード不一致() throws Exception {
        // Set up fixture
        InputStream stream = spy(new ByteArrayInputStream("私はペンです。".getBytes(MS932)));

        // Exercise SUT
        String actual = IOUtil.readAll(stream, Charset.forName("EUC-JP"));

        // Verify outcome
        assertThat(actual, is(not("私はペンです。")));
        verify(stream).close();
    }

    @Test
    void readAll_InputStream_Charset_文字コードがnull() throws Exception {
        // Set up fixture
        InputStream stream = spy(new ByteArrayInputStream("私はペンです。".getBytes(Charset.defaultCharset())));

        // Exercise SUT
        String actual = IOUtil.readAll(stream, null);

        // Verify outcome
        assertThat(actual, is("私はペンです。"));
        verify(stream).close();
    }

    @Test
    void readAll_InputStream_Charset_ストリームがnull() throws Exception {
        // Set up fixture
        InputStream stream = null;

        // Exercise SUT & Verify outcome
        expectThrows(NullPointerException.class, () -> {
            IOUtil.readAll(stream, Charset.defaultCharset());
        });
    }

    @Test
    void readAll_InputStream_Charset_close済み() throws Exception {
        // Set up fixture
        InputStream stream = TestResourceUtil.getResourceAsStream("assets/empty.txt");
        stream.close();

        // Exercise SUT & Verify outcome
        expectThrows(IOException.class, () -> {
            IOUtil.readAll(stream);
        });
    }

    //*******************************************************
    // readAllSilently(InputStream)
    //*******************************************************
    @Test
    void readAllSilently_InputStream_空() throws Exception {
        // Set up fixture
        InputStream stream = spy(new ByteArrayInputStream(new byte[0]));

        // Exercise SUT
        String actual = IOUtil.readAllSilently(stream);

        // Verify outcome
        assertThat(actual, is(""));
        verify(stream).close();
    }

    @Test
    void readAllSilently_InputStream_単一行() throws Exception {
        // Set up fixture
        InputStream stream = spy(new ByteArrayInputStream("hoge".getBytes()));

        // Exercise SUT
        String actual = IOUtil.readAllSilently(stream);

        // Verify outcome
        assertThat(actual, is("hoge"));
        verify(stream).close();
    }

    @Test
    void readAllSilently_InputStream_複数行() throws Exception {
        // Set up fixture
        InputStream stream = spy(new ByteArrayInputStream("hoge\nfoo\nbar".getBytes()));

        // Exercise SUT
        String actual = IOUtil.readAllSilently(stream);

        // Verify outcome
        assertThat(actual, is("hoge\nfoo\nbar"));
        verify(stream).close();
    }

    @Test
    void readAllSilently_InputStream_null() throws Exception {
        // Set up fixture
        InputStream stream = null;

        // Exercise SUT & Verify outcome
        expectThrows(NullPointerException.class, () -> {
            IOUtil.readAllSilently(stream);
        });
    }

    @Test
    void readAllSilently_InputStream_close済み() throws Exception {
        // Set up fixture
        InputStream stream = TestResourceUtil.getResourceAsStream("assets/empty.txt");
        stream.close();

        // Exercise SUT & Verify outcome
        expectThrows(UncheckedIOException.class, () -> {
            IOUtil.readAllSilently(stream);
        });
    }

    //*******************************************************
    // readAllSilently(InputStream, Charset)
    //*******************************************************
    @Test
    void readAllSilently_InputStream_Charset_文字コード一致_MS932() throws Exception {
        // Set up fixture
        InputStream stream = spy(new ByteArrayInputStream("私はペンです。".getBytes(MS932)));

        // Exercise SUT
        String actual = IOUtil.readAllSilently(stream, MS932);

        // Verify outcome
        assertThat(actual, is("私はペンです。"));
        verify(stream).close();
    }

    @Test
    void readAllSilently_InputStream_Charset_文字コード一致_GB2312() throws Exception {
        // Set up fixture
        InputStream stream = spy(new ByteArrayInputStream("我一支钢笔".getBytes(Charset.forName("GB2312"))));

        // Exercise SUT
        String actual = IOUtil.readAllSilently(stream, Charset.forName("GB2312"));

        // Verify outcome
        assertThat(actual, is("我一支钢笔"));
        verify(stream).close();
    }

    @Test
    void readAllSilently_InputStream_Charset_文字コード不一致() throws Exception {
        // Set up fixture
        InputStream stream = spy(new ByteArrayInputStream("私はペンです。".getBytes(MS932)));

        // Exercise SUT
        String actual = IOUtil.readAllSilently(stream, Charset.forName("EUC-JP"));

        // Verify outcome
        assertThat(actual, is(not("私はペンです。")));
        verify(stream).close();
    }

    @Test
    void readAllSilently_InputStream_Charset_文字コードがnull() throws Exception {
        // Set up fixture
        InputStream stream = spy(new ByteArrayInputStream("私はペンです。".getBytes(Charset.defaultCharset())));

        // Exercise SUT
        String actual = IOUtil.readAllSilently(stream, null);

        // Verify outcome
        assertThat(actual, is("私はペンです。"));
        verify(stream).close();
    }

    @Test
    void readAllSilently_InputStream_Charset_ストリームがnull() throws Exception {
        // Set up fixture
        InputStream stream = null;

        // Exercise SUT & Verify outcome
        expectThrows(NullPointerException.class, () -> {
            IOUtil.readAllSilently(stream, Charset.defaultCharset());
        });
    }

    @Test
    void readAllSilently_InputStream_Charset_close済み() throws Exception {
        // Set up fixture
        InputStream stream = TestResourceUtil.getResourceAsStream("assets/empty.txt");
        stream.close();

        // Exercise SUT & Verify outcome
        expectThrows(UncheckedIOException.class, () -> {
            IOUtil.readAllSilently(stream);
        });
    }

    //*******************************************************
    // copy(InputStream, OutputStream)
    //*******************************************************
    @Test
    void copy_空文字() throws Exception {
        // Set up fixture
        InputStream source = spy(new ByteArrayInputStream(new byte[0]));
        ByteArrayOutputStream dest = spy(new ByteArrayOutputStream());

        // Exercise SUT
        IOUtil.copy(source, dest);

        // Verify outcome
        assertThat(new String(dest.toByteArray()), is(""));
        verify(source).close();
        verify(dest).close();
    }

    @Test
    void copy_単一行() throws Exception {
        // Set up fixture
        InputStream source = spy(new ByteArrayInputStream("あいうえおかきくけこ".getBytes()));
        ByteArrayOutputStream dest = spy(new ByteArrayOutputStream());

        // Exercise SUT
        IOUtil.copy(source, dest);

        // Verify outcome
        assertThat(new String(dest.toByteArray()), is("あいうえおかきくけこ"));
        verify(source).close();
        verify(dest).close();
    }

    @Test
    void copy_複数行() throws Exception {
        // Set up fixture
        InputStream source = spy(new ByteArrayInputStream("hoge\nfoo\nbar".getBytes()));
        ByteArrayOutputStream dest = spy(new ByteArrayOutputStream());

        // Exercise SUT
        IOUtil.copy(source, dest);

        // Verify outcome
        assertThat(new String(dest.toByteArray()), is("hoge\nfoo\nbar"));
        verify(source).close();
        verify(dest).close();
    }

    @Test
    void copy_文字コードMS932() throws Exception {
        // Set up fixture
        InputStream source = spy(new ByteArrayInputStream("あいうえおかきくけこ".getBytes(MS932)));
        ByteArrayOutputStream dest = spy(new ByteArrayOutputStream());

        // Exercise SUT
        IOUtil.copy(source, dest);

        // Verify outcome
        assertThat(new String(dest.toByteArray(), MS932), is("あいうえおかきくけこ"));
        verify(source).close();
        verify(dest).close();
    }

    @Test
    void copy_InputStreamがclose済み() throws Exception {
        // Set up fixture
        InputStream source = TestResourceUtil.getResourceAsStream("assets/empty.txt");
        source.close();
        ByteArrayOutputStream dest = spy(new ByteArrayOutputStream());

        // Exercise SUT
        expectThrows(IOException.class, () -> {
            IOUtil.copy(source, dest);
        });

        // Verify outcome
        assertThat(dest.size(), is(0));
        verify(dest).close();
    }

    @Test
    void copy_OutputStreamがclose済み() throws Exception {
        // Set up fixture
        InputStream source = spy(new ByteArrayInputStream("hoge".getBytes()));
        OutputStream dest = Files.newOutputStream(Paths.get(TestResourceUtil.getFilePath("assets/empty.txt")));
        dest.close();

        // Exercise SUT
        expectThrows(IOException.class, () -> {
            IOUtil.copy(source, dest);
        });

        // Verify outcome
        verify(source).close();
    }

    @Test
    void copy_InputStreamがnull() throws Exception {
        // Set up fixture
        InputStream source = null;
        ByteArrayOutputStream dest = spy(new ByteArrayOutputStream());

        // Exercise SUT
        expectThrows(NullPointerException.class, () -> {
            IOUtil.copy(source, dest);
        });

        // Verify outcome
        assertThat(dest.size(), is(0));
        verify(dest).close();
    }

    @Test
    void copy_OutputStreamがnull() throws Exception {
        // Set up fixture
        InputStream source = spy(new ByteArrayInputStream("hoge".getBytes()));
        OutputStream dest = null;

        // Exercise SUT
        expectThrows(NullPointerException.class, () -> {
            IOUtil.copy(source, dest);
        });

        // Verify outcome
        verify(source).close();
    }

    //*******************************************************
    // copySilently(InputStream, OutputStream)
    //*******************************************************
    @Test
    void copySilently_空文字() throws Exception {
        // Set up fixture
        InputStream source = spy(new ByteArrayInputStream(new byte[0]));
        ByteArrayOutputStream dest = spy(new ByteArrayOutputStream());

        // Exercise SUT
        IOUtil.copySilently(source, dest);

        // Verify outcome
        assertThat(new String(dest.toByteArray()), is(""));
        verify(source).close();
        verify(dest).close();
    }

    @Test
    void copySilently_単一行() throws Exception {
        // Set up fixture
        InputStream source = spy(new ByteArrayInputStream("あいうえおかきくけこ".getBytes()));
        ByteArrayOutputStream dest = spy(new ByteArrayOutputStream());

        // Exercise SUT
        IOUtil.copySilently(source, dest);

        // Verify outcome
        assertThat(new String(dest.toByteArray()), is("あいうえおかきくけこ"));
        verify(source).close();
        verify(dest).close();
    }

    @Test
    void copySilently_複数行() throws Exception {
        // Set up fixture
        InputStream source = spy(new ByteArrayInputStream("hoge\nfoo\nbar".getBytes()));
        ByteArrayOutputStream dest = spy(new ByteArrayOutputStream());

        // Exercise SUT
        IOUtil.copySilently(source, dest);

        // Verify outcome
        assertThat(new String(dest.toByteArray()), is("hoge\nfoo\nbar"));
        verify(source).close();
        verify(dest).close();
    }

    @Test
    void copySilently_文字コードMS932() throws Exception {
        // Set up fixture
        InputStream source = spy(new ByteArrayInputStream("あいうえおかきくけこ".getBytes(MS932)));
        ByteArrayOutputStream dest = spy(new ByteArrayOutputStream());

        // Exercise SUT
        IOUtil.copySilently(source, dest);

        // Verify outcome
        assertThat(new String(dest.toByteArray(), MS932), is("あいうえおかきくけこ"));
        verify(source).close();
        verify(dest).close();
    }

    @Test
    void copySilently_InputStreamがclose済み() throws Exception {
        // Set up fixture
        InputStream source = TestResourceUtil.getResourceAsStream("assets/empty.txt");
        source.close();
        ByteArrayOutputStream dest = spy(new ByteArrayOutputStream());

        // Exercise SUT
        expectThrows(UncheckedIOException.class, () -> {
            IOUtil.copySilently(source, dest);
        });

        // Verify outcome
        assertThat(dest.size(), is(0));
        verify(dest).close();
    }

    @Test
    void copySilently_OutputStreamがclose済み() throws Exception {
        // Set up fixture
        InputStream source = spy(new ByteArrayInputStream("hoge".getBytes()));
        OutputStream dest = Files.newOutputStream(Paths.get(TestResourceUtil.getFilePath("assets/empty.txt")));
        dest.close();

        // Exercise SUT
        expectThrows(UncheckedIOException.class, () -> {
            IOUtil.copySilently(source, dest);
        });

        // Verify outcome
        verify(source).close();
    }

    @Test
    void copySilently_InputStreamがnull() throws Exception {
        // Set up fixture
        InputStream source = null;
        ByteArrayOutputStream dest = spy(new ByteArrayOutputStream());

        // Exercise SUT
        expectThrows(NullPointerException.class, () -> {
            IOUtil.copySilently(source, dest);
        });

        // Verify outcome
        assertThat(dest.size(), is(0));
        verify(dest).close();
    }

    @Test
    void copySilently_OutputStreamがnull() throws Exception {
        // Set up fixture
        InputStream source = spy(new ByteArrayInputStream("hoge".getBytes()));
        OutputStream dest = null;

        // Exercise SUT
        expectThrows(NullPointerException.class, () -> {
            IOUtil.copySilently(source, dest);
        });

        // Verify outcome
        verify(source).close();
    }
}
