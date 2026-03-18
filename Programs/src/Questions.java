import java.util.*;

public class Questions {
    // Enum to track the state of each parking spot
    enum Status { EMPTY, OCCUPIED, DELETED }

    static class ParkingSpot {
        String licensePlate;
        long entryTime;
        Status status;

        ParkingSpot() {
            this.status = Status.EMPTY;
        }
    }

    private final int CAPACITY = 500;
    private ParkingSpot[] spots = new ParkingSpot[CAPACITY];
    private int occupiedCount = 0;

    public Questions() {
        for (int i = 0; i < CAPACITY; i++) {
            spots[i] = new ParkingSpot();
        }
    }

    // Custom Hash Function based on license plate
    private int hash(String licensePlate) {
        return Math.abs(licensePlate.hashCode()) % CAPACITY;
    }

    /**
     * Parks a vehicle using Linear Probing.
     * Logic: If spot H is taken, try H+1, H+2, etc.
     */
    public String parkVehicle(String licensePlate) {
        if (occupiedCount >= CAPACITY) return "Error: Lot Full";

        int preferredSpot = hash(licensePlate);
        int currentSpot = preferredSpot;
        int probes = 0;

        // Linear Probing: Find first non-occupied spot
        while (spots[currentSpot].status == Status.OCCUPIED) {
            currentSpot = (currentSpot + 1) % CAPACITY;
            probes++;
        }

        spots[currentSpot].licensePlate = licensePlate;
        spots[currentSpot].entryTime = System.currentTimeMillis();
        spots[currentSpot].status = Status.OCCUPIED;
        occupiedCount++;

        return String.format("Assigned spot #%d (%d probes)", currentSpot, probes);
    }

    /**
     * Frees a spot and calculates the fee.
     */
    public String exitVehicle(String licensePlate) {
        int preferredSpot = hash(licensePlate);
        int currentSpot = preferredSpot;
        int initialSpot = currentSpot;

        // Search for the vehicle
        do {
            if (spots[currentSpot].status == Status.EMPTY) break; // Optimization: stop at EMPTY

            if (spots[currentSpot].status == Status.OCCUPIED &&
                    spots[currentSpot].licensePlate.equals(licensePlate)) {

                long durationMs = System.currentTimeMillis() - spots[currentSpot].entryTime;
                double fee = (durationMs / 1000.0) * 5.0; // $5 per "second" for demo

                spots[currentSpot].status = Status.DELETED; // Mark as deleted for probing integrity
                occupiedCount--;
                return String.format("Spot #%d freed. Fee: $%.2f", currentSpot, fee);
            }
            currentSpot = (currentSpot + 1) % CAPACITY;
        } while (currentSpot != initialSpot);

        return "Vehicle not found.";
    }

    public void getStatistics() {
        double occupancy = ((double) occupiedCount / CAPACITY) * 100;
        System.out.printf("--- Parking Stats ---\nOccupancy: %.1f%%\nSpots Taken: %d/%d\n",
                occupancy, occupiedCount, CAPACITY);
    }

    public static void main(String[] args) {
        Questions lot = new Questions();

        // Simulate vehicles arriving
        System.out.println(lot.parkVehicle("ABC-1234"));
        System.out.println(lot.parkVehicle("ABC-1235")); // Likely collision depending on hash
        System.out.println(lot.parkVehicle("XYZ-9999"));

        lot.getStatistics();

        System.out.println(lot.exitVehicle("ABC-1234"));
    }
}