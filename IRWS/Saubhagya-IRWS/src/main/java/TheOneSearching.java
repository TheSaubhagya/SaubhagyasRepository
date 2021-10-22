

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.io.PrintWriter;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.core.SimpleAnalyzer;
import org.apache.lucene.analysis.core.WhitespaceAnalyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.analysis.en.EnglishAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.queryparser.classic.MultiFieldQueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.search.similarities.*;
import org.apache.lucene.store.FSDirectory;

/** Here's showing the search command */
public class TheOneSearching {

    public void searchMethod() throws Exception {

        String index = "Indexes_Store";
        String queryString = "";

        IndexReader reader = DirectoryReader.open(FSDirectory.open(Paths.get(index)));
        IndexSearcher searcher = new IndexSearcher(reader);

       
        Analyzer analyzer = new EnglishAnalyzer();

        String results_path = "results.txt";
        PrintWriter writer = new PrintWriter(results_path, "UTF-8");

        //BM25 Similarity
        searcher.setSimilarity(new BM25Similarity());

        

        String queriesPath = "cran/cran.qry";
        BufferedReader bufferedReader = Files.newBufferedReader(Paths.get(queriesPath), StandardCharsets.UTF_8);
        MultiFieldQueryParser parser = new MultiFieldQueryParser(new String[]{"title", "author", "bibliography", "words"}, analyzer);

        String currentLine = bufferedReader.readLine();

        System.out.println("Search results being created. Have patience.");

        String id = "";
        int i=0;

        while (currentLine != null) {
            i++;
            if (currentLine.startsWith(".I")) {
                id = Integer.toString(i);
                currentLine = bufferedReader.readLine();
            }
            if (currentLine.startsWith(".W")) {
                currentLine = bufferedReader.readLine();
                while (currentLine != null && !currentLine.startsWith(".I")) {
                    queryString += currentLine + " ";
                    currentLine = bufferedReader.readLine();
                }
            }
            queryString = queryString.trim();
            Query query = parser.parse(QueryParser.escape(queryString));
            queryString = "";
            searchDoing(searcher, writer, Integer.parseInt(id), query);
        }

        System.out.println("Cool. Now check the 'results.txt' file. :) ");
        writer.close();
        reader.close();
    }


    // Searching and creating into a text file
    public static void searchDoing(IndexSearcher searcher, PrintWriter writer, Integer queryNumber, Query query) throws IOException {
        /*
         * Only 1000 out of 1400 hits have been kept in order to achieve higher efficiency
        
         */
        TopDocs results = searcher.search(query, 1000);
        ScoreDoc[] hits = results.scoreDocs;

        //Results writing for each of the hits. 
        for (int i = 0; i < hits.length; i++) {
            Document doc = searcher.doc(hits[i].doc);
            writer.println(queryNumber + " 0 " + doc.get("id") + " " + i + " " + hits[i].score + " Saubhagya");
        }
    }
}
