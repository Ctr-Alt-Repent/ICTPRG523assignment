package Lists;

/**
 * The LinkedList class is a doubly linked list that holds pointers to a head and a tail node.
 * The Node contains an Object and pointers to a next and previous Node to link the list.
 * Functions to manipulate the LinkedList are Append(Node), Prepend(Node), InsertAfter(Node, Node), Remove(Node).
 * Functions to read the Object data from the LinkedList are an overloaded toString().
 */
public class LinkedList
{
    public static void main(String[] args) {

        int[] iArray = {1,2,3,4,5,6,7,8,9,10};
        LinkedList list = new LinkedList();

        for (int i = 0; i < iArray.length; i++)
        {
            list.Append(new Node(iArray[i]));
        }

        System.out.println(list.toString());
        System.out.println(list.toStringReverse());
    }

    public Node head, tail;

    public static class Node
    {
        Object data;
        Node prev, next;

        // Constructor.
        public Node(Object data) { this.data = data; }

        // Getter and Setter.
        public void setData(Object data) { this.data = data; }
        public Object getData() { return data; }

        // Move to connected node.
        public Node getNext() { return next; }
        public Node getPrev() { return prev; }
    }

    // Constructors
    public LinkedList() { head = null; tail = null; }
    public LinkedList(Node n) { Append(n); }
    public LinkedList(Object data) { head = new LinkedList.Node(data); tail = head; }

    /**
     * Overwritten method to concatenate the data in the LinkedList into a single String.
     */
    public String toString()
    {
        Node focus = this.head;
        String s = "Linked List: ";

        while (focus != null)
        {
            s += focus.data.toString();
            if (focus.next != null)
            {
                s += " <-> ";
            }
            focus = focus.next;
        }
        return s;
    }

    /**
     * Testing method to confirm the doubly linked functionality by stepping through the LinkedList in reverse.
     */
    public String toStringReverse()
    {
        Node focus = this.tail;
        String s = "Reversed Linked List: ";

        while (focus != null)
        {
            s += focus.data.toString();
            if (focus.prev != null)
            {
                s += " <-> ";
            }
            focus = focus.prev;
        }
        return s;
    }

    /**
     * Add a Node to the end of the LinkedList.
     * @param n is the Node to be appended to the ArrayList.
     */
    public void Append(Node n)
    {
        n.next = null;
        n.prev = null;

        if (this.head == null || this.tail == null)
        {
            this.head = n;
            this.tail = n;
        }
        else
        {
            this.tail.next = n;
            n.prev = this.tail;
            this.tail = n;
        }
    }

    /**
     * Add a Node to the beginning of the LinkedList.
     * @param n is the Node to be prepended to the ArrayList.
     */
    public void Prepend(Node n)
    {
        if (this.head == null || this.tail == null)
        {
            this.head = n;
            this.tail = n;
        }
        else
        {
            n.next = this.head;
            this.head.prev = n;
            this.head = n;
        }
    }

    /**
     * Find a Node if it exists in the LinkedList.
     * @param data is the Node to be searched for in the ArrayList.
     */
    public Node Find(Node data)
    {
        Node focus = this.head;

        while (focus != null)
        {
            if (focus.data == data)
            {
                return  focus;
            }
            else
            {
                focus = focus.next;
            }
        }
        return null;
    }

    /**
     * Add a Node to the LinkedList after a chosen Node.
     * @param before is the Node that the inserted node will be inserted after.
     * @param after is the Node to be inserted to the LinkedList.
     */
    public void InsertAfter(Node before, Node after)
    {
        if (before == null)
        {
            Append(after);
        }

        if (this.tail == before)
        {
            this.tail = after;
        }

        after.next = before.next;
        after.next.prev = after;
        after.prev = before;
        before.next = after;

    }

    /**
     * Remove a Node from the LinkedList if it exists.
     * @param n is the Node to be removed to the ArrayList.
     */
    public void Remove (Node n)
    {
        if (n == this.head)
        {
            this.head = this.head.next;
            this.head.prev = null;
        }

        if (n == this.tail)
        {
            this.tail = this.tail.prev;
            this.tail.next = null;
        }

        Node focus = this.head.next;
        Node before = null;
        Node after = null;

        while (focus != this.tail)
        {
            if (focus == n)
            {
                before = focus.prev;
                after = focus.next;
                before.next = after;
                after.prev = before;
            }
            focus = focus.next;
        }
    }
}
