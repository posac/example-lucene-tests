package pl.posac.lucene;

import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

class LuceneProviderTest {


    @Test
    public void shouldCreateInstanceOfProvider() throws IOException {
        //given

        //when
        LuceneProvider instance = LuceneProvider.getInstance("src/test/resources/citiesToIndex.json");
        //then
        assertNotNull(instance);
    }

}