package assignment;
import java.net.URL;
import java.util.HashMap;

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

    private HashMap<String, String> map;

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

    public boolean containsWord(String s) {
        return map.containsKey(s);
    }

    @Override
    public boolean equals(Object obj) {
        Page p = (Page) obj;
        return this.url.equals(p.url);
    }
}
