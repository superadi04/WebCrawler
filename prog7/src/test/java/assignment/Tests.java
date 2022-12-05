package assignment;

import org.attoparser.config.ParseConfiguration;
import org.attoparser.simple.ISimpleMarkupParser;
import org.attoparser.simple.SimpleMarkupParser;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.*;

public class Tests {

    @Test
    public void shouldAndQuiet() throws MalformedURLException {
        Queue<URL> remaining = new LinkedList<>();
        remaining.add(new URL("file:///Users/adityachebrolu/Documents/GitHub/WebCrawler/prog7/president96/www.dolekemp96.org/interactive/computer/leader.html"));

        ISimpleMarkupParser parser = new SimpleMarkupParser(ParseConfiguration.htmlConfiguration());
        CrawlingMarkupHandler handler = new CrawlingMarkupHandler();

        int count = 0;

        // Try to start crawling, adding new URLS as we see them.
        try {
            while (!remaining.isEmpty()) {
                URL currURL = remaining.poll();
                handler.setCurrPage(currURL);
                count++;

                // Parse the next URL's page
                parser.parse(new InputStreamReader(currURL.openStream()), handler);
                // Add any new URLs
                remaining.addAll(handler.newURLs());
            }
            System.out.println(count);
        } catch (Exception e) {
            // Bad exception handling :(
            System.err.println("Error: Index generation failed!");
            e.printStackTrace();
            System.exit(1);
        }

        WebQueryEngine engine = WebQueryEngine.fromIndex(handler.getIndex());
        Collection<Page> ans = engine.query("\"Ensure that this MIME mapping is needed for your Web server before adding it to the list\"");

        System.out.println(ans.size());
    }

    /*
    @Test
    public void checkAndQueryOne() throws MalformedURLException {
        Page p1 = new Page(new URL("http://www.google.com"));
        Page p2 = new Page(new URL("http://www.facebook.com"));

        p1.addText("unicorn sheet clear color");

        p2.addText("unicorn horse color");

        WebIndex index = new WebIndex();
        index.addPage(p1);
        index.addPage(p2);

        WebQueryEngine engine = WebQueryEngine.fromIndex(index);
        Collection<Page> answer = engine.query("(sheet & clear)");
        Assertions.assertTrue(answer.contains(p1) && answer.size() == 1);
    }

    @Test
    public void checkAndQueryNone() throws MalformedURLException {
        Page p1 = new Page(new URL("http://www.google.com"));
        Page p2 = new Page(new URL("http://www.facebook.com"));

        p1.addText("unicorn sheet clear color");

        p2.addText("unicorn horse color");

        WebIndex index = new WebIndex();
        index.addPage(p1);
        index.addPage(p2);

        WebQueryEngine engine = WebQueryEngine.fromIndex(index);
        Collection<Page> answer = engine.query("(sheet & horse)");
        Assertions.assertTrue(answer.size() == 0);
    }

    @Test
    public void checkAndQueryBoth() throws MalformedURLException {
        Page p1 = new Page(new URL("http://www.google.com"));
        Page p2 = new Page(new URL("http://www.facebook.com"));

        p1.addText("unicorn sheet clear color");

        p2.addText("unicorn horse color");

        WebIndex index = new WebIndex();
        index.addPage(p1);
        index.addPage(p2);

        WebQueryEngine engine = WebQueryEngine.fromIndex(index);
        Collection<Page> answer = engine.query("(unicorn & color)");
        Assertions.assertTrue(answer.contains(p1) && answer.contains(p2) && answer.size() == 2);
    }

    @Test
    public void checkOrQueryBoth() throws MalformedURLException {
        Page p1 = new Page(new URL("http://www.google.com"));
        Page p2 = new Page(new URL("http://www.facebook.com"));

        p1.addText("unicorn sheet clear color");

        p2.addText("unicorn horse color");

        WebIndex index = new WebIndex();
        index.addPage(p1);
        index.addPage(p2);

        WebQueryEngine engine = WebQueryEngine.fromIndex(index);
        Collection<Page> answer = engine.query("(sheet | horse)");
        Assertions.assertTrue(answer.contains(p1) && answer.contains(p2) && answer.size() == 2);
    }

    @Test
    public void checkNegationBoth() throws MalformedURLException {
        Page p1 = new Page(new URL("http://www.google.com"));
        Page p2 = new Page(new URL("http://www.facebook.com"));

        p1.addText("unicorn sheet clear color");

        p2.addText("unicorn horse color");

        WebIndex index = new WebIndex();
        index.addPage(p1);
        index.addPage(p2);

        WebQueryEngine engine = WebQueryEngine.fromIndex(index);
        Collection<Page> answer = engine.query("!people");
        Assertions.assertTrue(answer.contains(p1) && answer.contains(p2) && answer.size() == 2);
    }

    @Test
    public void checkPhrase() throws MalformedURLException {
        Page p1 = new Page(new URL("http://www.google.com"));

        p1.addText("unicorn sheet clear color");

        WebIndex index = new WebIndex();
        index.addPage(p1);

        WebQueryEngine engine = WebQueryEngine.fromIndex(index);
        Collection<Page> answer = engine.query("\"unicorn sheet clear\"");
        Assertions.assertTrue(answer.contains(p1) && answer.size() == 1);
    }

    @Test
    public void implicitAndQuery() throws MalformedURLException {
        Page p1 = new Page(new URL("http://www.google.com"));

        p1.addText("unicorn sheet clear color");

        WebIndex index = new WebIndex();
        index.addPage(p1);

        WebQueryEngine engine = WebQueryEngine.fromIndex(index);
        Collection<Page> answer = engine.query("unicorn color");
        Assertions.assertTrue(answer.contains(p1) && answer.size() == 1);
    }*/
}
