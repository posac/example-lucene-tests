package pl.posac.lucene;

import org.apache.lucene.document.Document;
import org.apache.lucene.index.Term;
import org.apache.lucene.queryparser.classic.MultiFieldQueryParser;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.*;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.function.Function;

public class SearchRepository {

    private IndexSearcher searcher;

    public SearchRepository(IndexSearcher searcher) {
        this.searcher = searcher;
    }

    public List<ScoredResult> search(String text) throws Exception {
        List<ScoredResult> results = new LinkedList<>();

        Query query = getQuery(text);
        TopDocs hits = searcher.search(query, 10);
        for (ScoreDoc scoreDoc : hits.scoreDocs) {
            Document hitDoc = searcher.doc(scoreDoc.doc);
            results.add(new ScoredResult(hitDoc.get("id"), scoreDoc.score));
        }
        return results;
    }

    private Query getMultiFieldQuery(String text) throws ParseException {
        QueryParser queryParser = new MultiFieldQueryParser(new String[]{"id", "name", "alternative_name", "country"}, new CustomAnalyzer());
        return queryParser.parse(text);
    }

    private Query getQuery(String text) throws IOException {
        List<String> tokens = LuceneUtil.getTokens(text);

        List<String> idTokens = getIdTokens(tokens);

        BooleanQuery result = new BooleanQuery.Builder()
                .add(createQueryForExactMatch("id", idTokens), BooleanClause.Occur.SHOULD)
                .add(createQueryForWildcardMatch("alternative_name", tokens), BooleanClause.Occur.SHOULD)
                .add(createQueryForWildcardMatch("name", tokens), BooleanClause.Occur.SHOULD)
                .add(createQueryForExactMatch("country", tokens), BooleanClause.Occur.SHOULD)
                .build();
        return result;
    }

    private List<String> getIdTokens(List<String> tokens) {
        List<String> results = new LinkedList<>();
        String prev = null;
        for (String token : tokens) {
            if (token.length() == 5) {
                results.add(token);
                prev = null;
            } else if (prev != null && prev.length() + token.length() == 5) {
                results.add(createId(prev, token));
                prev = null;
            } else {
                if (token.length() == 3 || token.length() == 2)
                    prev = token;
            }
        }
        return results;
    }

    private String createId(String prev, String token) {
        return prev.length() < token.length() ? prev + token : token + prev;
    }


    private Query createQueryForWildcardMatch(String field, List<String> tokens) {
        return createQueryJoining(tokens, (text) -> new WildcardQuery(new Term(field, "*" + text + "*")));
    }

    private Query createQueryForExactMatch(String field, List<String> idTokens) {
        return createQueryJoining(idTokens, (text) -> new TermQuery(new Term(field, text)));
    }

    private Query createQueryJoining(List<String> idTokens, Function<String, Query> createQuery) {
        BooleanQuery.Builder builder = new BooleanQuery.Builder();
        idTokens.forEach(text -> builder.add(createQuery.apply(text), BooleanClause.Occur.SHOULD));
        return builder.build();
    }


    public static class ScoredResult {
        String id;
        Float score;

        public ScoredResult(String id, Float score) {
            this.id = id;
            this.score = score;
        }

        @Override
        public String toString() {
            return "ScoredResult{" +
                    "id='" + id + '\'' +
                    ", score=" + score +
                    '}';
        }
    }
}
