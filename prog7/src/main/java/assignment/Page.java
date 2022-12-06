package assignment;
import java.io.BufferedReader;
import java.io.Serializable;
import java.net.URL;
import java.util.*;

/**
 * The Page class holds anything that the QueryEngine returns to the server.  The field and method
 * we provided here is the bare minimum requirement to be a Page - feel free to add anything you
 * want as long as you don't break the getURL method.
 *
 * TODO: Implement this!
 */
public class Page implements Serializable {
    // The URL the page was located at.
    private URL url;

    private static final long serialVersionUID = 1L;

    /**
     * Creates a Page with a given URL.
     * @param url The url of the page.
     */
    public Page(URL url) {
        this.url = url;
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
}
