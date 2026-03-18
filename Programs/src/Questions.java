import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class Questions {

    // Helper class to represent a Token Bucket for each client
    static class TokenBucket {
        long tokens;
        long lastRefillTime;
        final long maxTokens;
        final long refillRatePerMs; // Tokens per millisecond

        public TokenBucket(long maxTokens, long refillIntervalSeconds) {
            this.maxTokens = maxTokens;
            this.tokens = maxTokens;
            this.lastRefillTime = System.currentTimeMillis();
            // Calculate rate: tokens per ms (e.g., 1000 tokens / 3600000 ms)
            this.refillRatePerMs = maxTokens / (refillIntervalSeconds * 1000);
        }

        // Synchronized to handle concurrent requests for the same client
        public synchronized boolean allowRequest() {
            refill();
            if (tokens > 0) {
                tokens--;
                return true;
            }
            return false;
        }

        private void refill() {
            long now = System.currentTimeMillis();
            long timePassed = now - lastRefillTime;
            long refillAmount = timePassed * refillRatePerMs;

            if (refillAmount > 0) {
                tokens = Math.min(maxTokens, tokens + refillAmount);
                lastRefillTime = now;
            }
        }

        public long getTokens() { return tokens; }
    }

    // Using ConcurrentHashMap for thread-safety across multiple clients
    private Map<String, TokenBucket> clientLimits = new ConcurrentHashMap<>();
    private final long HOURLY_LIMIT = 1000;
    private final long HOUR_IN_SECONDS = 3600;

    public String checkRateLimit(String clientId) {
        TokenBucket bucket = clientLimits.computeIfAbsent(clientId,
                k -> new TokenBucket(HOURLY_LIMIT, HOUR_IN_SECONDS));

        if (bucket.allowRequest()) {
            return "Allowed (" + bucket.getTokens() + " requests remaining)";
        } else {
            return "Denied (0 requests remaining, retry later)";
        }
    }

    public static void main(String[] args) {
        Questions system = new Questions();
        String user = "abc123";

        // Simulate rapid requests
        System.out.println(system.checkRateLimit(user));
        System.out.println(system.checkRateLimit(user));

        // In a real scenario, requests exceeding 1000/hr would return "Denied"
    }
}