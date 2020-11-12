package Sorting;

import CDArchiveProject.CDRecord;

import java.util.ArrayList;

/**
 * Selection.sort(ArrayList) uses the selection sorting algorithm to arrange the array
 * by an element's Barcode variable in ascending order.
 */
public class Selection {

    public static void main(String[] args) {
        ArrayList<CDRecord> records = new ArrayList<>();

        for (int i = 0; i < 20; i++)
        {
            int randomBarcode = (int)(Math.random()*100);
            records.add(new CDRecord(randomBarcode));
        }

        System.out.println("Before sort: " + records.toString());
        Selection.sort(records);
        System.out.println("After sort:  " + records.toString());
    }

    /**
     * Use a selection sorting algorithm to sort an ArrayList.
     * @param records is the data structure that will be sorted.
     */
    public static void sort(ArrayList<CDRecord> records)
    {
        for (int index = 0; index < records.size()-1; index++)
        {
            int smallestIndex = index;

            // Step through the List to find the index of the smallest barcode of the records.
            for (int currentIndex = index+1; currentIndex < records.size(); currentIndex++)
            {
                // Whenever a smaller barcode is found set the smallestIndex to the index of that record.
                if (records.get(currentIndex).getBarcode() < records.get(smallestIndex).getBarcode())
                {
                    smallestIndex = currentIndex;
                }
            }
            // Get the smallest record and the record at the current index.
            CDRecord smallestRecord = records.get(smallestIndex);
            CDRecord indexRecord = records.get(index);
            // Swap the records. The smallestRecord is set to the current index and the indexRecord is set to the smallest index.
            records.set(index, smallestRecord);
            records.set(smallestIndex, indexRecord);
        }
    }
}
