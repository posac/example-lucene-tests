package pl.posac.lucene;

import org.apache.lucene.document.Document;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.*;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

public class SearchRepository {

    private IndexSearcher searcher;

    public SearchRepository(IndexSearcher searcher) {
        this.searcher = searcher;
    }

    public List<ScoredResult> search(String text) throws IOException {
        List<ScoredResult> results = new LinkedList<>();
        Query query = getQuery(text);
        TopDocs hits = searcher.search(query, 10);
        for (ScoreDoc scoreDoc : hits.scoreDocs) {
            Document hitDoc = searcher.doc(scoreDoc.doc);
            results.add(new ScoredResult(hitDoc.get("id"), scoreDoc.score));
        }
        return results;
    }

    private Query getQuery(String text) {
        BooleanQuery result = new BooleanQuery.Builder()
                .add(new TermQuery(new Term("id", text)), BooleanClause.Occur.SHOULD)
                .add(new TermQuery(new Term("alternative_name", text)), BooleanClause.Occur.SHOULD)
                .add(new TermQuery(new Term("name", text)), BooleanClause.Occur.SHOULD)
                .add(new TermQuery(new Term("country", text)), BooleanClause.Occur.SHOULD)
                .build();
        return result;
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
