package pl.posac.lucene;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;

public class QueryTest {

    SearchRepository searchRepository = new SearchRepository(LuceneProvider.getInstance(Constants.RESOURCE).getIndexSearcher());

    public QueryTest() throws IOException {
    }

    @Test
    void shouldReturnExactMatchWithId() throws IOException {

        String text = "PLWRO";

        List<SearchRepository.ScoredResult> results = searchRepository.search(text);

        assertThat(results).anySatisfy(t -> assertThat(t.id).isEqualTo("PLWRO"));

    }

    @Test
    void shouldReturnExactMatchWithName() throws IOException {

        String text = "Warszawa";

        List<SearchRepository.ScoredResult> results = searchRepository.search(text);

        assertThat(results).anySatisfy(t -> assertThat(t.id).isEqualTo("PLWAR"));

    }

    @Test
    void shouldReturnExactMatchWithAlternativeName() throws IOException {

        String text = "Wawa";

        List<SearchRepository.ScoredResult> results = searchRepository.search(text);

        assertThat(results).anySatisfy(t -> assertThat(t.id).isEqualTo("PLWAR"));

    }


    @Test
    void shouldReturnReturnAllFromCountry() throws IOException {

        String text = "DE";

        List<SearchRepository.ScoredResult> results = searchRepository.search(text);

        assertThat(results.stream().map(t -> t.id).collect(Collectors.toList()))
                .containsExactly("DEBER", "DEMON");

    }


}
