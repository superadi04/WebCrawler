package assignment;

import java.util.*;
import java.net.*;
import org.attoparser.simple.*;

/**
 * A markup handler which is called by the Attoparser markup parser as it parses the input;
 * responsible for building the actual web index.
 *
 * TODO: Implement this!
 */
public class CrawlingMarkupHandler extends AbstractSimpleMarkupHandler {
    private WebIndex index;
    private List<URL> urls;
    private long startTime;
    private long endTime;
    private Set<URL> visitedURLS;
    private Page currPage;
    private int wordCount;

    public CrawlingMarkupHandler() {
        urls = new ArrayList<>();
        index = new WebIndex();
        visitedURLS = new HashSet<>();
        wordCount = 0;
    }

    public void setCurrPage(URL currURL) {
        visitedURLS.add(currURL);
        this.currPage = new Page(currURL);
        index.addPage(currPage);
    }

    /**
    * This method returns the complete index that has been crawled thus far when called.
    */
    public WebIndex getIndex() {
        return this.index;
    }

    /**
    * This method returns any new URLs found to the Crawler; upon being called, the set of new URLs
    * should be cleared.
    */
    public List<URL> newURLs() {
        List<URL> urlsReturn = this.urls;
        this.urls = new ArrayList<>();
        return urlsReturn;
    }

    /**
    * These are some of the methods from AbstractSimpleMarkupHandler.
    * All of its method implementations are NoOps, so we've added some things
    * to do; please remove all the extra printing before you turn in your code.
    *
    * Note: each of these methods defines a line and col param, but you probably
    * don't need those values. You can look at the documentation for the
    * superclass to see all of the handler methods.
    */

    /**
    * Called when the parser first starts reading a document.
    * @param startTimeNanos  the current time (in nanoseconds) when parsing starts
    * @param line            the line of the document where parsing starts
    * @param col             the column of the document where parsing starts
    */
    public void handleDocumentStart(long startTimeNanos, int line, int col) {
        this.startTime = startTimeNanos;
    }

    /**
    * Called when the parser finishes reading a document.
    * @param endTimeNanos    the current time (in nanoseconds) when parsing ends
    * @param totalTimeNanos  the difference between current times at the start
    *                        and end of parsing
    * @param line            the line of the document where parsing ends
    * @param col             the column of the document where the parsing ends
    */
    public void handleDocumentEnd(long endTimeNanos, long totalTimeNanos, int line, int col) {
        this.endTime = endTimeNanos;
    }

    /**
    * Called at the start of any tag.
    * @param elementName the element name (such as "div")
    * @param attributes  the element attributes map, or null if it has no attributes
    * @param line        the line in the document where this element appears
    * @param col         the column in the document where this element appears
    */
    public void handleOpenElement(String elementName, Map<String, String> attributes, int line, int col) {
        // Check if there are elementNames that contain links
        if (elementName.equals("a") || elementName.equals("area") || elementName.equals("base") || elementName.equals("link")) {
            String link = attributes.get("href"); // Link tag

            // Check if URL ends in .html or .htm
            if (link != null && (link.endsWith(".html") || link.endsWith(".htm"))) {
                URL next;

                try {
                    next = new URL(currPage.getURL(), link);
                } catch (MalformedURLException e) { // If URL does not work
                    return;
                }

                // Check for duplicate URLs
                if (!visitedURLS.contains(next)) {
                    visitedURLS.add(next);
                    urls.add(next);
                }
            }
        }
    }

    /**
    * Called at the end of any tag.
    * @param elementName the element name (such as "div").
    * @param line        the line in the document where this element appears.
    * @param col         the column in the document where this element appears.
    */
    public void handleCloseElement(String elementName, int line, int col) {

    }

    /**
    * Called whenever characters are found inside a tag. Note that the parser is not
    * required to return all characters in the tag in a single chunk. Whitespace is
    * also returned as characters.
    * @param ch      buffer containing characters; do not modify this buffer
    * @param start   location of 1st character in ch
    * @param length  number of characters in ch
    */
    public void handleText(char[] ch, int start, int length, int line, int col) {
        StringBuilder currWord = new StringBuilder();

        for (int i = start; i < start + length; i++) {
            if (isAlphaNumeric(ch[i])) {
                currWord.append(ch[i]);
            } else if (currWord.length() > 0) {
                index.addWord(currPage, currWord.toString().toLowerCase(), wordCount);
                currWord = new StringBuilder();
                wordCount++;
            }
        }

        if (!currWord.isEmpty()) {
            index.addWord(currPage, currWord.toString().toLowerCase(), wordCount);
        }
    }

    // Helper method to check if a character is alphanumeric
    private boolean isAlphaNumeric(char c) {
        return (c >= '0' && c <= '9') || (c >= 'A' && c <= 'Z') || (c >= 'a' && c <= 'z');
    }
}
