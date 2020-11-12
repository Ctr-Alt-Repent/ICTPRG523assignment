package CDArchiveProject;

import javax.swing.table.AbstractTableModel;
import  java.util.ArrayList;

/**
 * The CDRecordTableModel class provides a tableModel format that allows CDRecord data to be stored in a table.
 */
public class CDRecordTableModel extends AbstractTableModel{

    final private static String[] columnNames = new String[] {"Title", "Author", "Section", "X", "Y", "Barcode", "Description", "On-Loan"};
    ArrayList<CDRecord> records;

    public CDRecordTableModel(ArrayList<CDRecord> records)
    {
        this.records = records;
    }

    @Override
    public String getColumnName(int column) {
        return columnNames[column];
    }

    @Override
    public int getRowCount() {
        return records.size();
    }

    @Override
    public int getColumnCount() {
        return columnNames.length;
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        CDRecord record = this.records.get(rowIndex);

        return switch (columnIndex) {
            case 0 -> record.getTitle();
            case 1 -> record.getAuthor();
            case 2 -> record.getSection();
            case 3 -> record.getX();
            case 4 -> record.getY();
            case 5 -> record.getBarcode();
            case 6 -> record.getDescription();
            case 7 -> record.isOnLoan();
            default -> null;
        };

    }
}
