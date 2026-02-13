import java.util.*;

class PageViewEvent {
    String url;
    String userId;
    String source;

    PageViewEvent(String url, String userId, String source) {
        this.url = url;
        this.userId = userId;
        this.source = source;
    }
}

public class RealTimeAnalytics {

    // pageUrl → visit count
    private HashMap<String, Integer> pageViews = new HashMap<>();

    // pageUrl → unique users
    private HashMap<String, HashSet<String>> uniqueVisitors = new HashMap<>();

    // source → visit count
    private HashMap<String, Integer> trafficSources = new HashMap<>();

    // Process incoming event (O(1))
    public synchronized void processEvent(PageViewEvent event) {

        // Count page views
        pageViews.put(event.url,
                pageViews.getOrDefault(event.url, 0) + 1);

        // Track unique visitors
        uniqueVisitors
                .computeIfAbsent(event.url, k -> new HashSet<>())
                .add(event.userId);

        // Count traffic source
        trafficSources.put(event.source,
                trafficSources.getOrDefault(event.source, 0) + 1);
    }

    // Get top 10 pages using heap
    private List<String> getTopPages() {

        PriorityQueue<String> pq = new PriorityQueue<>(
                (a, b) -> pageViews.get(a) - pageViews.get(b)
        );

        for (String page : pageViews.keySet()) {
            pq.offer(page);
            if (pq.size() > 10) {
                pq.poll();
            }
        }

        List<String> result = new ArrayList<>();
        while (!pq.isEmpty()) {
            result.add(pq.poll());
        }

        Collections.reverse(result);
        return result;
    }

    // Display dashboard
    public synchronized void getDashboard() {

        System.out.println("\n===== REAL-TIME DASHBOARD =====");

        System.out.println("\nTop Pages:");
        int rank = 1;
        for (String page : getTopPages()) {
            System.out.println(
                    rank++ + ". " + page +
                            " - " + pageViews.get(page) + " views (" +
                            uniqueVisitors.get(page).size() + " unique)"
            );
        }

        System.out.println("\nTraffic Sources:");
        int total = trafficSources.values().stream().mapToInt(i -> i).sum();

        for (Map.Entry<String, Integer> entry : trafficSources.entrySet()) {
            double percent = (entry.getValue() * 100.0) / total;
            System.out.printf("%s: %.1f%%\n", entry.getKey(), percent);
        }
    }

    // MAIN METHOD
    public static void main(String[] args) throws InterruptedException {

        RealTimeAnalytics analytics = new RealTimeAnalytics();

        // Simulate incoming events
        analytics.processEvent(new PageViewEvent(
                "/article/breaking-news", "user_123", "google"));
        analytics.processEvent(new PageViewEvent(
                "/article/breaking-news", "user_456", "facebook"));
        analytics.processEvent(new PageViewEvent(
                "/sports/championship", "user_789", "google"));
        analytics.processEvent(new PageViewEvent(
                "/article/breaking-news", "user_123", "direct"));
        analytics.processEvent(new PageViewEvent(
                "/sports/championship", "user_101", "google"));

        // Simulate dashboard refresh every 5 seconds
        Thread.sleep(5000);
        analytics.getDashboard();
    }
}
