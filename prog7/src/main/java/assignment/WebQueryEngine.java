package assignment;
import java.util.*;

/**
 * A query engine which holds an underlying web index and can answer textual queries with a
 * collection of relevant pages.
 *
 * TODO: Implement this!
 */

public class WebQueryEngine {
    /**
     * Returns a WebQueryEngine that uses the given Index to construct answers to queries.
     *
     * @param index The WebIndex this WebQueryEngine should use.
     * @return A WebQueryEngine ready to be queried.
     */

    private char[] currQuery;
    private int currQueryIndex;
    private Node root;
    private WebIndex index;

    private WebQueryEngine(WebIndex index) {
        this.index = index;
    }

    private WebQueryEngine() {

    }

    public static WebQueryEngine fromIndex(WebIndex index) {
        return new WebQueryEngine(index);
    }

    public static void main(String[] args) {
        WebQueryEngine q = new WebQueryEngine();
        q.query("((wealth & (!fame | prop)) | \"happiness and love\")");
        System.out.println("hi");
    }

    /**
     * Returns a Collection of URLs (as Strings) of web pages satisfying the query expression.
     *
     * @param query A query expression.
     * @return A collection of web pages satisfying the query.
     */
    public Collection<Page> query(String query) {
        try {
            currQuery = query.toCharArray();
            this.root = parseQuery();
        } catch (Exception e) {
            System.err.println("Invalid query format.");
        }

        // TODO: Implement this!
        return new HashSet<>();
    }

    private Set<Page> processQuery(Node n, HashSet<Page> ans) {
        if (n.isLeafNode()) {
            return searchWord(n.data);
        }

        if (n.data.equals("&")) {
            return intersection(processQuery(n.left, ans), processQuery(n.right, ans));
        } else if (n.data.equals("|")) {
            return union(processQuery(n.left, ans), processQuery(n.right, ans));
        }

        return null;
    }

    private Set<Page> searchWord(String word) {
        Set<Page> pages = new HashSet<>();
        for (Page p : index.getPages()) {
            if ((word.charAt(0) == '!' && !p.containsWord(word)) || (word.charAt(0) != '!' && p.containsWord(word))) {
                pages.add(p);
            }
        }
        return pages;
    }

    private Set<Page> union(Set<Page> a, Set<Page> b) {
        Set<Page> ans = new HashSet<>();
        a.addAll(ans);
        b.addAll(ans);
        return ans;
    }

    private Set<Page> intersection(Set<Page> a, Set<Page> b) {
        Set<Page> ans = new HashSet<>();
        for (Page p : a) {
            if (b.contains(p)) {
                ans.add(p);
            }
        }
        return ans;
    }

    private String getToken() {
        if (currQueryIndex >= currQuery.length) {
            return null;
        }

        char c = currQuery[currQueryIndex];

        if (checkToken(c)) {
            char token = currQuery[currQueryIndex++];

            while (currQueryIndex < currQuery.length && currQuery[currQueryIndex] == ' ') {
                currQueryIndex++;
            }

            return Character.toString(token);
        }

        StringBuilder word = new StringBuilder();

        while (!checkToken(c)) {
            word.append(c);
            c = currQuery[++currQueryIndex];
        }

        return word.toString().trim();
    }

    public boolean checkToken(char c) {
        return c == '&' || c == '|' || c == '(' || c == ')';
    }

    private Node parseQuery() throws Exception {
        String t = getToken();

        if (t.equals("(")) {
            Node left = parseQuery();
            String operator = getToken();
            Node right = parseQuery();
            getToken(); // Right parenthesis
            return new Node(operator, left, right);
        } else if (t.length() >= 1) {
            return new Node(t);
        } else {
            throw new Exception();
        }
    }

    private class Node {
        private Node left;
        private Node right;
        private String data;

        private Node(String data, Node left, Node right) {
            this.data = data;
            this.left = left;
            this.right = right;
        }

        private Node(String data) {
            this.data = data;
        }

        private boolean isLeafNode () {
            return this.left == null && this.right == null;
        }
    }
}
