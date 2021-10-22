
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Date;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.en.EnglishAnalyzer;
import org.apache.lucene.document.*;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig.OpenMode;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.search.similarities.*;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;

public class AllIndexFunctions {

    /** We shall be indexing files here */
    public void methodForIndex() {
        String indexPath = "Indexes_Store";
        String documentAddress = "cran/cran.all.1400";

        final Path documentCase = Paths.get(documentAddress);

        if (!Files.isReadable(documentCase)) {
            System.out.println(documentCase.toAbsolutePath() + "' MISSING.");
            System.exit(1);
        }

        Date start = new Date();
        try {
            System.out.println("Indexing to directory '" + indexPath + "'...");

            Directory dir = FSDirectory.open(Paths.get(indexPath));

            
            Analyzer analyzer = new EnglishAnalyzer();

            IndexWriterConfig iwc = new IndexWriterConfig(analyzer);

            //Setting the BM25 Similarity
            iwc.setSimilarity(new BM25Similarity());

            

            iwc.setOpenMode(OpenMode.CREATE);

            IndexWriter writer = new IndexWriter(dir, iwc);
            documentingIndex(writer, documentCase);

            //Increases efficiency (https://www.tabnine.com/code/java/methods/org.apache.lucene.index.IndexWriter/forceMerge)
            writer.forceMerge(1);

            writer.close();

            Date end = new Date();
            System.out.println(end.getTime() - start.getTime() + " total milliseconds");

        } catch (IOException e) {
            System.out.println(" caught a " + e.getClass() + "\n with message: " + e.getMessage());
        }
    }

    /** Now, Indexing the cran.all.1400 file */
    static void documentingIndex(IndexWriter writer, Path file) throws IOException {
        try (InputStream stream = Files.newInputStream(file)) {

            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(stream, StandardCharsets.UTF_8));
            Boolean first = true;
            System.out.println("Indexing documents.");

            String thisArea = bufferedReader.readLine();
            String fullText = "";

            while(thisArea != null){
                Document doc = new Document();
                switch(thisArea.substring(0, 2)){

                case ".I":
                    
                    doc.add(new StringField("id", thisArea.substring(3), Field.Store.YES));
                    thisArea = bufferedReader.readLine();
                
                case ".T":
                    thisArea = bufferedReader.readLine();
                    while(!thisArea.startsWith(".A")){
                        fullText += thisArea + " ";
                        thisArea = bufferedReader.readLine();
                    }
                    doc.add(new TextField("title", fullText, Field.Store.YES));
                    fullText = "";
                
                case ".A":
                    thisArea = bufferedReader.readLine();
                    while(!thisArea.startsWith(".B")){
                        fullText += thisArea + " ";
                        thisArea = bufferedReader.readLine();
                    }
                    doc.add(new TextField("author", fullText, Field.Store.YES));
                    fullText = "";
                
                case ".B":
                    thisArea = bufferedReader.readLine();
                    while(!thisArea.startsWith(".W")){
                        fullText += thisArea + " ";
                        thisArea = bufferedReader.readLine();
                    }
                    /*
                     * For bibliography 
          
                     */
                    doc.add(new StringField("bibliography", fullText, Field.Store.YES));
                    fullText = "";
                
                case ".W":
                    thisArea = bufferedReader.readLine();
                    while(thisArea != null && !thisArea.startsWith(".I")){
                        fullText += thisArea + " ";
                        thisArea = bufferedReader.readLine();
                    }
                    //Storage optimization
                    doc.add(new TextField("words", fullText, Field.Store.NO));
                    fullText = "";
                }
                writer.addDocument(doc);
            }
        }
    }
}
