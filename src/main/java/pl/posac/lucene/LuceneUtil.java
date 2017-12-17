package pl.posac.lucene;

import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;

import java.io.IOException;
import java.io.StringReader;
import java.util.LinkedList;
import java.util.List;

public class LuceneUtil {


    public static List<String> getTokens(String text) throws IOException {
        List<String> tokens = new LinkedList<>();
        CustomAnalyzer analyzer = new CustomAnalyzer();
        TokenStream stream = analyzer.tokenStream("field", new StringReader(text));

        // get the CharTermAttribute from the TokenStream
        CharTermAttribute termAtt = stream.addAttribute(CharTermAttribute.class);

        try {
            stream.reset();
            while (stream.incrementToken()) {
                tokens.add(termAtt.toString());
            }


            stream.end();
        } finally {
            stream.close();
        }
        return tokens;
    }
}
