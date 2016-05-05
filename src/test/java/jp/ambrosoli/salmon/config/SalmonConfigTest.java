package jp.ambrosoli.salmon.config;

import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.MatcherAssert.*;
import static org.junit.gen5.api.Assertions.*;

import org.junit.gen5.api.AfterEach;
import org.junit.gen5.api.Test;
import org.junit.gen5.junit4.runner.JUnit5;
import org.junit.runner.RunWith;

@RunWith(JUnit5.class)
@SuppressWarnings("nls")
public class SalmonConfigTest {

    @AfterEach
    void tearDown() throws Exception {
        SalmonConfigAccessor.undeploy();
    }

    @Test
    public void salmon_propertiesが存在しない場合_デフォルト値がロードされること() throws Exception {
        // Exercise
        SalmonConfig.initialize();

        // Verify
        assertAll(() -> {
            assertThat(SalmonConfig.isAutoMSDos(), is(false));
        });
    }

    @Test
    public void salmon_propertiesが存在する場合_設定値がロードされること() throws Exception {
        // Setup
        SalmonConfigAccessor.deploy("salmon.properties.test");

        // Exercise
        SalmonConfig.initialize();

        // Verify
        assertAll(() -> {
            assertThat(SalmonConfig.isAutoMSDos(), is(true));
        });
    }

    @Test
    public void salmon_automsdosの設定値がない場合_isAutoMSDosがfalseを返すこと() throws Exception {
        // Setup
        SalmonConfigAccessor.deploy("salmon.properties.empty");

        // Exercise
        SalmonConfig.initialize();

        // Verify
        assertThat(SalmonConfig.isAutoMSDos(), is(false));
    }

    @Test
    public void salmon_automsdosの設定値が不正な場合_isAutoMSDosがfalseを返すこと() throws Exception {
        // Setup
        SalmonConfigAccessor.deploy("salmon.properties.invalid");

        // Exercise
        SalmonConfig.initialize();

        // Verify
        assertThat(SalmonConfig.isAutoMSDos(), is(false));
    }

}
