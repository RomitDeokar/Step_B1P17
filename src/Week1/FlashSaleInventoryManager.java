package Week1;

import java.util.*;

class FlashSaleInventoryManager {

    // productId -> stock count
    private HashMap<String, Integer> stockMap = new HashMap<>();

    // productId -> waiting list (FIFO)
    private HashMap<String, LinkedHashMap<Integer, Boolean>> waitingList = new HashMap<>();

    // Add product
    public synchronized void addProduct(String productId, int stock) {
        stockMap.put(productId, stock);
        waitingList.put(productId, new LinkedHashMap<>());
    }

    // Instant stock check (O(1))
    public synchronized String checkStock(String productId) {
        if (!stockMap.containsKey(productId)) {
            return "Product not found";
        }
        return stockMap.get(productId) + " units available";
    }

    // Purchase item (thread-safe)
    public synchronized String purchaseItem(String productId, int userId) {

        if (!stockMap.containsKey(productId)) {
            return "Product not found";
        }

        int stock = stockMap.get(productId);

        // Stock available
        if (stock > 0) {
            stockMap.put(productId, stock - 1);
            return "Success, " + (stock - 1) + " units remaining";
        }

        // Stock exhausted → add to waiting list
        LinkedHashMap<Integer, Boolean> queue = waitingList.get(productId);
        queue.put(userId, true);

        return "Added to waiting list, position #" + queue.size();
    }

    // Driver code
    public static void main(String[] args) {

        FlashSaleInventoryManager manager = new FlashSaleInventoryManager();

        manager.addProduct("IPHONE15_256GB", 100);

        System.out.println(manager.checkStock("IPHONE15_256GB"));
        System.out.println(manager.purchaseItem("IPHONE15_256GB", 12345));
        System.out.println(manager.purchaseItem("IPHONE15_256GB", 67890));

        // Exhaust stock
        for (int i = 0; i < 98; i++) {
            manager.purchaseItem("IPHONE15_256GB", i);
        }

        System.out.println(manager.purchaseItem("IPHONE15_256GB", 99999));
    }
}
