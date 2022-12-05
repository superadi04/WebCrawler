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
    private Map<String, Map<Page, Set<Location>>> pages;
    private Set<Page> visitedPages;

    public WebIndex() {
        pages = new TreeMap<>();
        visitedPages = new HashSet<>();
    }

    public Set<Page> getPages() {
        return this.visitedPages;
    }

    public void addPage(Page page) {
        visitedPages.add(page);
    }

    public void addWord(Page page, String word, int line, int col) {
        if (pages.containsKey(word)) {
            Map<Page, Set<Location>> wordPages = pages.get(word);

            if (wordPages.containsKey(page)) {
                wordPages.get(page).add(new Location(line, col));
            } else {
                Set<Location> set = new HashSet<>();
                set.add(new Location(line, col));
                wordPages.put(page, set);
            }
        } else {
            Map<Page, Set<Location>> wordPages = new HashMap<>();

            Set<Location> set = new HashSet<>();
            set.add(new Location(line, col));

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
            Set<Page> ans = new HashSet<>();

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

    private void queryHelper(List<String> data, Set<Page> currPages, int index, Page prevPage, Location prevLocation) {
        if (index == data.size()) {
            currPages.add(prevPage);
        }

        if (pages.get(data.get(index)) == null) {
            return;
        }

        if (pages.get(data.get(index)).containsKey(prevPage)) {
            for (Location currLocation : pages.get(data.get(index)).get(prevPage)) {
                if ((currLocation.line == prevLocation.line && prevLocation.col + data.get(index).length() + 1 == currLocation.col) || (prevLocation.line + 1 == currLocation.line && currLocation.col == 0)) {
                    queryHelper(data, currPages, index + 1, prevPage, currLocation);
                }
            }
        }
    }

    public Set<Page> getPagesForQuery(List<String> data, boolean isPhrase) throws MalformedURLException {
        Set<Page> ans = getPagesWord(data.get(0));

        if (ans.size() == 0) {
            return ans;
        }

        if (isPhrase) {
            ans = new HashSet<>();

            for (Page page : pages.get(data.get(0)).keySet()) {
                for (Location location : pages.get(data.get(0)).get(page)) {
                    HashSet<Page> addToAns = new HashSet<>();
                    queryHelper(data, addToAns, 1, page, location);
                    ans.addAll(addToAns);
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

    private class Location {
        private int line;
        private int col;

        private Location(int line, int col) {
            this.line = line;
            this.col = col;
        }

        @Override
        public boolean equals(Object obj) {
            Location l = (Location) obj;
            return this.line == l.line && this.col == l.col;
        }
    }
}


