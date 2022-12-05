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

    public Set<Page> getPagesForQuery(ArrayList<String> data, boolean isPhrase) {
        Set<Page> ans = getPagesWord(data.get(0));

        if (ans.size() == 0) {
            return ans;
        }

        if (isPhrase) {
            ans = new HashSet<>();

            for (Page page : pages.get(data.get(0)).keySet()) {
                for (int location : pages.get(data.get(0)).get(page)) {
                    if (queryHelper(data, page, 1, location)) {
                        ans.add(page);
                    }
                }
            }
        } else {
            if (ans == null) {
                return new HashSet<>();
            }

            for (int i = 1; i < data.size(); i++) {
                Set<Page> pagesForWord = getPagesWord(data.get(i));
                Set<Page> ans2 = new HashSet<>();

                for (Page page : ans) {
                    if (pagesForWord.contains(page)) {
                        ans2.add(page);
                    }
                }
                ans = ans2;
            }
        }

        return ans;
    }
}


