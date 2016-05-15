package jp.ambrosoli.salmon.config;

import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.MatcherAssert.*;
import static org.junit.gen5.api.Assertions.*;

import org.junit.gen5.api.AfterEach;
import org.junit.gen5.api.Test;
import org.junit.gen5.junit4.runner.JUnit5;
import org.junit.runner.RunWith;

import jp.ambrosoli.salmon.test.util.SalmonConfigAccessor;

@RunWith(JUnit5.class)
@SuppressWarnings("nls")
class SalmonConfigTest {

    @AfterEach
    void tearDown() throws Exception {
        SalmonConfigAccessor.undeploy();
    }

    @Test
    void salmon_propertiesが存在しない場合_デフォルト値がロードされること() throws Exception {
        // Exercise
        SalmonConfig.initialize();

        // Verify
        assertAll(() -> {
            assertThat(SalmonConfig.isAutoMSDos(), is(false));
        });
    }

    @Test
    void salmon_propertiesが存在する場合_設定値がロードされること() throws Exception {
        // Setup
        SalmonConfigAccessor.deploy(this, "salmon.properties.test");

        // Exercise
        SalmonConfig.initialize();

        // Verify
        assertAll(() -> {
            assertThat(SalmonConfig.isAutoMSDos(), is(true));
        });
    }

    @Test
    void salmon_automsdosの設定値がない場合_isAutoMSDosがfalseを返すこと() throws Exception {
        // Setup
        SalmonConfigAccessor.deploy(this, "salmon.properties.empty");

        // Exercise
        SalmonConfig.initialize();

        // Verify
        assertThat(SalmonConfig.isAutoMSDos(), is(false));
    }

    @Test
    void salmon_automsdosの設定値が不正な場合_isAutoMSDosがfalseを返すこと() throws Exception {
        // Setup
        SalmonConfigAccessor.deploy(this, "salmon.properties.invalid");

        // Exercise
        SalmonConfig.initialize();

        // Verify
        assertThat(SalmonConfig.isAutoMSDos(), is(false));
    }

}
