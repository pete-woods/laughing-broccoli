package com.gildedrose;


import org.junit.Test;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Paths;

import static java.nio.charset.StandardCharsets.UTF_8;
import static org.assertj.core.api.Assertions.assertThat;


public class TextTest {
    private static String expectedOutput(String path) throws IOException, URISyntaxException {
        return new String(Files.readAllBytes(Paths.get(TextTest.class.getResource(path).toURI())), UTF_8);
    }

    private static String texttestFixtureOutput(String... args) throws IOException {
        try (var out = new ByteArrayOutputStream()) {
            try (var printStream = new PrintStream(out)) {
                TexttestFixture.print(printStream, args);
            }
            return out.toString();
        }
    }

    @Test
    public void testTextOutput() throws Exception {
        assertThat(texttestFixtureOutput()).isEqualTo(expectedOutput("/original-output.txt"));
    }

    @Test
    public void testTextOutput3() throws Exception {
        assertThat(texttestFixtureOutput("3")).isEqualTo(expectedOutput("/original-output-3.txt"));
    }

    @Test
    public void testTextOutput10() throws Exception {
        assertThat(texttestFixtureOutput("10")).isEqualTo(expectedOutput("/original-output-10.txt"));
    }
}
