/*
 * This is the main Program to be run. 
 */

public class LetsRunThisApp {
    public static void main (String[] args) throws Exception {
        AllIndexFunctions letsinsdex = new AllIndexFunctions();
        TheOneSearching letssearch = new TheOneSearching();

        letsinsdex.methodForIndex();
        letssearch.methodForSearch();
    }
}
