package Sorting;

import CDArchiveProject.CDRecord;

import java.util.ArrayList;

/**
 * Insertion.sort(ArrayList) uses the insertion sorting algorithm to arrange the array
 * by an element's Author variable in ascending order.
 */
public class Insertion {

    public static void main(String[] args) {
        ArrayList<CDRecord> records = new ArrayList<>();

        for (int i = 0; i < 20; i++)
        {
            int randomBarcode = (int)(Math.random()*100);
            records.add(new CDRecord(randomBarcode));
        }

        System.out.println("Before sort: " + records.toString());
        Insertion.sort(records);
        System.out.println("After sort:  " + records.toString());
    }

    /**
     * Use an insertion sorting algorithm to sort an ArrayList.
     * @param records is the data structure that will be sorted.
     */
    public static void sort(ArrayList<CDRecord> records)
    {
        for (int index = 1; index < records.size(); index++)
        {
            CDRecord focusRecord = records.get(index);
            int previousIndex = index -1;

            // As long as the prevIndex is within the bounds of the List and the focusRecord's barcode property is less the the previousIndex's.
            while (previousIndex >=0 && focusRecord.getAuthor().compareTo(records.get(previousIndex).getAuthor()) < 0)
            {
                // Move the previousIndex's record up 1 place and decrement previousRecord for the next loop check.
                records.set(previousIndex+1, records.get(previousIndex));
                previousIndex--;
            }

            // Set the focusRecord next to the previousIndex that its barcode is not less than.
            records.set(previousIndex+1, focusRecord);
        }
    }
}
