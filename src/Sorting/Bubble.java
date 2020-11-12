package Sorting;

import CDArchiveProject.CDRecord;

import java.util.ArrayList;

/**
 * Bubble.sort(ArrayList) uses the bubble sorting algorithm to arrange the array
 * by an element's Title variable in ascending order.
 */
public class Bubble {

    public static void main(String[] args) {
        ArrayList<CDRecord> records = new ArrayList<>();

        for (int i = 0; i < 20; i++)
        {
            int randomBarcode = (int)(Math.random()*100);
            records.add(new CDRecord(randomBarcode));
        }

        System.out.println("Before sort: " + records.toString());
        Bubble.sort(records);
        System.out.println("After sort:  " + records.toString());
    }

    /**
     * Use a bubble sorting algorithm to sort an ArrayList.
     * @param records is the data structure that will be sorted.
     */
    public static void sort(ArrayList<CDRecord> records)
    {
        boolean swapped = true;

        // As long as swapped evaluates to true we will keep checking the List for unsorted elements.
        while(swapped)
        {
            // At the beginning of each sweep of the List we have not swapped any elements yet so swapped is set to false.
            swapped = false;

            for(int i = 1; i < records.size(); i++)
            {
                // Get the elements to compare.
                CDRecord left = records.get(i-1);
                CDRecord right = records.get(i);

                // Swap the elements if left's barcode is greater than right's.
                if (left.getTitle().compareTo(right.getTitle()) > 0)
                {
                    records.set(i, left);
                    records.set(i-1, right);
                    // 2 elements have been swapped so swapped is set to true.
                    swapped = true;
                }
            }
        }
    }
}
