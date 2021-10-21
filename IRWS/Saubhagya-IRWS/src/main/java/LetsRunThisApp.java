/*
 * An entry point to the entire program with the 'main()' method.
 */

public class LetsRunThisApp {
    public static void main (String[] args) throws Exception {
        AllIndexFunctions letsinsdex = new Indexer();
        TheOneSearching letssearch = new Searcher();

        letsinsdex.indexMethod();
        letssearch.searchMethod();
    }
}
