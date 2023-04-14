import java.io.*;
import java.util.ArrayList;
import java.util.List;

/*
    GatorTaxi application that provides the information about the pending rides.
    It offers functions like:
    Print(rideNo) -> that prints the triplet (rideNo, rideCost, tripDuration)
    Print(rideNo1, rideNo2) -> that prints all triplets (rx, rideCost, tripDuration) for which rideNo1 <= rx <= rideNo2
    Insert(rideNo, rideCost, tripDuration) -> inserts new ride if the rideno is not duplicate
    GetNextRide() -> returns the ride with lowest rideCost. If two rides have same rideCost, one with lower trip duration is returned
    CancelRide(rideNo) -> deletes the triplet (rideNo, rideCost, tripDuration) if the rideNo exists
    UpdateTrip(rideNo, newTripDuration) ->
        1. if newTripDuration <= existingTripDuration, just update the tripDuration of the ride to newTripDuration
        2. if existingTripDuration < newTripDuration <= 2 * existingTripDuration, cancel the existing ride and add a new ride (rideNo, existingRideCost + 10, newTripDuration)
        3. if newTripDuration > 2 * existingTripDuration, cancel the existing ride
 */
public class gatorTaxi {

    private static final int maxSize = 2000;
    private MinHeap minCostRides;
    private RBT rides;

    gatorTaxi(){
        this.minCostRides = new MinHeap(maxSize);
        this.rides = new RBT();
    }

    /*
        This function provides implementation of cancellation of ride with input rideNo, if it exists
     */
    public void cancelTrip(int rideNo) { //O(log(n))
        RBTNode node = rides.search(rideNo);
        if(node != null) {
            rides.delete(rideNo);
            minCostRides.deleteRide(rideNo);
        }
    }

    /*
        This function provides implementation for update of the trip with input rideNo and newTripDuration, according to following rules
        1. if newTripDuration <= existingTripDuration, just update the tripDuration of the ride to newTripDuration
        2. if existingTripDuration < newTripDuration <= 2 * existingTripDuration, cancel the existing ride and add a new ride (rideNo, existingRideCost + 10, newTripDuration)
        3. if newTripDuration > 2 * existingTripDuration, cancel the existing ride
     */
    public void updateTrip(int rideNo, int newTripDuration) { // O(log(n))
        RBTNode node = rides.search(rideNo);
        if(node != null) {
            int rideCost = node.ride.rideCost, tripDuration = node.ride.tripDuration;
            if(newTripDuration <= tripDuration){
                node.ride.tripDuration = newTripDuration;
            }
            else if(newTripDuration > tripDuration && newTripDuration <= 2 * (tripDuration)){
                rides.deleteRBTNode(node);
                minCostRides.deleteRide(rideNo);
                insert(rideNo, rideCost + 10, newTripDuration);
            } else if (newTripDuration > 2 * tripDuration) {
                rides.deleteRBTNode(node);
                minCostRides.deleteRide(rideNo);
            }
        }
    }

    /*
        This function provides implementation of get next ride with the lowest cost.
        In case of tie, it returns the ride with the lowest trip duration
     */
    public void getNextRide() {
        Ride result = minCostRides.extractMin();
        if(result == null)   System.out.println("No active ride requests");
        else {
            System.out.println("(" + result.rideNo + "," + result.rideCost + "," + result.tripDuration + ")");
            rides.delete(result.rideNo);
        }
    }

    /*
        This function provides implementation of printing the ride triplets between the input rideNo1 and rideNo2
     */
    public void rangeSearch(int rideNo1, int rideNo2) { // O(log(n) + S)
        List<Ride> result = new ArrayList<>();

        rides.rangeSearch(rideNo1, rideNo2, result);
        StringBuilder stringBuilder = new StringBuilder();
        if(result.size() == 0)   stringBuilder.append("(0,0,0)");
        else {
            stringBuilder.append("(" + result.get(0).rideNo + "," + result.get(0).rideCost + "," + result.get(0).tripDuration + ")");
            for(int i = 1; i < result.size(); i++) {
                stringBuilder.append(",");
                stringBuilder.append("(" + result.get(i).rideNo + "," + result.get(i).rideCost + "," + result.get(i).tripDuration + ")");
            }
        }

        System.out.println(stringBuilder.toString());
    }

    /*
        This function provides implementation of printing the triplet with input rideNo
     */
    public void print(int rideNo) { // Search and print - O(logn)
        RBTNode node = rides.search(rideNo);
        if(node != null) System.out.println("(" + node.ride.rideNo + "," + node.ride.rideCost + "," + node.ride.tripDuration + ")");
        else System.out.println("(0,0,0)");
    }

    /*
        This function provides implementation of insertion of new ride with input rideNo, rideCost, tripDuration
     */
    public void insert(int rideNo, int rideCost, int tripDuration) {  // O(log(n))
        Ride ride = new Ride(rideNo, rideCost, tripDuration);
        RBTNode rbtNode = new RBTNode(ride);
        if(minCostRides.insert(ride))
            rides.insert(rbtNode);
    }

    /*
        This function is the starter of GatorTaxi application
     */
    public static void main(String[] args) throws IOException {
        gatorTaxi gTaxi = new gatorTaxi();

        //Create a new output file
        File outputFile = new File("output_file.txt");
        //Attach the output file to print stream
        PrintStream printStream = new PrintStream(outputFile);
        //Redirect the system output stream to print stream, such that all print commands are redirected into the output_file.txt
        System.setOut(printStream);

        // Buffered reader that that input as the file reader with file name as passed in program arguments
        BufferedReader bufferedReader = new BufferedReader(new FileReader(args[0]));
        while(true) {
            String command = bufferedReader.readLine();
            if(command.startsWith("Insert")) { //If the input command starts with Insert, call insertion of new ride
                String [] inputs = command.replaceAll(".*\\(|\\).*", "").trim().split(",");
                gTaxi.insert(Integer.parseInt(inputs[0]), Integer.parseInt(inputs[1]), Integer.parseInt(inputs[2]));
            } else if (command.startsWith("Print")) { //If the input command starts with Print
                String [] inputs = command.replaceAll(".*\\(|\\).*", "").trim().split(",");
                if(inputs.length == 1)  gTaxi.print(Integer.parseInt(inputs[0])); // if the command has only one argument, then call print logic of gator taxi
                if(inputs.length == 2)  gTaxi.rangeSearch(Integer.parseInt(inputs[0]), Integer.parseInt(inputs[1])); // if the command has only two arguments, then call range search of gator taxi
            } else if(command.startsWith("GetNextRide()")) { //If the input command starts with GetNextRide, call getNextRide logic of gator taxi
                gTaxi.getNextRide();
            } else if(command.startsWith("UpdateTrip")) { //If the input command starts with UpdateTrip, call update trip logic of gator taxi
                String [] inputs = command.replaceAll(".*\\(|\\).*", "").trim().split(",");
                gTaxi.updateTrip(Integer.parseInt(inputs[0]), Integer.parseInt(inputs[1]));
            } else if(command.startsWith("CancelRide")) { //If the input command starts with CancelRide, call cancel ride logic of gator taxi
                String [] inputs = command.replaceAll(".*\\(|\\).*", "").trim().split(",");
                gTaxi.cancelTrip(Integer.parseInt(inputs[0]));
            }
        }
    }
}
