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
    void shouldReturnExactMatchWithId() throws Exception {

        String text = "PLWRO";

        List<SearchRepository.ScoredResult> results = searchRepository.search(text);

        assertThat(results).anySatisfy(t -> assertThat(t.id).isEqualTo("PLWRO"));

    }

    @Test
    void shouldReturnExactMatchWithName() throws Exception {

        String text = "Warszawa";

        List<SearchRepository.ScoredResult> results = searchRepository.search(text);

        assertThat(results).anySatisfy(t -> assertThat(t.id).isEqualTo("PLWAR"));

    }

    @Test
    void shouldReturnExactMatchWithAlternativeName() throws Exception {

        String text = "Wawa";

        List<SearchRepository.ScoredResult> results = searchRepository.search(text);

        assertThat(results).anySatisfy(t -> assertThat(t.id).isEqualTo("PLWAR"));

    }


    @Test
    void shouldReturnReturnAllFromCountry() throws Exception {

        String text = "DE";

        List<SearchRepository.ScoredResult> results = searchRepository.search(text);

        assertThat(results.stream().map(t -> t.id).collect(Collectors.toList()))
                .containsExactly("DEBER", "DEMON");

    }


    @Test
    void shouldBeNotCaseSensitive() throws Exception {

        String text = "POZNAN";

        List<SearchRepository.ScoredResult> results = searchRepository.search(text);

        assertThat(results).anySatisfy(t -> assertThat(t.id).isEqualTo("PLPOZ"));

    }

    @Test
    void shouldNotMakeFuzzySearchOnId() throws Exception {

        String text = "N2Y";

        List<SearchRepository.ScoredResult> results = searchRepository.search(text);

        assertThat(results).isEmpty();

    }

    @Test
    void shouldMakeWildCardSearchInNamePrefixed() throws Exception {

        String text = "WRO";

        List<SearchRepository.ScoredResult> results = searchRepository.search(text);

        assertThat(results).anySatisfy(t -> assertThat(t.id).isEqualTo("PLWRO"));

    }

    @Test
    void shouldMakeWildCardSearchInNameSurfixed() throws Exception {

        String text = "claw";

        List<SearchRepository.ScoredResult> results = searchRepository.search(text);

        assertThat(results).anySatisfy(t -> assertThat(t.id).isEqualTo("PLWRO"));

    }

    @Test
    void shouldMakeWildCardSearchInAlternativeNames() throws Exception {

        String text = "slau";

        List<SearchRepository.ScoredResult> results = searchRepository.search(text);

        assertThat(results).anySatisfy(t -> assertThat(t.id).isEqualTo("PLWRO"));

    }

    @Test
    void shouldPartsOfIdInTokensAndMergeThem() throws Exception {

        String text = "US N2Y";

        List<SearchRepository.ScoredResult> results = searchRepository.search(text);

        assertThat(results).anySatisfy(t -> assertThat(t.id).isEqualTo("USN2Y"));

    }
}
