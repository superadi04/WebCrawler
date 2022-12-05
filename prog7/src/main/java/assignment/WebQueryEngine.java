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
    private WebIndex index;

    private WebQueryEngine(WebIndex index) {
        this.index = index;
    }

    public WebQueryEngine() {
        // FOR TESTING
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
            this.currQuery = query.toCharArray();
            Stack<String> ans = convertToPostfix(query);

            while (!ans.isEmpty()) {
                System.out.println(ans.pop());
            }
            //test(query);

            //this.root = parseQuery();
            //return parseTree(root);
        } catch (Exception e) {
            System.err.println("Invalid query format.");
        }

        return null;

    }

    private Set<Page> performQuery(String query) {
        Stack<String> postfix = convertToPostfix(query);
        Set<Page> ans = new HashSet<>();

        if (postfix.isEmpty()) {
            return ans;
        }

        while (!postfix.isEmpty()) {
            List<String> words = new ArrayList<>();
            while (!postfix.isEmpty() && checkToken(postfix.peek().charAt(0)) {
                postfix.pop();
            }
        }
    }

    // Shunting-Yard Algorithm
    private Stack<String> convertToPostfix(String query) {
        String s = getToken();
        Stack<Character> tokens = new Stack<>();
        Stack<String> ans = new Stack<>();

        while (s != null) {
            if (stringCharCheck(s, '&') || stringCharCheck(s, '|') || stringCharCheck(s, '(')) {
                tokens.push(s.charAt(0));
            } else if (stringCharCheck(s, ')')) {
                while (!tokens.isEmpty() && tokens.peek() != '(') {
                    ans.push(Character.toString(tokens.pop()));
                }
                if (!tokens.isEmpty()) {
                    tokens.pop();
                }
            } else {
                ans.push(s);
            }
            s = getToken();
        }

        return ans;
    }

    private boolean stringCharCheck(String s, char c) {
        return s.length() == 1 && s.charAt(0) == c;
    }

    private String getToken() {
        while (currQueryIndex < currQuery.length && currQuery[currQueryIndex] == ' ') {
            currQueryIndex++;
        }

        if (currQueryIndex >= currQuery.length) {
            return null;
        }

        char c = currQuery[currQueryIndex++];

        if (checkToken(c)) {
            return Character.toString(c);
        }

        StringBuilder word = new StringBuilder();
        word.append(c);

        if (c == '"') {
            while (currQueryIndex < currQuery.length && currQuery[currQueryIndex] != '"') {
                word.append(currQuery[currQueryIndex++]);
            }
            if (currQueryIndex < currQuery.length) {
                word.append(currQuery[currQueryIndex++]);
            }
        } else {
            while (currQueryIndex < currQuery.length && !checkToken(currQuery[currQueryIndex]) && currQuery[currQueryIndex] != ' ') {
                word.append(currQuery[currQueryIndex++]);
            }
        }

        return word.toString().trim();
    }


    /*private Stack<String> getPostfix(char[] currQuery) {
        Stack<Character> tokens = new Stack<>();
        Stack<String> ans = new Stack<>();

        String currToken = getToken();

        while (currToken != null) {

        }
    }*/

    public boolean checkToken(char c) {
        return c == '&' || c == '|' || c == '(' || c == ')';
    }

    private Set<Page> intersection(Set<Page> a, Set<Page> b) {
        HashSet<Page> intersectionSet = new HashSet<>();

        for (Page p : a) {
            if (b.contains(p)) {
                intersectionSet.add(p);
            }
        }

        return intersectionSet;
    }

    private Set<Page> union(Set<Page> a, Set<Page> b) {
        HashSet<Page> unionSet = new HashSet<>();
        unionSet.addAll(a);
        unionSet.addAll(b);
        return unionSet;
    }

    /*
    private Set<Page> parseTree(Node curr) {
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
        private ArrayList<String> data;
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
    }*/
}
