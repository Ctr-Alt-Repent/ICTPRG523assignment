package CDArchiveProject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.ArrayList;

/**
 * The RecordStorage class contains 2 methods used to read and write the CDRecords to a file.
 * saveRecordList(String, ArrayList) will use a BufferedWriter to write the data in the ArrayList to a file in a specified format.
 * loadRecordList(String) will use a BufferedReader to read data from a file into an ArrayList.
 */
public class RecordStorage {

    /**
     * Function to load data into an ArrayList from a file in the source folder designated by the filepath.
     * @param filepath is the name of the file to be read from.
     */
    public static ArrayList<CDRecord> loadRecordList(String filepath)
    {

        ArrayList<CDRecord> records = new ArrayList<>();

        try
        {
            FileReader fr = new FileReader(filepath);
            BufferedReader br = new BufferedReader(fr);

            String line;
            while ((line = br.readLine()) != null)
            {
                String[] dataColumns = line.split(";");
                CDRecord record = new CDRecord(
                        dataColumns[1],
                        dataColumns[2],
                        dataColumns[3],
                        Integer.parseInt(dataColumns[4]),
                        Integer.parseInt(dataColumns[5]),
                        Integer.parseInt(dataColumns[6]),
                        dataColumns[7],
                        dataColumns[8].equalsIgnoreCase("yes")
                );
                records.add(record);
            }
        }
        catch(Exception e)
        {
            System.err.println("Failed to load records: " + e.toString());
        }

        return records;
    }

    /**
     * Function to write data from an ArrayList into a file in the source folder designated by the filepath.
     * @param filepath is the name of the file to be read from.
     * @param records is the ArrayList that data will be retrieved from and built into Strings before being written.
     */
    public static void saveRecordList(String filepath, ArrayList<CDRecord> records)
    {
        try
        {
            int ID = 0;
            FileWriter fw = new FileWriter(filepath);
            BufferedWriter bw = new BufferedWriter(fw);
            for (CDRecord record : records)
            {
                String output = ID + ";"
                              + record.getTitle() + ";"
                              + record.getAuthor() + ";"
                              + record.getSection() + ";"
                              + record.getX() + ";"
                              + record.getY() + ";"
                              + record.getBarcode() + ";"
                              + record.getDescription() + ";";
                if (record.isOnLoan()) { output += "yes"; } else { output += "no"; }
                bw.write(output);
                bw.newLine();
                ID++;
            }
            bw.flush();
            bw.close();
        }
        catch(Exception e)
        {
            System.err.println("Failed to save records: " + e.toString());
        }

    }
}
