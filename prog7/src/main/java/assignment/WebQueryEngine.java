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

    private char[] currQuery; // Char array of current query
    private int currQueryIndex; // Current index in current query array
    private WebIndex index;
    private String lastToken; // Previous token for implicit AND edge case

    // Private constructor for internal usage
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
            this.currQuery = query.toCharArray();
            currQueryIndex = 0;
            lastToken = "#"; // Set as arbitrary non-alphanumeric value
            return performQuery();
        } catch (Exception e) {
            System.err.println("Invalid query format.");
        }

        // If there is exception, return nothing
        return new HashSet<>();
    }

    private Set<Page> performQuery() {
        Stack<String> postfix = convertToPostfix();

        // Edge cases if the query is empty or there is only 1 word in the query
        if (postfix.isEmpty()) {
            return new HashSet<>();
        } else if (postfix.size() == 1) {
            return index.getPagesForQuery(postfix.pop());
        }

        // Auxiliary stack to add sets of pages for each tokens
        Stack<Set<Page>> temp = new Stack<>();

        while (!postfix.isEmpty()) {
            if (postfix.peek().charAt(0) == '&' || postfix.peek().charAt(0) == '|') {
                Set<Page> ans;
                char c = postfix.pop().charAt(0);

                // Do operation on top 2 elements on auxiliary stack
                if (c == '&') {
                    ans = intersection(temp.pop(), temp.pop());
                } else {
                    ans = union(temp.pop(), temp.pop());
                }

                // Add back the result to auxiliary stack
                temp.push(ans);
            } else {
                // Add all pages for that given subquery to auxiliary
                temp.push(index.getPagesForQuery(postfix.pop().toLowerCase()));
            }
        }

        return temp.pop();
    }

    // Shunting-Yard Algorithm
    private Stack<String> convertToPostfix() {
        String s = getToken();
        Stack<Character> tokens = new Stack<>();
        Stack<String> ans = new Stack<>();

        while (s != null) {
            if (stringCharCheck(s, '&') || stringCharCheck(s, '|') || stringCharCheck(s, '(')) {
                // Implicit AND edge case
                if (stringCharCheck(s, '(') && isAlphaNumeric(lastToken.charAt(lastToken.length() - 1))) {
                    tokens.push('&');
                }
                tokens.push(s.charAt(0)); // If we reach an operator/left parens, push to tokens stack
            } else if (stringCharCheck(s, ')')) {
                while (!tokens.isEmpty() && tokens.peek() != '(') { // If we reach right parens, pop from stack until we reach left parens
                    ans.push(Character.toString(tokens.pop()));
                }
                // Pop last left parens
                if (!tokens.isEmpty()) {
                    tokens.pop();
                }
            } else {
                // Implicit AND edge case
                if (stringCharCheck(lastToken, ')') || isAlphaNumeric(lastToken.charAt(lastToken.length() - 1))) {
                    tokens.push('&');
                }
                // Important for insensitive case
                ans.push(s.toLowerCase());
            }
            lastToken = s;
            s = getToken(); // Get next token
        }

        while (!tokens.isEmpty()) {
            ans.push(Character.toString(tokens.pop()));
        }

        return reverseStack(ans);
    }

    // Helper method to check if char is alphanumeric
    private boolean isAlphaNumeric(char c) {
        return (c >= 'a' && c <= 'z') || (c >= 'A' && c <= 'Z') || (c >= '0' && c <= '9');
    }

    // Helper method used to reverse a stack
    private Stack<String> reverseStack(Stack<String> input) {
        Stack<String> newStack = new Stack<>();

        while (!input.isEmpty()) {
            newStack.push(input.pop());
        }

        return newStack;
    }

    // Helper method to check for tokens
    private boolean stringCharCheck(String s, char c) {
        return s.length() == 1 && s.charAt(0) == c;
    }

    // Helper method used to get next token in the query
    private String getToken() {
        // Remove extraneous whitespace
        while (currQueryIndex < currQuery.length && currQuery[currQueryIndex] == ' ') {
            currQueryIndex++;
        }

        // Check if current index is less than length of query
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
            // Used for phrase queries: Get the entire phrase query
            while (currQueryIndex < currQuery.length && currQuery[currQueryIndex] != '"') {
                word.append(currQuery[currQueryIndex++]);
            }
            if (currQueryIndex < currQuery.length) {
                word.append(currQuery[currQueryIndex++]);
            }
        } else {
            // Add entire word to token
            while (currQueryIndex < currQuery.length && !checkToken(currQuery[currQueryIndex]) && currQuery[currQueryIndex] != ' ') {
                word.append(currQuery[currQueryIndex++]);
            }
        }

        // Trim extra whitespace
        return word.toString().trim();
    }

    // Helper method used to check if a token is an operator or paren
    private boolean checkToken(char c) {
        return c == '&' || c == '|' || c == '(' || c == ')';
    }

    // Used for AND operations on two sets of pages
    private Set<Page> intersection(Set<Page> a, Set<Page> b) {
        HashSet<Page> intersectionSet = new HashSet<>();

        for (Page p : a) {
            if (b.contains(p)) {
                intersectionSet.add(p);
            }
        }

        return intersectionSet;
    }

    // Used for OR operations on two sets of pages
    private Set<Page> union(Set<Page> a, Set<Page> b) {
        HashSet<Page> unionSet = new HashSet<>();
        unionSet.addAll(a);
        unionSet.addAll(b);
        return unionSet;
    }
}
