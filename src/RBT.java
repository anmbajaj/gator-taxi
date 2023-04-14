import java.util.List;

/*
    Red Black Tree Implementation
 */
public class RBT {
    public RBTNode root;

    RBT() {
        this.root = null;
    }

    /*
        The function is used as part of fixing up the coloring
        of red-black tree, while insertion or deletion operations.

        A                                          B
       / \        ->(rotate left on A)->          / \
      C   B                                      A   E
         / \                                    / \
        D   E                                  C   D
    */
    private RBTNode rotateLeft(RBTNode node) {
        RBTNode rightChild = node.right;
        node.right = rightChild.left;

        if(rightChild.left != null)     rightChild.left.parent = node;
        rightChild.parent = node.parent;

        if(node.parent == null)     this.root = rightChild;
        else if(node == node.parent.left)   node.parent.left = rightChild;
        else    node.parent.right = rightChild;

        rightChild.left = node;
        node.parent = rightChild;

        return node;
    }

    /*
        The function is used as part of fixing up the coloring
        of red-black tree, while insertion or deletion operations.

            B                                          A
           / \        ->(rotate right on B)->         / \
          A   E                                      C   B
         / \                                            / \
        C   D                                          D   E
     */
    private RBTNode rotateRight(RBTNode node) {
        RBTNode leftChild = node.left;
        node.left = leftChild.right;

        if(leftChild.right != null)     leftChild.right.parent = node;
        leftChild.parent = node.parent;

        if(node.parent == null)     this.root = leftChild;
        else if(node == node.parent.right)   node.parent.right = leftChild;
        else    node.parent.left = leftChild;

        leftChild.right = node;
        node.parent = leftChild;

        return node;
    }

    /*
        The function provides the implementation of fixing 2 consecutive red nodes
        (generated while insertion of nodes) in the red black tree.
        The function is called after each insertion.
     */
    private void fixColorViolations(RBTNode node){
        //If the inserted node is root, change the color to black
        if(node == root){
            root.color = Color.BLACK;
            return;
        }

        //If the parent of newly inserted node is black, nothing to do
        if(node.parent.color == Color.BLACK)    return;
        RBTNode parent = node.parent, grandParent = node.parent.parent, uncle = null;
        if(grandParent != null){
            uncle = (parent == grandParent.left) ?  grandParent.right : grandParent.left;
            //If the color of uncle is red, make uncle black, parent black, and grandparent red
            //Propagate the violation to grandparent
            if(uncle != null && uncle.color == Color.RED) {
                parent.color = Color.BLACK;
                uncle.color = Color.BLACK;
                grandParent.color = Color.RED;
                fixColorViolations(grandParent);
            } else { // If uncle is null or its color is black
                if(parent == grandParent.left && node == parent.right) //If parent is left child of grandparent and node is right child of parent(LR case), rotate left first & convert to LL case
                    node = rotateLeft(parent);
                else if(parent == grandParent.right && node == parent.left) //If parent is right child of grandparent and node is left child of parent(RL case), rotate right first & convert to RR case
                    node = rotateRight(parent);
                parent = node.parent;
                // fix the colors, after rotation
                parent.color = Color.BLACK;
                grandParent.color = Color.RED;

                //Fix the LL case by right rotation of grandparent
                if(node == parent.left)    rotateRight(grandParent);
                //Fix the RR case by right rotation of grandparent
                else if(node == parent.right)    rotateLeft(grandParent);
            }
        }
    }

    /*
        This function provides the implementation of insert operation in the red black tree
     */
    public void insert(RBTNode newNode) {
        RBTNode prevNode = null;
        RBTNode currentNode = this.root;

        while(currentNode != null) {
            prevNode = currentNode;
            //If the ride no already exists, print "Duplicate RideNumber" and exit the system
            if(newNode.ride.rideNo == currentNode.ride.rideNo){
                System.out.println("Duplicate RideNumber");
                System.exit(0);
                return;
            } else if(newNode.ride.rideNo < currentNode.ride.rideNo)   currentNode = currentNode.left; // If new node rideno is less than current node rideno, go to left subtree
            else if(newNode.ride.rideNo > currentNode.ride.rideNo)   currentNode = currentNode.right; // If new node rideno is greater than current node rideno, go to right subtree
        }

        //update parent of new node
        newNode.parent = prevNode;
        //if prevnode is null, new node is root of the tree
        if(prevNode == null)    root = newNode;
        else if(newNode.ride.rideNo < prevNode.ride.rideNo) prevNode.left = newNode;//update left pointer of parent node
        else prevNode.right = newNode; // update right pointer of parent node

        //fix violations, if any
        fixColorViolations(newNode);
    }

