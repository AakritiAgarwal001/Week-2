import java.util.*;

public class Questions {
    // L1 Cache: In-memory, size-limited, LRU eviction
    private final int L1_CAPACITY = 10000;
    private LinkedHashMap<String, String> l1Cache = new LinkedHashMap<>(L1_CAPACITY, 0.75f, true) {
        protected boolean removeEldestEntry(Map.Entry<String, String> eldest) {
            return size() > L1_CAPACITY;
        }
    };

    // L2 Cache: SSD-backed (simulated with a HashMap of file paths)
    private Map<String, String> l2Cache = new HashMap<>();

    // Metrics and Promotion Logic
    private Map<String, Integer> accessCounts = new HashMap<>();
    private final int PROMOTION_THRESHOLD = 3;

    /**
     * Retrieves video data using the multi-level strategy.
     */
    public String getVideo(String videoId) {
        long startTime = System.currentTimeMillis();

        // 1. Check L1 Cache (Memory)
        if (l1Cache.containsKey(videoId)) {
            printResult(videoId, "L1 HIT", System.currentTimeMillis() - startTime);
            return l1Cache.get(videoId);
        }

        // 2. Check L2 Cache (SSD)
        if (l2Cache.containsKey(videoId)) {
            String data = "Data_from_SSD_for_" + videoId;
            updateAccessAndPromote(videoId, data);
            printResult(videoId, "L2 HIT (Promoting...)", System.currentTimeMillis() - startTime + 5);
            return data;
        }

        // 3. L3 Check (Database - Simulated)
        String dbData = "Data_from_DB_for_" + videoId;
        l2Cache.put(videoId, "/ssd/path/" + videoId);
        accessCounts.put(videoId, 1);

        printResult(videoId, "L3 HIT (Added to L2)", System.currentTimeMillis() - startTime + 150);
        return dbData;
    }

    private void updateAccessAndPromote(String videoId, String data) {
        int count = accessCounts.getOrDefault(videoId, 0) + 1;
        accessCounts.put(videoId, count);

        // If video becomes "popular," move it to L1
        if (count >= PROMOTION_THRESHOLD) {
            l1Cache.put(videoId, data);
        }
    }

    private void printResult(String id, String status, long time) {
        System.out.printf("[Video: %s] %s | Time: %dms\n", id, status, time);
    }

    public static void main(String[] args) {
        Questions streamingService = new Questions();

        // Simulate repeated access pattern
        streamingService.getVideo("Inception_2010"); // L3 Hit
        streamingService.getVideo("Inception_2010"); // L2 Hit
        streamingService.getVideo("Inception_2010"); // L2 Hit -> Promoted
        streamingService.getVideo("Inception_2010"); // L1 Hit!
    }
}