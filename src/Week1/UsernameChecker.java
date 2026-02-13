package Week1;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class UsernameChecker {

    // username -> userId mapping
    private ConcurrentHashMap<String, Integer> usernameToUserId;

    // username -> attempt frequency
    private ConcurrentHashMap<String, Integer> attemptFrequency;

    public UsernameChecker() {
        usernameToUserId = new ConcurrentHashMap<>();
        attemptFrequency = new ConcurrentHashMap<>();
    }

    // Register existing user (for simulation)
    public void addUser(String username, int userId) {
        usernameToUserId.put(username, userId);
    }

    // Check availability in O(1)
    public boolean checkAvailability(String username) {

        // Increase attempt count
        attemptFrequency.merge(username, 1, Integer::sum);

        return !usernameToUserId.containsKey(username);
    }

    // Suggest alternatives
    public List<String> suggestAlternatives(String username) {

        List<String> suggestions = new ArrayList<>();

        if (!usernameToUserId.containsKey(username)) {
            suggestions.add(username);
            return suggestions;
        }

        // Append numbers
        for (int i = 1; i <= 5; i++) {
            String newUsername = username + i;
            if (!usernameToUserId.containsKey(newUsername)) {
                suggestions.add(newUsername);
            }
        }

        // Replace underscore with dot
        if (username.contains("_")) {
            String modified = username.replace("_", ".");
            if (!usernameToUserId.containsKey(modified)) {
                suggestions.add(modified);
            }
        }

        return suggestions;
    }

    // Get most attempted username
    public String getMostAttempted() {

        String mostAttempted = null;
        int maxAttempts = 0;

        for (Map.Entry<String, Integer> entry : attemptFrequency.entrySet()) {
            if (entry.getValue() > maxAttempts) {
                maxAttempts = entry.getValue();
                mostAttempted = entry.getKey();
            }
        }

        return mostAttempted + " (" + maxAttempts + " attempts)";
    }

    // Main method for testing
    public static void main(String[] args) {

        UsernameChecker checker = new UsernameChecker();

        checker.addUser("john_doe", 1);
        checker.addUser("admin", 2);

        System.out.println(checker.checkAvailability("john_doe")); // false
        System.out.println(checker.checkAvailability("jane_smith")); // true

        System.out.println(checker.suggestAlternatives("john_doe"));
        System.out.println(checker.getMostAttempted());
    }
}
