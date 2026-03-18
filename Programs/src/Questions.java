import java.util.*;

public class Questions {

    static class Transaction {
        String id;
        double amount;
        String merchant;
        long timestamp; // epoch milliseconds
        String accountId;

        Transaction(String id, double amount, String merchant, long timestamp, String accountId) {
            this.id = id;
            this.amount = amount;
            this.merchant = merchant;
            this.timestamp = timestamp;
            this.accountId = accountId;
        }

        @Override
        public String toString() {
            return String.format("[ID: %s, Amt: $%.2f, Merchant: %s]", id, amount, merchant);
        }
    }

    /**
     * Classic Two-Sum: Finds pairs that sum exactly to the target.
     * Use Case: Identifying split transactions or money laundering patterns.
     */
    public List<String> findTwoSum(List<Transaction> transactions, double target) {
        List<String> pairs = new ArrayList<>();
        Map<Double, Transaction> seen = new HashMap<>();

        for (Transaction tx : transactions) {
            double complement = target - tx.amount;
            if (seen.containsKey(complement)) {
                pairs.add(tx.id + " & " + seen.get(complement).id);
            }
            seen.put(tx.amount, tx);
        }
        return pairs;
    }

    /**
     * Duplicate Detection: Finds transactions with same amount and merchant
     * but originating from different accounts.
     */
    public void detectDuplicates(List<Transaction> transactions) {
        // Key: amount + merchant, Value: List of transactions
        Map<String, List<Transaction>> groups = new HashMap<>();

        for (Transaction tx : transactions) {
            String key = tx.amount + ":" + tx.merchant;
            groups.computeIfAbsent(key, k -> new ArrayList<>()).add(tx);
        }

        System.out.println("--- Duplicate/Fraud Report ---");
        for (List<Transaction> group : groups.values()) {
            if (group.size() > 1) {
                long uniqueAccounts = group.stream().map(t -> t.accountId).distinct().count();
                if (uniqueAccounts > 1) {
                    System.out.println("Suspicious activity (Same Merchant/Amt, Different Accs):");
                    group.forEach(t -> System.out.println("  " + t));
                }
            }
        }
    }

    public static void main(String[] args) {
        Questions engine = new Questions();
        List<Transaction> dailyTxs = Arrays.asList(
                new Transaction("1", 500.0, "Store A", System.currentTimeMillis(), "Acc_1"),
                new Transaction("2", 300.0, "Store B", System.currentTimeMillis(), "Acc_2"),
                new Transaction("3", 200.0, "Store C", System.currentTimeMillis(), "Acc_3"),
                new Transaction("4", 500.0, "Store A", System.currentTimeMillis(), "Acc_4") // Potential Duplicate
        );

        // Task 1: Find pairs summing to 500
        System.out.println("Pairs summing to 500: " + engine.findTwoSum(dailyTxs, 500.0));

        // Task 2: Detect duplicates
        engine.detectDuplicates(dailyTxs);
    }
}