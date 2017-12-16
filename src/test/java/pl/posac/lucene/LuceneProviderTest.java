package pl.posac.lucene;

import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertNotNull;

class LuceneProviderTest {


    @Test
    public void shouldCreateInstanceOfProvider() throws IOException {
        //given

        //when
        LuceneProvider instance = LuceneProvider.getInstance(Constants.RESOURCE);
        //then
        assertNotNull(instance);
    }


    @Test
    public void shouldReturnSearchIndex() throws IOException {
        assertNotNull(LuceneProvider.getInstance(Constants.RESOURCE).getIndexSearcher());
    }
}