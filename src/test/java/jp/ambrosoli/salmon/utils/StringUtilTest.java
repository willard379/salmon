package jp.ambrosoli.salmon.utils;

import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.MatcherAssert.*;

import org.junit.gen5.api.Test;
import org.junit.gen5.junit4.runner.JUnit5;
import org.junit.runner.RunWith;

@RunWith(JUnit5.class)
@SuppressWarnings("nls")
public class StringUtilTest {

    @Test
    public void isEmpty_null空文字でない文字列を渡すとfalseが返される() throws Exception {
        // SetUp
        String value = "value";

        // Exercise
        boolean actual = StringUtil.isEmpty(value);

        // Verify
        assertThat(actual, is(false));
    }

    @Test
    public void isEmpty_nullを渡すとtrueが返される() throws Exception {
        // SetUp
        String value = null;

        // Exercise
        boolean actual = StringUtil.isEmpty(value);

        // Verify
        assertThat(actual, is(true));
    }

    @Test
    public void isEmpty_空文字を渡すとtrueが返される() throws Exception {
        // SetUp
        String value = "";

        // Exercise
        boolean actual = StringUtil.isEmpty(value);

        // Verify
        assertThat(actual, is(true));
    }

    @Test
    public void isEmpty_ホワイトスペースを渡すとfalseが返される() throws Exception {
        // SetUp
        String value = " ";

        // Exercise
        boolean actual = StringUtil.isEmpty(value);

        // Verify
        assertThat(actual, is(false));
    }

    @Test
    public void isNotEmpty_null空文字でない文字列を渡すとtrueが返される() throws Exception {
        // SetUp
        String value = "value";

        // Exercise
        boolean actual = StringUtil.isNotEmpty(value);

        // Verify
        assertThat(actual, is(true));
    }

    @Test
    public void isNotEmpty_nullを渡すとfalseが返される() throws Exception {
        // SetUp
        String value = null;

        // Exercise
        boolean actual = StringUtil.isNotEmpty(value);

        // Verify
        assertThat(actual, is(false));
    }

    @Test
    public void isNotEmpty_空文字を渡すとfalseが返される() throws Exception {
        // SetUp
        String value = "";

        // Exercise
        boolean actual = StringUtil.isNotEmpty(value);

        // Verify
        assertThat(actual, is(false));
    }

    @Test
    public void isNotEmpty_ホワイトスペースを渡すとtrueが返される() throws Exception {
        // SetUp
        String value = " ";

        // Exercise
        boolean actual = StringUtil.isNotEmpty(value);

        // Verify
        assertThat(actual, is(true));
    }

    @Test
    public void chomp_nullを渡すとnullが返される() throws Exception {
        // SetUp
        String target = null;

        // Exercise
        String actual = StringUtil.chomp(target);

        // Verify
        assertThat(actual, is(is(nullValue())));
    }

    @Test
    public void chomp_空文字を渡すと空文字が返される() throws Exception {
        // SetUp
        String target = "";

        // Exercise
        String actual = StringUtil.chomp(target);

        // Verify
        assertThat(actual, is(""));
    }

    @Test
    public void chomp_改行コードを含まない文字列を渡すとそのまま返される() throws Exception {
        // SetUp
        String target = "hoge";

        // Exercise
        String actual = StringUtil.chomp(target);

        // Verify
        assertThat(actual, is("hoge"));
    }

    @Test
    public void chomp_末尾にCRを含む文字列を渡すと末尾のCRが削除される() throws Exception {
        // SetUp
        String target = "hoge\r";

        // Exercise
        String actual = StringUtil.chomp(target);

        // Verify
        assertThat(actual, is("hoge"));
    }

    @Test
    public void chomp_末尾にLFを含む文字列を渡すと末尾のLFが削除される() throws Exception {
        // SetUp
        String target = "hoge\n";

        // Exercise
        String actual = StringUtil.chomp(target);

        // Verify
        assertThat(actual, is("hoge"));
    }

