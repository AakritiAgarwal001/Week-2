import java.util.*;

public class Questions {
    // Trie Node representing each character in a search query
    static class TrieNode {
        Map<Character, TrieNode> children = new HashMap<>();
        // Stores queries passing through this node and their frequencies
        // In a production system, we'd store only the Top 10 here to meet the <50ms requirement
        Map<String, Integer> counts = new HashMap<>();
    }

    private final TrieNode root = new TrieNode();

    /**
     * Updates the frequency of a search query.
     * If the query is new, it's added to the Trie.
     */
    public void updateFrequency(String query) {
        TrieNode curr = root;
        for (char c : query.toLowerCase().toCharArray()) {
            curr = curr.children.computeIfAbsent(c, k -> new TrieNode());
            // Record this query at every prefix level for $O(L)$ retrieval
            curr.counts.put(query, curr.counts.getOrDefault(query, 0) + 1);
        }
    }

    /**
     * Returns top 10 suggestions for a given prefix.
     * Time Complexity: O(L + K log K) where L is prefix length and K is unique queries at that prefix.
     */
    public List<String> search(String prefix) {
        TrieNode curr = root;
        for (char c : prefix.toLowerCase().toCharArray()) {
            if (!curr.children.containsKey(c)) {
                return Collections.emptyList();
            }
            curr = curr.children.get(c);
        }

        // Use a PriorityQueue (Min-Heap) to find top 10 most frequent queries
        PriorityQueue<Map.Entry<String, Integer>> minHeap = new PriorityQueue<>(
                (a, b) -> a.getValue().equals(b.getValue())
                        ? b.getKey().compareTo(a.getKey())
                        : a.getValue() - b.getValue()
        );

        for (Map.Entry<String, Integer> entry : curr.counts.entrySet()) {
            minHeap.offer(entry);
            if (minHeap.size() > 10) {
                minHeap.poll();
            }
        }

        List<String> results = new ArrayList<>();
        while (!minHeap.isEmpty()) {
            results.add(0, minHeap.poll().getKey());
        }
        return results;
    }

    public static void main(String[] args) {
        Questions autocomplete = new Questions();

        // Simulate historical data
        autocomplete.updateFrequency("java tutorial");
        autocomplete.updateFrequency("java tutorial"); // Higher frequency
        autocomplete.updateFrequency("javascript");
        autocomplete.updateFrequency("java download");
        autocomplete.updateFrequency("java 21 features");

        // Test prefix search
        System.out.println("Suggestions for 'jav':");
        List<String> suggestions = autocomplete.search("jav");
        for (int i = 0; i < suggestions.size(); i++) {
            System.out.println((i + 1) + ". " + suggestions.get(i));
        }
    }
}