    /*
        This function provides an implementation of search a RBT node with given ride no
     */
    public RBTNode search(int rideNo) {
        RBTNode current = root;
        while(current != null && current.ride.rideNo != rideNo){
            if(rideNo < current.ride.rideNo)    current = current.left;
            else    current = current.right;
        }
        return current;
    }

    /*
        This function is a helper function for range search.
        It performs an in-order traversal and adds the rides into rides list
     */
    private void rangeSearchHelper(RBTNode node, int lowerBound, int upperBound, List<Ride> rides) {
        if(node == null)    return;

        if(lowerBound < node.ride.rideNo)   rangeSearchHelper(node.left, lowerBound, upperBound, rides); // If lower bound is less than node rideno, search in left subtree
        if(lowerBound <= node.ride.rideNo && upperBound >= node.ride.rideNo)    rides.add(node.ride); // If current node ride no is between lower bound and upper bound, add to rides list
        if(upperBound > node.ride.rideNo)   rangeSearchHelper(node.right, lowerBound, upperBound, rides); // If upper bound is greater than node rideno, search in right subtree
    }

    /*
        This function provides an implementation of search the rides between teh given bounds
     */
    public void rangeSearch(int rideNo1, int rideNo2, List<Ride> rides) {
        rangeSearchHelper(root, rideNo1, rideNo2, rides);
    }

    /*
        This function provides the successor info for the input node.
        The successor node is needed, while performing the delete operation on the RBT node, with 2 child
     */
    public RBTNode getSuccessor(RBTNode node) {
        RBTNode successor = node;
        while(successor.left != null)
            successor = successor.left;

        return successor;
    }

    /*
        This function provides the replacement node for the node to be deleted in the RBT
     */
    public RBTNode getReplacementNode(RBTNode node) {
        //If the node has two child, return the in-order successor of the node
        if(node.left != null && node.right != null) {
            return getSuccessor(node.right);
        }
        //if the node is a leaf node, return null
        if (node.left == null && node.right == null)    return null;
        //If the node left child is not null return left
        if(node.left != null)   return  node.left;
        //If the node right child is not null return right
        else   return  node.right;
    }

    /*
        This function is a helper function, which helps swap the rides values between 2 RBT nodes.
        This function needed when performing the delete operation
     */
    private void swapRides(RBTNode node1, RBTNode node2) {
        Ride temp = node1.ride;
        node1.ride = node2.ride;
        node2.ride = temp;
    }

    /*
        This function provides the sibling info for the input node.
        The sibling node is needed, while performing the delete operation on the RBT
     */
    private RBTNode getSibling(RBTNode node) {
        if(node == root)    return null;
        return node.parent.left == node ? node.parent.right : node.parent.left;
    }

    /*
        This function checks if the input node has any red child
        The red child info is needed, while performing the delete operation on the RBT
     */
    private boolean hasRedChild(RBTNode node) {
        return (node.left != null && node.left.color == Color.RED) || (node.right != null && node.right.color == Color.RED);
    }


