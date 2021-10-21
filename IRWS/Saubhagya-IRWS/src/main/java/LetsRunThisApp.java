/*
 * This is the main Program to be run. 
 */

public class LetsRunThisApp {
    public static void main (String[] args) throws Exception {
        AllIndexFunctions letsinsdex = new Indexer();
        TheOneSearching letssearch = new Searcher();

        letsinsdex.indexMethod();
        letssearch.searchMethod();
    }
}
