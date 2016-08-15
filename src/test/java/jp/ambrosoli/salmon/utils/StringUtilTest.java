package jp.ambrosoli.salmon.utils;

import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.MatcherAssert.*;

import org.junit.gen5.api.Test;
import org.junit.gen5.junit4.runner.JUnit5;
import org.junit.runner.RunWith;

@RunWith(JUnit5.class)
@SuppressWarnings("nls")
class StringUtilTest {

    @Test
    void isEmpty_null空文字でない文字列を渡すとfalseが返される() throws Exception {
        // Set up fixture
        String value = "value";

        // Exercise SUT
        boolean actual = StringUtil.isEmpty(value);

        // Verify outcome
        assertThat(actual, is(false));
    }

    @Test
    void isEmpty_nullを渡すとtrueが返される() throws Exception {
        // Set up fixture
        String value = null;

        // Exercise SUT
        boolean actual = StringUtil.isEmpty(value);

        // Verify outcome
        assertThat(actual, is(true));
    }

    @Test
    void isEmpty_空文字を渡すとtrueが返される() throws Exception {
        // Set up fixture
        String value = "";

        // Exercise SUT
        boolean actual = StringUtil.isEmpty(value);

        // Verify outcome
        assertThat(actual, is(true));
    }

    @Test
    void isEmpty_ホワイトスペースを渡すとfalseが返される() throws Exception {
        // Set up fixture
        String value = " ";

        // Exercise SUT
        boolean actual = StringUtil.isEmpty(value);

        // Verify outcome
        assertThat(actual, is(false));
    }

    @Test
    void isNotEmpty_null空文字でない文字列を渡すとtrueが返される() throws Exception {
        // Set up fixture
        String value = "value";

        // Exercise SUT
        boolean actual = StringUtil.isNotEmpty(value);

        // Verify outcome
        assertThat(actual, is(true));
    }

    @Test
    void isNotEmpty_nullを渡すとfalseが返される() throws Exception {
        // Set up fixture
        String value = null;

        // Exercise SUT
        boolean actual = StringUtil.isNotEmpty(value);

        // Verify outcome
        assertThat(actual, is(false));
    }

    @Test
    void isNotEmpty_空文字を渡すとfalseが返される() throws Exception {
        // Set up fixture
        String value = "";

        // Exercise SUT
        boolean actual = StringUtil.isNotEmpty(value);

        // Verify outcome
        assertThat(actual, is(false));
    }

    @Test
    void isNotEmpty_ホワイトスペースを渡すとtrueが返される() throws Exception {
        // Set up fixture
        String value = " ";

        // Exercise SUT
        boolean actual = StringUtil.isNotEmpty(value);

        // Verify outcome
        assertThat(actual, is(true));
    }

    @Test
    void chomp_nullを渡すとnullが返される() throws Exception {
        // Set up fixture
        String target = null;

        // Exercise SUT
        String actual = StringUtil.chomp(target);

        // Verify outcome
        assertThat(actual, is(is(nullValue())));
    }

    @Test
    void chomp_空文字を渡すと空文字が返される() throws Exception {
        // Set up fixture
        String target = "";

        // Exercise SUT
        String actual = StringUtil.chomp(target);

        // Verify outcome
        assertThat(actual, is(""));
    }

    @Test
    void chomp_改行コードを含まない文字列を渡すとそのまま返される() throws Exception {
        // Set up fixture
        String target = "hoge";

        // Exercise SUT
        String actual = StringUtil.chomp(target);

        // Verify outcome
        assertThat(actual, is("hoge"));
    }

    @Test
    void chomp_末尾にCRを含む文字列を渡すと末尾のCRが削除される() throws Exception {
        // Set up fixture
        String target = "hoge\r";

        // Exercise SUT
        String actual = StringUtil.chomp(target);

        // Verify outcome
        assertThat(actual, is("hoge"));
    }

    @Test
    void chomp_末尾にLFを含む文字列を渡すと末尾のLFが削除される() throws Exception {
        // Set up fixture
        String target = "hoge\n";

        // Exercise SUT
        String actual = StringUtil.chomp(target);

        // Verify outcome
        assertThat(actual, is("hoge"));
    }

