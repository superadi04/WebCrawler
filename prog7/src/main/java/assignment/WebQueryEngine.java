package assignment;
import java.awt.print.Pageable;
import java.net.MalformedURLException;
import java.util.*;

/**
 * A query engine which holds an underlying web index and can answer textual queries with a
 * collection of relevant pages.
 *
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

    public static WebQueryEngine fromIndex(WebIndex index) {
        return new WebQueryEngine(index);
    }

    /*
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
            return parseTree(root);
        } catch (Exception e) {
            System.err.println("Invalid query format.");
        }

        return null;

    }

    private Set<Page> parseTree(Node curr) throws MalformedURLException {
        if (curr.isLeafNode()) {
            return index.getPagesForQuery(curr.data, curr.isPhrase);
        } else {
            if (curr.data.get(0).charAt(0) == '&') {
                return intersection(parseTree(curr.left), parseTree(curr.right));
            } else {
                return union(parseTree(curr.left), parseTree(curr.right));
            }
        }
    }

    private Set<Page> intersection(Set<Page> a, Set<Page> b) {
        Set<Page> intersectionSet = new HashSet<>();

        for (Page p : a) {
            if (b.contains(p)) {
                intersectionSet.add(p);
            }
        }

        return intersectionSet;
    }

    private Set<Page> union(Set<Page> a, Set<Page> b) {
        Set<Page> unionSet = new HashSet<>();
        unionSet.addAll(a);
        unionSet.addAll(b);
        return unionSet;
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

        while (!checkToken(c) && currQueryIndex < currQuery.length) {
            word.append(c);
            c = currQuery[Math.min(currQuery.length - 1, ++currQueryIndex)];
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
        private List<String> data;
        private boolean isPhrase;

        private Node(String operator, Node left, Node right) {
            this.left = left;
            this.right = right;
            data = new ArrayList<>();
            data.add(operator);
        }

        private Node(String operator) {
            data = new ArrayList<>();

            if (operator.charAt(0) == '"' && operator.charAt(operator.length() - 1) == '"') {
                isPhrase = true;
                operator = operator.substring(1, operator.length() - 1);
            }

            StringTokenizer st = new StringTokenizer(operator);

            while (st.hasMoreTokens()) {
                data.add(st.nextToken().toLowerCase());
            }
        }

        private boolean isLeafNode() {
            return this.left == null && this.right == null;
        }
    }
}
