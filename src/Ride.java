/*
    Ride POJO class, implements the comparable interface
    to compare two objects based on rideCost followed by
    tripDuration, if needed(objects with same rideCost)
 */
public class Ride implements Comparable<Ride>{
    public int rideNo;
    public int rideCost;
    public int tripDuration;

    Ride(int rideNo, int rideCost, int tripDuration){
        this.rideNo = rideNo;
        this.rideCost = rideCost;
        this.tripDuration = tripDuration;
    }

    /*
        This function compares two rides on the basis of right cost,
        followed by trip duration(if ride cost are same).
     */
    @Override
    public int compareTo(Ride ride) {
        return this.rideCost < ride.rideCost || (this.rideCost == ride.rideCost && this.tripDuration < ride.tripDuration) ? -1 : 1;
    }

    @Override
    public String toString() {
        return "Ride{" +
                "rideNo=" + rideNo +
                ", rideCost=" + rideCost +
                ", tripDuration=" + tripDuration +
                '}';
    }
}
