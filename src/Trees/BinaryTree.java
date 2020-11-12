package Trees;

import java.util.ArrayList;
import java.util.Random;
import java.util.List;

/**
 * The BinaryTree can hold a single root Node.
 * Functions that allow manipulation of the BinaryTree are Insert(Node), Remove(Node), Find(Node).
 * Functions that traverse the BinaryTree are preOrderTraverse(), inOrderTraverse(), postOrderTraverse().
 * Each Node contains a key and an Object and 2 pointers to child Nodes.
 * Node functions for variables include get(), set() and an overridden toString().
 */
public class BinaryTree {

    public static void main(String[] args)
    {
        BinaryTree tree = new BinaryTree();
        tree.insert(new Node(50, "Joel"));

        Random randomNumber = new Random();
        int upperBound = 100;


        // Testing insert
        for (int i = 0; i < 10; i++)
        {
            tree.insert(new Node(randomNumber.nextInt(upperBound), "!"));
        }

        // Testing find
        for (int i = 0; i < upperBound; i++)
        {
            if (tree.find(i) != null)
                System.out.println("Successfully found: " + tree.find(i).data);
        }

        // Testing remove
        if (tree.find(50) != null)
        {
            System.out.println("Successfully found: " + tree.find(50).data);
            tree.remove(tree.find(50));
        }

        // Testing tree traversals
        System.out.println("Pre Order:" + tree.preOrderTraverse());
        System.out.println("In Order:" + tree.inOrderTraverse());
        System.out.println("Post Order:" + tree.postOrderTraverse());

    }

    Node root;

    public static class Node
    {
        private int key;
        private Object data;
        private Node left, right;

        // Constructor.
        public Node(int key, Object data) { this.key = key; this.data = data; left = null; right = null; }

        // Getters and Setters.
        public void setData(Object data) { this.data = data; }
        public void setLeft(Node node) { this.left = node; }
        public void setRight(Node node) { this.right = node; }
        public int getKey() { return key; }
        public Object getData() { return data; }
        public Node getLeft() { return left; }
        public Node getRight() { return right; }

        public String toString()
        {
            return Integer.toString(this.key) + " = " + this.data.toString();
        }
    }

    // Tree Constructors
    public BinaryTree() { root = null; }
    public BinaryTree(Node node) { root = node; }
    public BinaryTree(int key, Object data) { root = new Node(key, data); }
    public BinaryTree(Node[] nodes) {
        for (Node node : nodes)
        {
            this.insert(node);
        }
    }
    public BinaryTree(List<Node> nodes) {
        for (Node node : nodes)
        {
            this.insert(node);
        }
    }
    public BinaryTree(ArrayList<Node> nodes) {
        for (Node node : nodes)
        {
            this.insert(node);
        }
    }

    /**
     * Begin the recursive process to insert a Node into the BinaryTree.
     * @param insertNode is the Node to be inserted into the BinaryTree.
     */
    public void insert (Node insertNode)
    {
        root = insert(root, insertNode);
    }

    /**
     * Recursive override that compares 2 nodes and steps through the BinaryTree.
     * @param focusNode is the Node that the Node to be inserted is compared with.
     * @param insertNode is the Node to be inserted into the BinaryTree.
     */
    private Node insert(Node focusNode, Node insertNode)
    {
        if (focusNode == null)
        {
            return insertNode;
        }

        if (insertNode.key < focusNode.key)
        {
            focusNode.left = insert(focusNode.left, insertNode);
            return focusNode;
        }
        else if (insertNode.key > focusNode.key)
        {
            focusNode.right = insert(focusNode.right, insertNode);
            return focusNode;
        }
        else
        {
            return focusNode;
        }
    }

    /**
     * Begin the recursive process to remove a Node from the BinaryTree.
     * @param removeNode is the Node to be removed from the BinaryTree.
     */
    public void remove (Node removeNode)
    {
        root = remove(root, removeNode);
    }