    /*
        This function provides the implementation of fixing the double black,
        that is generated as part of delete node
     */
    private void fixDoubleBlack(RBTNode node) {
        if(node == root)    return;

        RBTNode parent = node.parent, sibling = getSibling(node);

        if(sibling != null) {
            if(sibling.color == Color.RED)  { // If sibling color is red
                if(sibling.parent.left == sibling)  rotateRight(parent); // If sibling is left child of its parent, rotate right on parent
                else    rotateLeft(parent); // If sibling is right child of its parent, rotate left on parent

                //fix the colors of parent and sibling
                parent.color = Color.RED;
                sibling.color = Color.BLACK;
                fixDoubleBlack(node); // since the double black still exists on the node, call fix double black for node
            } else {
                if(hasRedChild(sibling)) {
                    // If the sibling has left red child
                    if(sibling.left != null && sibling.left.color == Color.RED) {
                        if(sibling.parent.left == sibling) { // If sibling is left child of its parent (LL case)
                            //fix the colors
                            sibling.left.color = sibling.color;
                            sibling.color = parent.color;
                            rotateRight(parent); // rotate right
                        } else { // If sibling is right child of its parent (RL case)
                            sibling.left.color = parent.color; // fix colors
                            rotateRight(sibling); // rotate right on sibling
                            rotateLeft(parent); // rotate left on parent
                        }
                    } else { // If the sibling has right red child
                        if(sibling.parent.left == sibling){ // If sibling is left child of its parent (LR case)
                            sibling.right.color = parent.color; // fix colors
                            rotateLeft(sibling); // rotate left on sibling
                            rotateRight(parent); // rotate right on parent
                        } else { // If sibling is right child of its parent (RR case)
                            //fix the colors
                            sibling.right.color = sibling.color;
                            sibling.color = parent.color;
                            rotateLeft(parent); // rotate right on parent
                        }
                    }
                } else { // if sibling has no red child
                    //fix the color of sibling
                    sibling.color = Color.RED;
                    if(parent.color == Color.BLACK) fixDoubleBlack(parent); // If parent was black and since the double black os propagated to parent now, call fix dpuble black
                    else    parent.color = Color.BLACK; // Since parent was red, it can cover the deficit, make it black.
                }
            }
        } else fixDoubleBlack(parent); // if sibling is null, fix double black on parent
    }

    /*
        This function provides the implementation for node deletion, with input RBT node
     */
    public void deleteRBTNode(RBTNode nodeToDelete) {
        RBTNode replacementNode = getReplacementNode(nodeToDelete); // get the replacement node
        boolean bothBlack = (replacementNode == null || replacementNode.color == Color.BLACK) && (nodeToDelete.color == Color.BLACK); // check if replacement node and the node to delete are both black

        if(replacementNode == null){ // If replacement node is null
            if(nodeToDelete == root)    root = null; // If node to delete us root, make the root null
            else {
                if(bothBlack)   fixDoubleBlack(nodeToDelete); //If the flag is true, call fix double black case on node to delete
                else if(getSibling(nodeToDelete) != null)   getSibling(nodeToDelete).color = Color.RED; // else if sibling is not null color, it red

                if(nodeToDelete.parent.left == nodeToDelete)    nodeToDelete.parent.left = null; //If node to delete is left child of parent, make left child of parent null
                else    nodeToDelete.parent.right = null; //If node to delete is right child of parent, make right child of parent null
            }
            return;
        }

        //If the node to delete has atmost one child
        if(nodeToDelete.left == null || nodeToDelete.right == null) {
            if(nodeToDelete == root){ // If node to delete is root
                nodeToDelete.ride = replacementNode.ride; // copy the ride details from replacement node to root node
                nodeToDelete.left = nodeToDelete.right = null; // make root's left and right child null
            } else { // if node to delete is not root
                if(nodeToDelete.parent.left == nodeToDelete)    nodeToDelete.parent.left = replacementNode;  // if node to delete is left child of parent, make replacement node as left child
                else    nodeToDelete.parent.right = replacementNode; // if node to delete is right child of parent, make replacement node as right child

                replacementNode.parent = nodeToDelete.parent; // Update parent node

                if(bothBlack)    fixDoubleBlack(replacementNode); // if both of node to delete and replacement nodes were black, call the fix double black
                else    replacementNode.color = Color.BLACK; // else make the replacement node as black
            }
            return;
        }

        // if the node to delete had both left and right child, swap rides between node to delete and replacement node
        swapRides(nodeToDelete, replacementNode);
        // call delete for replacement node
        deleteRBTNode(replacementNode);
    }

    /*
        This function provides the implementation of delete function in RBT for input ride no
     */
    public void delete(int rideNo) {
        RBTNode nodeToDelete = search(rideNo); // search the node to delete
        if(nodeToDelete == null)    return;

        deleteRBTNode(nodeToDelete);
    }
}