    @Test
    void chomp_末尾にCRLFを含む文字列を渡すと末尾のCRLFが削除される() throws Exception {
        // Set up fixture
        String target = "hoge\r\n";

        // Exercise SUT
        String actual = StringUtil.chomp(target);

        // Verify outcome
        assertThat(actual, is("hoge"));
    }

    @Test
    void chomp_先頭にCRを含む文字列を渡すとそのまま返される() throws Exception {
        // Set up fixture
        String target = "\rhoge";

        // Exercise SUT
        String actual = StringUtil.chomp(target);

        // Verify outcome
        assertThat(actual, is("\rhoge"));
    }

    @Test
    void chomp_先頭にLFを含む文字列を渡すとそのまま返される() throws Exception {
        // Set up fixture
        String target = "\nhoge";

        // Exercise SUT
        String actual = StringUtil.chomp(target);

        // Verify outcome
        assertThat(actual, is("\nhoge"));
    }

    @Test
    void chomp_先頭にCRLFを含む文字列を渡すとそのまま返される() throws Exception {
        // Set up fixture
        String target = "\r\nhoge";

        // Exercise SUT
        String actual = StringUtil.chomp(target);

        // Verify outcome
        assertThat(actual, is("\r\nhoge"));
    }

    @Test
    void chomp_途中にCRを含む文字列を渡すとそのまま返される() throws Exception {
        // Set up fixture
        String target = "ho\rge";

        // Exercise SUT
        String actual = StringUtil.chomp(target);

        // Verify outcome
        assertThat(actual, is("ho\rge"));
    }

    @Test
    void chomp_途中にLFを含む文字列を渡すとそのまま返される() throws Exception {
        // Set up fixture
        String target = "ho\nge";

        // Exercise SUT
        String actual = StringUtil.chomp(target);

        // Verify outcome
        assertThat(actual, is("ho\nge"));
    }

    @Test
    void chomp_途中にCRLFを含む文字列を渡すとそのまま返される() throws Exception {
        // Set up fixture
        String target = "ho\r\nge";

        // Exercise SUT
        String actual = StringUtil.chomp(target);

        // Verify outcome
        assertThat(actual, is("ho\r\nge"));
    }

    @Test
    void chomp_先頭と途中と末尾にCRを含む文字列を渡すと末尾のCRのみ削除される() throws Exception {
        // Set up fixture
        String target = "\rho\rge\r";

        // Exercise SUT
        String actual = StringUtil.chomp(target);

        // Verify outcome
        assertThat(actual, is("\rho\rge"));
    }

    @Test
    void chomp_先頭と途中と末尾にLFを含む文字列を渡すと末尾のLFのみ削除される() throws Exception {
        // Set up fixture
        String target = "\nho\nge\n";

        // Exercise SUT
        String actual = StringUtil.chomp(target);

        // Verify outcome
        assertThat(actual, is("\nho\nge"));
    }

    @Test
    void chomp_先頭と途中と末尾にCRLFを含む文字列を渡すと末尾のCRLFのみ削除される() throws Exception {
        // Set up fixture
        String target = "\r\nho\r\nge\r\n";

        // Exercise SUT
        String actual = StringUtil.chomp(target);

        // Verify outcome
        assertThat(actual, is("\r\nho\r\nge"));
    }

    @Test
    void chomp_連続するCRで終わる文字列を渡すと末尾のCRのみ削除される() throws Exception {
        // Set up fixture
        String target = "hoge\r\r";

        // Exercise SUT
        String actual = StringUtil.chomp(target);

        // Verify outcome
        assertThat(actual, is("hoge\r"));
    }

    @Test
    void chomp_連続するLFで終わる文字列を渡すと末尾のLFのみ削除される() throws Exception {
        // Set up fixture
        String target = "hoge\n\n";

        // Exercise SUT
        String actual = StringUtil.chomp(target);

        // Verify outcome
        assertThat(actual, is("hoge\n"));
    }

    @Test
    void chomp_連続するCRLFで終わる文字列を渡すと末尾のCRLFのみ削除される() throws Exception {
        // Set up fixture
        String target = "hoge\r\n\r\n";

        // Exercise SUT
        String actual = StringUtil.chomp(target);

        // Verify outcome
        assertThat(actual, is("hoge\r\n"));
    }

}
