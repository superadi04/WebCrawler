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
    private HashSet<Page> visitedPages;

    public WebIndex() {
        pages = new HashMap<>();
        visitedPages = new HashSet<>();
    }

    public HashSet<Page> getPages() {
        return this.visitedPages;
    }

    public void addPage(Page page) {
        visitedPages.add(page);
    }

    public void addWord(Page page, String word, int wordCount) {
        if (pages.containsKey(word)) {
            HashMap<Page, HashSet<Integer>> wordPages = pages.get(word);

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
        if (word.charAt(0) == '!') {
            word = word.substring(1);

            if (!pages.containsKey(word)) {
                return new HashSet<>();
            }

            Set<Page> wordPages = pages.get(word).keySet();
            HashSet<Page> ans = new HashSet<>();

            for (Page page : visitedPages) {
                if (!wordPages.contains(page)) {
                    ans.add(page);
                }
            }

            return ans;
        }

        if (!pages.containsKey(word)) {
            return new HashSet<>();
        }

        return pages.get(word).keySet();
    }

    private boolean queryHelper(ArrayList<String> data, Page currPage, int index, int prevLocation) {
        if (index == data.size()) {
            return true;
        }

        if (pages.get(data.get(index)) == null) {
            return false;
        }

        if (pages.get(data.get(index)).containsKey(currPage) && pages.get(data.get(index)).get(currPage).contains(prevLocation+1)) {
            return queryHelper(data, currPage, index + 1, prevLocation+1);
        }

        return false;
    }

    public Set<Page> getPagesForQuery(String data) {
        Set<Page> ans = new HashSet<>();

        if (data.charAt(0) == '"' && data.charAt(data.length() - 1) == '"') {
            data = data.substring(1, data.length() - 1);

            StringTokenizer st = new StringTokenizer(data);
            ArrayList<String> tokens = new ArrayList<>();

            while (st.hasMoreTokens()) {
                tokens.add(st.nextToken().toLowerCase());
            }

            if (!pages.containsKey(tokens.get(0))) {
                return ans;
            }

            for (Page page : pages.get(tokens.get(0)).keySet()) {
                for (int location : pages.get(tokens.get(0)).get(page)) {
                    if (queryHelper(tokens, page, 1, location)) {
                        ans.add(page);
                    }
                }
            }

            return ans;
        }

        return getPagesWord(data);
    }
}


