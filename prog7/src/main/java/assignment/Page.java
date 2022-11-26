package assignment;
import java.io.BufferedReader;
import java.net.URL;
import java.util.*;

/**
 * The Page class holds anything that the QueryEngine returns to the server.  The field and method
 * we provided here is the bare minimum requirement to be a Page - feel free to add anything you
 * want as long as you don't break the getURL method.
 *
 * TODO: Implement this!
 */
public class Page {
    // The URL the page was located at.
    private URL url;

    private Map<String, Set<String>> map;

    /**
     * Creates a Page with a given URL.
     * @param url The url of the page.
     */
    public Page(URL url) {
        this.url = url;
        map = new HashMap<>();
    }

    /**
     * @return the URL of the page.
     */
    public URL getURL() { return url; }

    @Override
    public boolean equals(Object obj) {
        Page p = (Page) obj;
        return this.url.equals(p.url);
    }

    public boolean checkQuery(String word) {
        if (word.charAt(0) == '"' && word.charAt(word.length() - 1) == '"') {
            return containsPhrase(word);
        }

        return implicitAnd(word);
    }

    public boolean containsPhrase(String word) {
        word = word.substring(1, word.length() - 1);

        StringTokenizer st = new StringTokenizer(word);

        String curr = st.nextToken();

        while (st.hasMoreTokens()) {
            String next = st.nextToken();
            if (!map.containsKey(curr) || (map.containsKey(curr) && !map.get(curr).contains(next))) {
                return false;
            }
            curr = next;
        }

        return map.containsKey(curr);
    }

    public boolean implicitAnd(String word) {
        StringTokenizer st = new StringTokenizer(word);

        while (st.hasMoreTokens()) {
            String curr = st.nextToken();

            if (!map.containsKey(curr)) {
                return false;
            }
        }

        return true;
    }

    public void addSequence(String first, String second) {
        if (map.containsKey(first) && map.get(first) != null) {
            map.get(first).add(second);
        } else {
            Set<String> set = new HashSet<>();
            set.add(second);
            map.put(first, set);
        }
    }

    public void addText(String text) {
        StringTokenizer st = new StringTokenizer(text);

        String curr = st.nextToken();

        while (st.hasMoreTokens()) {
            String next = st.nextToken();
            addSequence(curr, next);
            curr = next;
        }

        map.put(curr, null);
    }
}
