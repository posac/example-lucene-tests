package pl.posac.lucene;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.Tokenizer;
import org.apache.lucene.analysis.core.UpperCaseFilter;
import org.apache.lucene.analysis.core.WhitespaceTokenizer;

public class CustomAnalyzer extends Analyzer {

    @Override
    protected TokenStreamComponents createComponents(String field) {
        final Tokenizer source = new WhitespaceTokenizer();
        TokenStream result = new UpperCaseFilter(source);
        return new TokenStreamComponents(source, result);
//        return new TokenStreamComponents(source);
    }

    @Override
    protected TokenStream normalize(String fieldName, TokenStream in) {
        return new UpperCaseFilter(in);
    }
}
