import java.util.Scanner;
import java.util.Stack;

public class PalindromeChecker {

    // Method 1: Simple palindrome check
    public static boolean isSimplePalindrome(String input) {
        String reversed = new StringBuilder(input).reverse().toString();
        return input.equals(reversed);
    }

    // Method 2: Ignore case, spaces, special characters
    public static boolean isAdvancedPalindrome(String input) {
        // Remove non-alphanumeric characters and convert to lowercase
        String cleaned = input.replaceAll("[^a-zA-Z0-9]", "").toLowerCase();

        int left = 0;
        int right = cleaned.length() - 1;

        while (left < right) {
            if (cleaned.charAt(left) != cleaned.charAt(right)) {
                return false;
            }
            left++;
            right--;
        }

        return true;
    }

    // Method 3: Using Stack (Data Structure Concept)
    public static boolean isPalindromeUsingStack(String input) {
        String cleaned = input.replaceAll("[^a-zA-Z0-9]", "").toLowerCase();
        Stack<Character> stack = new Stack<>();

        for (char c : cleaned.toCharArray()) {
            stack.push(c);
        }

        for (char c : cleaned.toCharArray()) {
            if (c != stack.pop()) {
                return false;
            }
        }

        return true;
    }

    public static void main(String[] args) {

        Scanner scanner = new Scanner(System.in);

        System.out.println("===== PALINDROME CHECKER APP =====");
        System.out.print("Enter a string: ");
        String input = scanner.nextLine();

        System.out.println("\n--- Results ---");

        // Simple check
        if (isSimplePalindrome(input)) {
            System.out.println("Simple Check: It is a palindrome.");
        } else {
            System.out.println("Simple Check: Not a palindrome.");
        }

        // Advanced check
        if (isAdvancedPalindrome(input)) {
            System.out.println("Advanced Check: It is a palindrome.");
        } else {
            System.out.println("Advanced Check: Not a palindrome.");
        }

        // Stack check
        if (isPalindromeUsingStack(input)) {
            System.out.println("Stack Check: It is a palindrome.");
        } else {
            System.out.println("Stack Check: Not a palindrome.");
        }

        scanner.close();
    }
}