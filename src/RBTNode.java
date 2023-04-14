/*
    This is POJO file that defines any RBT node
 */
public class RBTNode {
    public Ride ride;
    public RBTNode left;
    public RBTNode right;
    public RBTNode parent;
    public Color color;

    RBTNode(Ride ride){
        this.ride = ride;
        this.left = null;
        this.right = null;
        this.parent = null;
        this.color = Color.RED;
    }

    @Override
    public String toString() {
        return String.valueOf(ride.rideNo) + "(" + color + ")";
    }
}
