package assignment;

import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.*;

/**
 * A web-index which efficiently stores information about pages. Serialization is done automatically
 * via the superclass "Index" and Java's Serializable interface.
 *
 * TODO: Implement this!
 */
public class WebIndex extends Index {
    /**
     * Needed for Serialization (provided by Index) - don't remove this!
     */
    private static final long serialVersionUID = 1L;
    private HashMap<String, HashMap<Page, HashSet<Integer>>> pages;
    private HashSet<Page> visitedPages; // All pages that were crawled

    public WebIndex() {
        pages = new HashMap<>();
        visitedPages = new HashSet<>();
    }

    // Getter method for all pages crawled
    public HashSet<Page> getPages() {
        return this.visitedPages;
    }

    public void addPage(Page page) {
        visitedPages.add(page);
    }

    // Add a word to the pages HashMap
    public void addWord(Page page, String word, int wordCount) {
        if (pages.containsKey(word)) {
            HashMap<Page, HashSet<Integer>> wordPages = pages.get(word);

            // Check if HashSet already present for the word
            if (wordPages.containsKey(page)) {
                wordPages.get(page).add(wordCount);
            } else {
                HashSet<Integer> set = new HashSet<>();
                set.add(wordCount);
                wordPages.put(page, set);
            }
        } else {
            HashMap<Page, HashSet<Integer>> wordPages = new HashMap<>();

            HashSet<Integer> set = new HashSet<>();
            set.add(wordCount);

            wordPages.put(page, set);
            pages.put(word, wordPages);
        }
    }

    private Set<Page> getPagesWord(String word) {
        if (word.charAt(0) == '!') { // Check for negation query
            word = word.substring(1);

            if (!pages.containsKey(word)) {
                return new HashSet<>();
            }

            Set<Page> wordPages = pages.get(word).keySet();
            HashSet<Page> ans = new HashSet<>();

            // Get all pages that are not returned by the query w/out negation
            for (Page page : visitedPages) {
                if (!wordPages.contains(page)) {
                    ans.add(page);
                }
            }

            return ans;
        }

        // Edge case if word is not in index at all
        if (!pages.containsKey(word)) {
            return new HashSet<>();
        }

        // No negation case
        return pages.get(word).keySet();
    }

    // Recursive helper method for phrase queries
    private boolean queryHelper(ArrayList<String> data, Page currPage, int index, int prevLocation) {
        if (index == data.size()) { // We reached the end of the List
            return true;
        }

        if (pages.get(data.get(index)) == null) { // The current word does not exist for any page
            return false;
        }

        // Check for consecutive words in a phrase
        if (pages.get(data.get(index)).containsKey(currPage) && pages.get(data.get(index)).get(currPage).contains(prevLocation+1)) {
            return queryHelper(data, currPage, index + 1, prevLocation+1);
        }

        return false;
    }

    public Set<Page> getPagesForQuery(String data) {
        Set<Page> ans = new HashSet<>();

        // Phrase queries
        if (data.charAt(0) == '"' && data.charAt(data.length() - 1) == '"') {
            data = data.substring(1, data.length() - 1);

            StringTokenizer st = new StringTokenizer(data);
            ArrayList<String> tokens = new ArrayList<>();

            // Used for case-insensitivity
            while (st.hasMoreTokens()) {
                tokens.add(st.nextToken().toLowerCase());
            }

            if (!pages.containsKey(tokens.get(0))) {
                return ans;
            }

            // Iterate through all pages for the word and their respective relative locations
            for (Page page : pages.get(tokens.get(0)).keySet()) {
                for (int location : pages.get(tokens.get(0)).get(page)) {
                    if (queryHelper(tokens, page, 1, location)) {
                        ans.add(page);
                    }
                }
            }

            return ans;
        }

        // Non-phrase queries
        return getPagesWord(data);
    }
}


