package pl.posac.lucene;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.StringField;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.store.RAMDirectory;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

public class LuceneProvider {

    private static LuceneProvider INSTANCE;

    private final RAMDirectory index;
    private final StandardAnalyzer analyzer;
    private List<City> cities;

    private LuceneProvider(String documentsPath) throws IOException {

        analyzer = new StandardAnalyzer();
        index = new RAMDirectory();

        IndexWriterConfig config = new IndexWriterConfig(analyzer);
        IndexWriter w = new IndexWriter(index, config);
        createAndAddDocumentsFromCSV(w, documentsPath);
        w.close();

    }

    public static LuceneProvider getInstance(String documentsPath) throws IOException {
        if (INSTANCE == null) {
            INSTANCE = new LuceneProvider(documentsPath);
        }
        return INSTANCE;
    }

    private void createAndAddDocumentsFromCSV(IndexWriter indexWriter, String documentsPath) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        mapper.setPropertyNamingStrategy(
                PropertyNamingStrategy.SNAKE_CASE);
        this.cities = Arrays.asList(mapper.readValue(new File(documentsPath), City[].class));
        cities.stream()
                .map(this::createDocument)
                .forEach(t -> {
                    try {
                        indexWriter.addDocument(t);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                });
    }


    private Document createDocument(City city) {
        Document doc = new Document();
        doc.add(new StringField("id", city.getId(), Field.Store.YES));
        doc.add(new StringField("name", city.getName(), Field.Store.YES));
        doc.add(new StringField("country", city.getCountry(), Field.Store.YES));
        if(city.getAlternativeNames()!=null)
            city.getAlternativeNames()
                    .forEach(value -> doc.add(new StringField("alternative_name", value, Field.Store.YES)));
        return doc;
    }


    public IndexSearcher getIndexSearcher() throws IOException {
        DirectoryReader reader = DirectoryReader.open(index);
        return new IndexSearcher(reader);
    }
}
