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
            Set<Page> ans = performQuery();
            return ans;
        } catch (Exception e) {
            System.err.println("Invalid query format.");
        }

        return null;

    }

    private Set<Page> performQuery() {
        Stack<String> postfix = convertToPostfix();

        if (postfix.isEmpty()) {
            return new HashSet<>();
        } else if (postfix.size() == 1) {
            return index.getPagesForQuery(postfix.pop());
        }

        Set<Page> ans = new HashSet<>();
        Stack<String> temp = new Stack<>();

        while (!postfix.isEmpty()) {
            temp.push(postfix.pop());
            if (temp.peek().charAt(0) == '&' || temp.peek().charAt(0) == '|') {
                char c = temp.pop().charAt(0);
                if (c == '&') {
                    ans = intersection(index.getPagesForQuery(temp.pop()), index.getPagesForQuery(temp.pop()));
                } else {
                    ans = union(index.getPagesForQuery(temp.pop()), index.getPagesForQuery(temp.pop()));
                }
                break;
            }
        }

        while (!postfix.isEmpty()) {
            temp.push(postfix.pop());
            if (temp.peek().charAt(0) == '&' || temp.peek().charAt(0) == '|') {
                char c = temp.pop().charAt(0);
                if (c == '&') {
                    ans = intersection(ans, index.getPagesForQuery(temp.pop()));
                } else {
                    ans = union(ans, index.getPagesForQuery(temp.pop()));
                }
            }
        }

        while (!temp.isEmpty()) {
            ans = intersection(ans, index.getPagesForQuery(temp.pop()));
        }

        return ans;
        /*


        String part1 = postfix.pop();
        String part2 = postfix.pop();

        Set<Page> ans;

        if (postfix.isEmpty() || postfix.peek().equals("&") || !postfix.peek().equals("|")) {
            if (postfix.peek().equals("&")) {
                postfix.pop();
            }
            ans = intersection(index.getPagesForQuery(part1), index.getPagesForQuery(part2));
        } else {
            ans = union(index.getPagesForQuery(part1), index.getPagesForQuery(part2));
        }

        while (!postfix.isEmpty()) {
            String part3 = postfix.pop();

            if (postfix.isEmpty() || postfix.peek().equals("&") || !postfix.peek().equals("|")) {
                if (postfix.peek().equals("&")) {
                    postfix.pop();
                }
                ans = intersection(ans, index.getPagesForQuery(part3));
            } else {
                ans = union(ans, index.getPagesForQuery(part3));
            }
        }

        return ans;
        */
    }

    // Shunting-Yard Algorithm
    private Stack<String> convertToPostfix() {
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

        while (!tokens.isEmpty()) {
            ans.push(Character.toString(tokens.pop()));
        }

        return reverseStack(ans);
    }

    private Stack<String> reverseStack(Stack<String> input) {
        Stack<String> newStack = new Stack<>();

        while (!input.isEmpty()) {
            newStack.push(input.pop());
        }

        return newStack;
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
}
