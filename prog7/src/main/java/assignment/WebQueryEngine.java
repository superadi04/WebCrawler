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
    private String lastToken;

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
            currQueryIndex = 0;
            return performQuery();
        } catch (Exception e) {
            System.err.println("Invalid query format.");
        }

        return new HashSet<>();
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

        boolean edgeCase = true;

        while (!postfix.isEmpty()) {
            temp.push(postfix.pop().toLowerCase());
            if (temp.peek().charAt(0) == '&' || temp.peek().charAt(0) == '|') {
                edgeCase = false;
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

        if (edgeCase && !temp.isEmpty()) {
            ans = index.getPagesForQuery(temp.pop());
        }

        while (!temp.isEmpty()) {
            ans = intersection(ans, index.getPagesForQuery(temp.pop()));
        }

        return ans;
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
                if (stringCharCheck(lastToken, ')') || isAlphaNumeric(lastToken.charAt(lastToken.length() - 1))) {
                    tokens.push('&');
                }
                ans.push(s.toLowerCase());
            }
            s = getToken();
        }

        while (!tokens.isEmpty()) {
            ans.push(Character.toString(tokens.pop()));
        }

        return reverseStack(ans);
    }

    private boolean isAlphaNumeric(char c) {
        return (c >= 'a' && c <= 'z') || (c >= 'A' && c <= 'Z') || (c >= '0' && c <= '9');
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