    /**
     * Recursive override that compares 2 nodes and steps through the BinaryTree.
     * Once the Node to be removed has been located 1 of 3 ways to reconnect the Node's children will be followed.
     * @param focusNode is the Node that the Node to be removed is compared with.
     * @param removeNode is the Node to be removed from the BinaryTree.
     */
    public Node remove (Node focusNode, Node removeNode)
    {
        if (focusNode == null)
        {
            return null;
        }

        if (focusNode.key == removeNode.key)
        {
            // focusNode is a leaf node. Null assigned to parent.
            if (focusNode.left == null && focusNode.right == null) {
                return null;
            }
            // focusNode has 1 child. Child assigned to parent.
            if (focusNode.left == null)
            {
                return focusNode.right;
            }
            if (focusNode.right == null)
            {
                return  focusNode.left;
            }
            // focusNode has 2 children. Find smallest node of node's right subtree.
            Node smallestNode = findSmallestNode(focusNode.right);
            focusNode.key = smallestNode.key;
            focusNode.data = smallestNode.data;
            focusNode.right = remove(focusNode.right, smallestNode);
            return  focusNode;
        }

        if (removeNode.key < focusNode.key)
        {
            focusNode.left = remove(focusNode.left, removeNode);
            return focusNode;
        }
        focusNode.right = remove(focusNode.right, removeNode);
        return focusNode;
    }

    /**
     * Helper function in the Node removal process after the Node to be removed has been found.
     * @param node is the beginning of the branch that is searched for its smalled Node member.
     */
    private Node findSmallestNode(Node node)
    {
        return node.left == null ? node : findSmallestNode(node.left);
    }

    /**
     * Begin the recursive process to find a Node in the BinaryTree.
     * @param key is the key value of the Node searched for.
     */
    public Node find (int key)
    {
        return find(root, key);
    }

    /**
     * Recursive override that searches Nodes for the key value and steps through the BinaryTree.
     * Once the Node containing the key value has been located it will be returned.
     * @param focusNode is the Node that will be searched to see it of contains the key value.
     * @param key is the key value of the Node that is being searched for.
     */
    private Node find(Node focusNode, int key)
    {
        if (focusNode != null)
        {
            if (key < focusNode.key)
            {
                return (find(focusNode.left, key));
            }
            else if (key > focusNode.key)
            {
                return (find(focusNode.right, key));
            }
            else
            {
                return focusNode;
            }
        }
        else
        {
            return null;
        }
    }

    /**
     * Binary tree pre-order traversal algorithm to get the Nodes in the BinaryTree into an ArrayList.
     */
    public ArrayList<Node> preOrderTraverse()
    {
        return preOrderTraverse(root);
    }

    /**
     * Recursive override that adds the focusNode to the ArrayList, then calls itself with the focusNode's left then right children.
     * @param focusNode is the Node currently being added to the ArrayList.
     */
    private ArrayList<Node> preOrderTraverse(Node focusNode)
    {
        ArrayList<Node> nodes = new ArrayList<>();

        if (focusNode != null)
        {
            nodes.add(focusNode);
            nodes.addAll(preOrderTraverse(focusNode.left));
            nodes.addAll(preOrderTraverse(focusNode.right));
        }

        return nodes;
    }

    /**
     * Binary tree in-order traversal algorithm to get the Nodes in the BinaryTree into an ArrayList.
     */
    public ArrayList<Node> inOrderTraverse()
    {
        return inOrderTraverse(root);
    }

    /**
     * Recursive override that calls itself with focusNode's left child before adding the focusNode to the ArrayList then calling itself with the focusNode's right child.
     * @param focusNode is the Node currently being added to the ArrayList.
     */
    private ArrayList<Node> inOrderTraverse(Node focusNode)
    {
        ArrayList<Node> nodes = new ArrayList<>();

        if (focusNode != null)
        {
            nodes.addAll(inOrderTraverse(focusNode.left));
            nodes.add(focusNode);
            nodes.addAll(inOrderTraverse(focusNode.right));
        }

        return nodes;
    }

    /**
     * Binary tree post-order traversal algorithm to get the Nodes in the BinaryTree into an ArrayList.
     */
    public ArrayList<Node> postOrderTraverse()
    {
        return postOrderTraverse(root);
    }

    /**
     * Recursive override that calls itself with focusNode's left then right children before adding the focusNode to the ArrayList.
     * @param focusNode is the Node currently being added to the ArrayList.
     */
    private ArrayList<Node> postOrderTraverse(Node focusNode)
    {
        ArrayList<Node> nodes = new ArrayList<>();

        if (focusNode != null)
        {
            nodes.addAll(postOrderTraverse(focusNode.left));
            nodes.addAll(postOrderTraverse(focusNode.right));
            nodes.add(focusNode);
        }

        return nodes;
    }
}
