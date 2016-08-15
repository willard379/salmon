package jp.ambrosoli.salmon.utils;

import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.MatcherAssert.*;

import java.io.File;

import org.junit.gen5.api.Assumptions;
import org.junit.gen5.api.BeforeEach;
import org.junit.gen5.api.Nested;
import org.junit.gen5.api.Test;
import org.junit.gen5.junit4.runner.JUnit5;
import org.junit.runner.RunWith;

@RunWith(JUnit5.class)
@SuppressWarnings("nls")
class PlatformUtilTest {

    @Nested
    class Windows {

        @BeforeEach
        void setUp() {
            Assumptions.assumeTrue(System.getProperty("os.name").toLowerCase().contains("windows"));
        }

        @Test
        void isWindows() throws Exception {
            assertThat(PlatformUtil.isWindows(), is(true));
        }

        @Test
        void isUnix() {
            assertThat(PlatformUtil.isUnix(), is(false));
        }

        @Test
        void getDevNull() throws Exception {
            // Exercise SUT
            File devNull = PlatformUtil.devNull();

            // Verify outcome
            assertThat(devNull.toString(), is("nul"));
        }

        @Test
        void pathSeparator() throws Exception {
            // Exercise SUT
            String pathSeparator = PlatformUtil.pathSeparator();

            // Verify outcome
            assertThat(pathSeparator, is(";"));
        }

        @Test
        void pathKey() throws Exception {
            // Exercise SUT
            String pathKey = PlatformUtil.pathKey();

            // Verify outcome
            assertThat(pathKey, is("Path"));
        }
    }

    @Nested
    class Linux {

        @BeforeEach
        void setUp() {
            Assumptions.assumeFalse(System.getProperty("os.name").toLowerCase().contains("windows"));
        }

        @Test
        void isWindows() throws Exception {
            assertThat(PlatformUtil.isWindows(), is(false));
        }

        @Test
        void isUnix() {
            assertThat(PlatformUtil.isUnix(), is(true));
        }

        @Test
        void getDevNull() throws Exception {
            // Exercise SUT
            File devNull = PlatformUtil.devNull();

            // Verify outcome
            assertThat(devNull.toString(), is("/dev/null"));
        }

        @Test
        void pathSeparator() throws Exception {
            // Exercise SUT
            String pathSeparator = PlatformUtil.pathSeparator();

            // Verify outcome
            assertThat(pathSeparator, is(":"));
        }

        @Test
        void pathKey() throws Exception {
            // Exercise SUT
            String pathKey = PlatformUtil.pathKey();

            // Verify outcome
            assertThat(pathKey, is("PATH"));
        }
    }

}
