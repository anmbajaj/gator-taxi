import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/*
    This class defines the implementation of MinHeap data structure for Ride
 */
public class MinHeap {
    public Ride[] heap;
    public int size;
    public int capacity;

    Map<Integer, Integer> rideMap;

    MinHeap(int capacity){
        this.heap = new Ride[capacity];
        this.size = 0;
        this.capacity = capacity;
        this.rideMap = new HashMap<>();
    }

    /*
        This function returns the left index for the input index
     */
    private int getLeft(int index) {
        return 2 * index + 1;
    }

    /*
        This function returns the right index for the input index
     */
    private int getRight(int index) {
        return 2 * index + 2;
    }

    /*
        This function returns the parent index for the input index
     */
    private int getParent(int index) {
        return (index - 1)/2;
    }

    /*
        This function provides the implementation of insert into min heap
     */
    public boolean insert(Ride newRide) {
        if(size == capacity)    return false;

        heap[size++] = newRide;
        rideMap.put(newRide.rideNo, size - 1);
        heapifyUp(size - 1);
        return true;
    }

    /*
        This function provides the implementation of swap the values of two indexes of the heap
     */
    private void swap(int index1, int index2) {
        Ride temp = heap[index1];
        heap[index1] = heap[index2];
        heap[index2] = temp;

        rideMap.put(heap[index1].rideNo, index1);
        rideMap.put(heap[index2].rideNo, index2);
    }

    /*
        This function provides the implementation of finding the index with minimum ride
     */
    public int findMinIndex(int index1, int index2) {
        return heap[index1].compareTo(heap[index2]) < 0 ? index1 : index2;
    }

    /*
        This function provides the implementation of heapifyUp for the input index
     */
    private void heapifyUp(int index) {
        Ride heapifyRide = heap[index];
        int parent = getParent(index);
        while(parent >= 0 && heapifyRide.compareTo(heap[parent]) < 0) {
            swap(index, parent);
            index = parent;
            parent = getParent(index);
        }
    }

    /*
        This function provides the implementation of heapifyDown for the input index
     */
    private void heapifyDown(int index) {
        while(index < size) {
            int minIndex = index, left = getLeft(index), right = getRight(index);
            if(left < size){
                minIndex = findMinIndex(minIndex, left);
                if(right < size)
                    minIndex = findMinIndex(minIndex, right);
            }
            if(minIndex == index)   break;
            swap(index, minIndex);
            index = minIndex;
        }
    }

    /*
        This function provides the implementation of extracting the minimum from the heap
     */
    public Ride extractMin() {
        if(size == 0)   return null;

        swap(0, --size);
        heapifyDown(0);
        rideMap.remove(heap[size].rideNo);
        return heap[size];
    }

    /*
        This function provides the implementation of deleting the key with input index
     */
    public void deleteKey(int index) {
        heap[index].rideCost = Integer.MIN_VALUE;
        heapifyUp(index);
        extractMin();
    }

    /*
        This function provides the implementation of deleting the node with input rideNo
     */
    public void deleteRide(int rideNo) {
        if(rideMap.containsKey(rideNo)){
            deleteKey(rideMap.get(rideNo));
            rideMap.remove(rideNo);
        }
    }

    @Override
    public String toString() {
        return "MinHeap{" +
                "heap=" + Arrays.toString(heap) +
                ", size=" + size +
                ", capacity=" + capacity +
                ", rideMap=" + rideMap +
                '}';
    }
}
