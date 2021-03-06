package fr.techad.edc.httpd.search;

import fr.techad.edc.httpd.WebServerConfig;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.queryparser.classic.MultiFieldQueryParser;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * TECH ADVANTAGE
 * All right reserved
 * Created by cochon on 24/04/2018.
 */
public class ContentSearcher extends ContentBase {
    private static final Logger LOGGER = LoggerFactory.getLogger(ContentSearcher.class);
    private static final String[] SEARCH_FIELDS = {DOC_LABEL, DOC_CONTENT, DOC_TYPE};
    private static final Map<String, Float> BOOTS;

    static {
        Map<String, Float> aMap = new HashMap<>();
        aMap.put(DOC_LABEL, 2f);
        aMap.put(DOC_CONTENT, 1f);
        aMap.put(DOC_TYPE, .5f);
        BOOTS = Collections.unmodifiableMap(aMap);
    }

    private IndexSearcher indexSearcher;

    public ContentSearcher(WebServerConfig webServerConfig) {
        super(webServerConfig);
    }

    /**
     * Search in the help content.
     *
     * @param search the query search
     * @return the list of result
     * @throws IOException    is an error is occurred to read indexed file
     * @throws ParseException if the search parameter is malformed
     */
    public List<DocumentationSearchResult> search(String search) throws IOException, ParseException {
        List<DocumentationSearchResult> results = new ArrayList<>();
        LOGGER.debug("Search {}", search);
        createSearcher();
        QueryParser qp = new MultiFieldQueryParser(SEARCH_FIELDS, new StandardAnalyzer(), BOOTS);
        Query query = qp.parse(search);

        TopDocs hits = indexSearcher.search(query, 100);
        LOGGER.debug("Found {} results for the search '{}'", hits.totalHits, search);

        for (ScoreDoc sd : hits.scoreDocs) {
            Document d = indexSearcher.doc(sd.doc);
            DocumentationSearchResult documentationSearchResult = new DocumentationSearchResult();
            String idStr = d.get(DOC_ID);
            documentationSearchResult.setId(Long.valueOf(idStr));
            documentationSearchResult.setLabel(d.get(DOC_LABEL));
            documentationSearchResult.setStrategyId(Long.valueOf(d.get(DOC_STRATEGY_ID)));
            documentationSearchResult.setStrategyLabel(d.get(DOC_STRATEGY_LABEL));
            documentationSearchResult.setLanguageCode(d.get(DOC_LANGUAGE_CODE));
            documentationSearchResult.setUrl(d.get(DOC_URL));
            documentationSearchResult.setType(d.get(DOC_TYPE));
            results.add(documentationSearchResult);
        }
        return results;
    }

    private void createSearcher() throws IOException {
        if (indexSearcher == null) {
            Directory dir = FSDirectory.open(getIndexPath());
            IndexReader reader = DirectoryReader.open(dir);
            indexSearcher = new IndexSearcher(reader);
        }
    }
}
