import java.util.*;


public class DNSCache {

    private final int MAX_CACHE_SIZE = 5;

    // LRU Cache (accessOrder = true)
    private LinkedHashMap<String, DNSEntry> cache;

    private int hits = 0;
    private int misses = 0;

    public DNSCache() {

        cache = new LinkedHashMap<>(16, 0.75f, true) {
            protected boolean removeEldestEntry(Map.Entry<String, DNSEntry> eldest) {
                return size() > MAX_CACHE_SIZE;
            }
        };

        startCleanupThread();
    }

    // Resolve domain
    public synchronized String resolve(String domain) {

        long startTime = System.nanoTime();

        if (cache.containsKey(domain)) {
            DNSEntry entry = cache.get(domain);

            if (!entry.isExpired()) {
                hits++;
                return "Cache HIT → " + entry.ipAddress +
                        " (retrieved in " + elapsed(startTime) + " ms)";
            } else {
                cache.remove(domain);
            }
        }

        // Cache miss
        misses++;
        String ip = queryUpstreamDNS(domain);

        // TTL set to 5 seconds for testing
        cache.put(domain, new DNSEntry(domain, ip, 5));

        return "Cache MISS → " + ip + " (TTL: 5s)";
    }

    // Simulated upstream DNS lookup
    private String queryUpstreamDNS(String domain) {
        return "172.217.14." + new Random().nextInt(255);
    }

    // Cache statistics
    public synchronized String getCacheStats() {
        int total = hits + misses;
        double hitRate = (total == 0) ? 0 : ((double) hits / total) * 100;
        return "Hit Rate: " + String.format("%.2f", hitRate) + "%";
    }

    // Background thread to remove expired entries
    private void startCleanupThread() {

        Thread cleaner = new Thread(() -> {
            while (true) {
                try {
                    Thread.sleep(2000);
                    synchronized (this) {
                        cache.entrySet().removeIf(entry -> entry.getValue().isExpired());
                    }
                } catch (InterruptedException ignored) {
                }
            }
        });

        cleaner.setDaemon(true); // VERY IMPORTANT
        cleaner.start();
    }

    private double elapsed(long start) {
        return (System.nanoTime() - start) / 1_000_000.0;
    }

    // MAIN METHOD
    public static void main(String[] args) throws InterruptedException {

        DNSCache dns = new DNSCache();

        System.out.println(dns.resolve("google.com")); // MISS
        System.out.println(dns.resolve("google.com")); // HIT

        System.out.println(dns.getCacheStats());

        // Wait for TTL to expire
        Thread.sleep(6000);

        System.out.println(dns.resolve("google.com")); // Should MISS again
        System.out.println(dns.getCacheStats());
    }
}
