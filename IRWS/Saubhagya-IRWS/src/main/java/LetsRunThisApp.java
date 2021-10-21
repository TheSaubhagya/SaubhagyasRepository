/*
 * An entry point to the entire program with the 'main()' method.
 */

public class LetsRunThisApp {
    public static void main (String[] args) throws Exception {
        Indexer letsinsdex = new Indexer();
        Searcher letssearch = new Searcher();

        letsinsdex.indexMethod();
        letssearch.searchMethod();
    }
}
