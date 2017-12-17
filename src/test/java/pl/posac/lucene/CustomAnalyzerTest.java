package pl.posac.lucene;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class CustomAnalyzerTest {

    @Test
    void shouldUpperCaseWords() throws IOException {
        String text = "test xx sss";

        List<String> tokens = LuceneUtil.getTokens(text);

        assertThat(tokens).containsExactly("TEST", "XX", "SSS");
    }


}