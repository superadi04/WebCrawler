package assignment;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

public class Tests {

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
    }
}
