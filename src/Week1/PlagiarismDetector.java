import java.util.*;

class PlagiarismDetector {

    private static final int N = 5; // 5-grams

    // n-gram → set of document IDs
    private HashMap<String, Set<String>> ngramIndex = new HashMap<>();

    // Add document to index
    public void addDocument(String docId, String content) {

        List<String> ngrams = generateNGrams(content);

        for (String gram : ngrams) {
            ngramIndex
                    .computeIfAbsent(gram, k -> new HashSet<>())
                    .add(docId);
        }

        System.out.println("Indexed " + ngrams.size() + " n-grams for " + docId);
    }

    // Analyze document for plagiarism
    public void analyzeDocument(String docId, String content) {

        List<String> ngrams = generateNGrams(content);
        HashMap<String, Integer> matchCount = new HashMap<>();

        for (String gram : ngrams) {
            if (ngramIndex.containsKey(gram)) {
                for (String existingDoc : ngramIndex.get(gram)) {
                    matchCount.put(
                            existingDoc,
                            matchCount.getOrDefault(existingDoc, 0) + 1
                    );
                }
            }
        }

        System.out.println("Extracted " + ngrams.size() + " n-grams");

        for (Map.Entry<String, Integer> entry : matchCount.entrySet()) {

            String comparedDoc = entry.getKey();
            int matches = entry.getValue();

            double similarity = (matches * 100.0) / ngrams.size();

            System.out.printf(
                    "Found %d matching n-grams with \"%s\" → Similarity: %.1f%% %s\n",
                    matches,
                    comparedDoc,
                    similarity,
                    similarity >= 60 ? "(PLAGIARISM DETECTED)" :
                            similarity >= 15 ? "(Suspicious)" : ""
            );
        }
    }

    // Generate n-grams using sliding window
    private List<String> generateNGrams(String text) {

        String[] words = text.toLowerCase().split("\\s+");
        List<String> ngrams = new ArrayList<>();

        for (int i = 0; i <= words.length - N; i++) {
            StringBuilder gram = new StringBuilder();
            for (int j = 0; j < N; j++) {
                gram.append(words[i + j]).append(" ");
            }
            ngrams.add(gram.toString().trim());
        }

        return ngrams;
    }

    // Driver code
    public static void main(String[] args) {

        PlagiarismDetector detector = new PlagiarismDetector();

        String essay1 =
                "data structures and algorithms are very important for computer science students";

        String essay2 =
                "data structures and algorithms are very important for software engineering students";

        String essay3 =
                "machine learning and artificial intelligence are modern computer science topics";

        detector.addDocument("essay_089.txt", essay1);
        detector.addDocument("essay_092.txt", essay2);

        System.out.println("\nAnalyzing new document:\n");

        detector.analyzeDocument("essay_123.txt", essay2);
    }
}