    @Test
    public void chomp_末尾にCRLFを含む文字列を渡すと末尾のCRLFが削除される() throws Exception {
        // SetUp
        String target = "hoge\r\n";

        // Exercise
        String actual = StringUtil.chomp(target);

        // Verify
        assertThat(actual, is("hoge"));
    }

    @Test
    public void chomp_先頭にCRを含む文字列を渡すとそのまま返される() throws Exception {
        // SetUp
        String target = "\rhoge";

        // Exercise
        String actual = StringUtil.chomp(target);

        // Verify
        assertThat(actual, is("\rhoge"));
    }

    @Test
    public void chomp_先頭にLFを含む文字列を渡すとそのまま返される() throws Exception {
        // SetUp
        String target = "\nhoge";

        // Exercise
        String actual = StringUtil.chomp(target);

        // Verify
        assertThat(actual, is("\nhoge"));
    }

    @Test
    public void chomp_先頭にCRLFを含む文字列を渡すとそのまま返される() throws Exception {
        // SetUp
        String target = "\r\nhoge";

        // Exercise
        String actual = StringUtil.chomp(target);

        // Verify
        assertThat(actual, is("\r\nhoge"));
    }

    @Test
    public void chomp_途中にCRを含む文字列を渡すとそのまま返される() throws Exception {
        // SetUp
        String target = "ho\rge";

        // Exercise
        String actual = StringUtil.chomp(target);

        // Verify
        assertThat(actual, is("ho\rge"));
    }

    @Test
    public void chomp_途中にLFを含む文字列を渡すとそのまま返される() throws Exception {
        // SetUp
        String target = "ho\nge";

        // Exercise
        String actual = StringUtil.chomp(target);

        // Verify
        assertThat(actual, is("ho\nge"));
    }

    @Test
    public void chomp_途中にCRLFを含む文字列を渡すとそのまま返される() throws Exception {
        // SetUp
        String target = "ho\r\nge";

        // Exercise
        String actual = StringUtil.chomp(target);

        // Verify
        assertThat(actual, is("ho\r\nge"));
    }

    @Test
    public void chomp_先頭と途中と末尾にCRを含む文字列を渡すと末尾のCRのみ削除される() throws Exception {
        // SetUp
        String target = "\rho\rge\r";

        // Exercise
        String actual = StringUtil.chomp(target);

        // Verify
        assertThat(actual, is("\rho\rge"));
    }

    @Test
    public void chomp_先頭と途中と末尾にLFを含む文字列を渡すと末尾のLFのみ削除される() throws Exception {
        // SetUp
        String target = "\nho\nge\n";

        // Exercise
        String actual = StringUtil.chomp(target);

        // Verify
        assertThat(actual, is("\nho\nge"));
    }

    @Test
    public void chomp_先頭と途中と末尾にCRLFを含む文字列を渡すと末尾のCRLFのみ削除される() throws Exception {
        // SetUp
        String target = "\r\nho\r\nge\r\n";

        // Exercise
        String actual = StringUtil.chomp(target);

        // Verify
        assertThat(actual, is("\r\nho\r\nge"));
    }

    @Test
    public void chomp_連続するCRで終わる文字列を渡すと末尾のCRのみ削除される() throws Exception {
        // SetUp
        String target = "hoge\r\r";

        // Exercise
        String actual = StringUtil.chomp(target);

        // Verify
        assertThat(actual, is("hoge\r"));
    }

    @Test
    public void chomp_連続するLFで終わる文字列を渡すと末尾のLFのみ削除される() throws Exception {
        // SetUp
        String target = "hoge\n\n";

        // Exercise
        String actual = StringUtil.chomp(target);

        // Verify
        assertThat(actual, is("hoge\n"));
    }

    @Test
    public void chomp_連続するCRLFで終わる文字列を渡すと末尾のCRLFのみ削除される() throws Exception {
        // SetUp
        String target = "hoge\r\n\r\n";

        // Exercise
        String actual = StringUtil.chomp(target);

        // Verify
        assertThat(actual, is("hoge\r\n"));
    }

}
