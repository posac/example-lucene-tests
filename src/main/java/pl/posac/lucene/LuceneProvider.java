package pl.posac.lucene;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.RAMDirectory;


import java.io.File;
import java.io.IOException;
import java.util.Arrays;

public class LuceneProvider {

    private static LuceneProvider INSTANCE;

    private final RAMDirectory index;
    private final StandardAnalyzer analyzer;

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
        Arrays.stream(mapper.readValue(new File(documentsPath), City[].class))
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
        doc.add(new TextField("name", city.getName(), Field.Store.YES));
        doc.add(new StringField("country", city.getCountry(), Field.Store.YES));
        if(city.getAlternativeNames()!=null)
            city.getAlternativeNames()
                    .forEach(value -> doc.add(new TextField("alternative_name", value, Field.Store.YES)));
        return doc;
    }

    public Object getIndex() {
        Directory dir = new RAMDirectory();

        return null;

    }
}
