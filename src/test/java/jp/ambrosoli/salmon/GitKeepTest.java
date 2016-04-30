package jp.ambrosoli.salmon;

import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.Matchers.*;

import org.junit.gen5.api.Test;
import org.junit.gen5.junit4.runner.JUnit5;
import org.junit.runner.RunWith;

@RunWith(JUnit5.class)
public class GitKeepTest {

    @Test
    public void testGreeting() {
        // Setup
        GitKeep sut = new GitKeep();
        String name = "willard379";

        // Exercise
        String actual = sut.greeting(name);

        // Verify
        assertThat(actual, is("Hello, willard379!"));
    }

}
