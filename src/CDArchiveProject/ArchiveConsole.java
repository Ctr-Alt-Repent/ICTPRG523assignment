package CDArchiveProject;

//*******************************
//* Unit: ICTPRG523             *
//* Project: CD Archive Project *
//* Name: Joel Gallaher         *
//*******************************

import Sockets.Client;
import Sorting.*;
import Trees.BinaryTree;
import Lists.*;
import org.apache.commons.lang3.StringUtils;

import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.FileWriter;
import java.io.BufferedWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.regex.*;
import java.time.format.DateTimeFormatter;
import java.time.LocalDateTime;

public class ArchiveConsole {

    JFrame window;
    ArrayList<CDRecord> records;
    LinkedList processLog;
    JTable cdRecordTable;
    CDRecordTableModel tableModel;
    CDRecord focusRecord;
    CDRecord recordPanelRecord;
    JTextField titleText;
    JTextField authorText;
    JTextField sectionText;
    JTextField xText;
    JTextField yText;
    JTextField barcodeText;
    JTextArea descriptionText;
    JTextArea processLogsTextArea;
    Client client;
    boolean newRecord = true;
    Integer row;
    HashMap<Integer, CDRecord> recordHashMap = new HashMap<>();

    public static void main(String[] args) {
        SwingUtilities.invokeLater(ArchiveConsole::new);
    }

    public ArchiveConsole()
    {
        records = RecordStorage.loadRecordList("records.data");
        processLog = new LinkedList();

        /*
         * The Client connects to the Server located at the "localhost" address.
         * Received messages are appended to the LinkedList and processLogsTextArea.
         * Received messages are split and analysed to update the CDRecord that was actioned if necessary.
         */
        client = new Client("localhost", (msg, sender) -> {
            processLog.Append(new LinkedList.Node(msg));
            processLogsTextArea.append(msg);
            processLogsTextArea.append("\n");

            String[] messageComponents = msg.split(" - ");
            switch (messageComponents[3])
            {
                // In this case the successfully retrieved CD will have its CDRecord's onLoan boolean set to true.
                case "Item Retrieved" -> {
                    for (CDRecord r : records)
                    {
                        if (r.getBarcode() == Integer.parseInt(messageComponents[4]))
                        {
                            r.onLoan = true;
                            RecordStorage.saveRecordList("records.data", records);
                            tableModel.fireTableDataChanged();

                            if (recordPanelRecord.getBarcode().equals(r.getBarcode()))
                            {
                                recordPanelRecord.onLoan = true;
                            }
                        }
                    }
                }
                // In this case the successfully retrieved CD will have its CDRecord deleted from the ArrayList.
                case "Item Removed" -> {
                    records.removeIf(r -> r.getBarcode() == Integer.parseInt(messageComponents[4]));
                    RecordStorage.saveRecordList("records.data", records);
                    tableModel.fireTableDataChanged();
                }
                // In this case the successfully retrieved CD will have its CDRecord's onLoan boolean set to false.
                case "Item Returned" -> {
                    for (CDRecord r : records)
                    {
                        if (r.getBarcode() == Integer.parseInt(messageComponents[4]))
                        {
                            r.onLoan = false;
                            RecordStorage.saveRecordList("records.data", records);
                            tableModel.fireTableDataChanged();

                            if (recordPanelRecord.getBarcode().equals(r.getBarcode()))
                            {
                                recordPanelRecord.onLoan = false;
                            }
                        }
                    }
                }
                case "Item Added" -> newRecord = false;
                default -> {}
            }
        });

        // Setup window configuration and GUI elements.
        window = new JFrame("Archive Console");
        window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        window.getContentPane().setLayout(new GridBagLayout());
        createUI();
        window.pack();
        window.setMinimumSize(new Dimension(1000, 600));
        window.setSize(new Dimension(1000, 600));
        window.setVisible(true);
    }

    /**
     * Function to configure components for the UI.
     */
    private void createUI()
    {
        JLabel searchLabel = new JLabel("Search String:");
        addComponent(window.getContentPane(), searchLabel, GridBagConstraints.BOTH, 0, 0, 1, 1, 0.0f, 0.0f, new Insets(2, 2, 2, 2), GridBagConstraints.WEST);

        JTextField searchText = new JTextField();
        addComponent(window.getContentPane(), searchText, GridBagConstraints.BOTH, 1, 0, 1, 1, 100.0f, 0.0f, new Insets(2, 2, 2, 2), GridBagConstraints.WEST);

        JButton searchButton = new JButton("Search");
        addComponent(window.getContentPane(), searchButton, GridBagConstraints.BOTH, 2, 0, 1, 1, 0.0f, 0.0f, new Insets(2, 2, 2, 5), GridBagConstraints.WEST);

        /*
         * This ActionListener will take the text entered into the searchText JTextField and filter the table rows using the regexFilter(text) method.
         */
        searchButton.addActionListener(e -> {
            String text = searchText.getText();
            TableRowSorter<CDRecordTableModel> sorter = (TableRowSorter<CDRecordTableModel>) cdRecordTable.getRowSorter();
            if(text.length() == 0) {
                sorter.setRowFilter(null);
            } else {
                try {
                    sorter.setRowFilter(RowFilter.regexFilter(text));
                } catch(PatternSyntaxException pse) {
                    System.out.println("Bad regex pattern");
                }
            }
        });

        // The archiveListPanel contains the cdRecordTable to display the CDRecords and several buttons to sort the cdRecordTable in ascending order by a chosen table column.
        JPanel archiveListPanel = createArchiveListPanel();
        archiveListPanel.setBorder(BorderFactory.createLineBorder(Color.BLACK, 1));
        addComponent(window.getContentPane(), archiveListPanel, GridBagConstraints.BOTH, 0, 1, 3, 1, 70.0f, 40.0f, new Insets(2, 2, 2, 5), GridBagConstraints.CENTER);

        // The processLog panel contains a JTextArea for displaying a log of messaged sent and received and several buttons to sort the CDRecords into a binary tree or hashmap.
        JPanel processLogPanel = createProcessLogPanel();
        processLogPanel.setBorder(BorderFactory.createLineBorder(Color.BLACK, 1));
        addComponent(window.getContentPane(), processLogPanel, GridBagConstraints.BOTH, 0, 2, 3, 1, 70.0f, 40.0f, new Insets(2, 2, 2, 5), GridBagConstraints.CENTER);

        // The recordPanel contains a JTextField for each data point of a CDRecord to allow editing CDRecord data and adding new CDRecords.
        JPanel recordPanel = createRecordPanel();
        recordPanel.setBorder(BorderFactory.createLineBorder(Color.BLACK, 1));
        addComponent(window.getContentPane(), recordPanel, GridBagConstraints.BOTH, 3, 0, 1, 2, 30.0f, 40.0f, new Insets(2, 2, 2, 5), GridBagConstraints.CENTER);

        // The actionRequestPanel contains several buttons that take the data from the recordPanel and send it along with a specific command to a 'robotic arm' to process the command.
        JPanel actionRequestPanel = createActionRequestPanel();
        actionRequestPanel.setBorder(BorderFactory.createLineBorder(Color.BLACK, 1));
        addComponent(window.getContentPane(), actionRequestPanel, GridBagConstraints.BOTH, 3, 2, 1, 1, 30.0f, 40.0f, new Insets(2, 2, 2, 5), GridBagConstraints.CENTER);

        JButton exitButton = new JButton("Exit");
        addComponent(window.getContentPane(), exitButton, GridBagConstraints.VERTICAL, 3, 3, 1, 1, 0.0f, 0.0f, new Insets(2,2,2,2), GridBagConstraints.EAST);

        /*
         * This ActionListener will disconnect the client from the Server before closing the ArchiveConsole program.
         */
        exitButton.addActionListener(e -> {
            client.disconnect();
            System.exit(0);
        });

    }

    /**
     * Function to configure and add UI components to the archiveListPanel.
     */
    private JPanel createArchiveListPanel()
    {
        JPanel panel = new JPanel();
        panel.setLayout(new GridBagLayout());
        panel.setBackground(Color.lightGray);

        JLabel titleLabel = new JLabel("Archive CDs");
        addComponent(panel, titleLabel, GridBagConstraints.VERTICAL, 0, 0, 4, 1, 100.0f, 0.0f, new Insets(2,2,2,2), GridBagConstraints.CENTER);

        cdRecordTable = new JTable();
        ArrayList<CDRecord> data = records;
        tableModel = new CDRecordTableModel(data);
        cdRecordTable.setModel(tableModel);
        cdRecordTable.setFillsViewportHeight(true);
        cdRecordTable.getTableHeader().setReorderingAllowed(false);
        cdRecordTable.getTableHeader().setResizingAllowed(false);
        cdRecordTable.getColumnModel().getColumn(0).setPreferredWidth(150);
        cdRecordTable.getColumnModel().getColumn(1).setPreferredWidth(80);
        cdRecordTable.getColumnModel().getColumn(2).setPreferredWidth(50);
        cdRecordTable.getColumnModel().getColumn(3).setPreferredWidth(20);
        cdRecordTable.getColumnModel().getColumn(4).setPreferredWidth(20);
        cdRecordTable.getColumnModel().getColumn(5).setPreferredWidth(60);
        cdRecordTable.getColumnModel().getColumn(6).setPreferredWidth(150);
        cdRecordTable.getColumnModel().getColumn(7).setPreferredWidth(50);
        TableRowSorter<CDRecordTableModel> sorter = new TableRowSorter<>(tableModel);
        cdRecordTable.setRowSorter(sorter);

        /*
         * This MouseListener will store the index of the table row that was clicked on.
         * The data in the CDRecord that corresponds to that row will be accessed and sent to the recordPanel.
         */
        cdRecordTable.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                row = cdRecordTable.getSelectedRow();
                focusRecord = records.get(row);
                populateCreateRecordPanel(focusRecord);
                newRecord = false;
            }
        });

        JScrollPane cdRecordTableScrollPane = new JScrollPane(cdRecordTable);
        addComponent(panel, cdRecordTableScrollPane, GridBagConstraints.BOTH, 0, 1, 4, 1, 100.0f, 100.0f, new Insets(2,2,2,2), GridBagConstraints.CENTER);

        JLabel sortLabel = new JLabel("Sort");
        addComponent(panel, sortLabel, GridBagConstraints.BOTH, 0, 2, 1, 1, 0.0f, 0.0f, new Insets(2, 2, 2, 2), GridBagConstraints.WEST);

        JButton byTitleButton = new JButton("By Title");
        addComponent(panel, byTitleButton, GridBagConstraints.VERTICAL, 1, 2, 1, 1, 0.0f, 0.0f, new Insets(2, 2, 2, 2), GridBagConstraints.WEST);

        /*
         * This ActionListener will call the Bubble.sort method on the ArrayList to sort the CDRecords using the Bubble Sort algorithm.
         * The CDRecords sorted this way are sorted by title.
         */
        byTitleButton.addActionListener(e -> {
            Bubble.sort(records);
            tableModel.fireTableDataChanged();
        });

        JButton byAuthorButton = new JButton("By Author");
        addComponent(panel, byAuthorButton, GridBagConstraints.VERTICAL, 2, 2, 1, 1, 0.0f, 0.0f, new Insets(2, 2, 2, 2), GridBagConstraints.WEST);

        /*
         * This ActionListener will call the Insertion.sort method on the ArrayList to sort the CDRecords using the Insertion Sort algorithm.
         * The CDRecords sorted this way are sorted by author.
         */
        byAuthorButton.addActionListener(e -> {
            Insertion.sort(records);
            tableModel.fireTableDataChanged();
        });

        JButton byBarcodeButton = new JButton("By Barcode");
        addComponent(panel, byBarcodeButton, GridBagConstraints.VERTICAL, 3, 2, 1, 1, 0.0f, 0.0f, new Insets(2, 2, 2, 2), GridBagConstraints.WEST);

        /*
         * This ActionListener will call the Selection.sort method on the ArrayList to sort the CDRecords using the Selection Sort algorithm.
         * The CDRecords sorted this way are sorted by barcode.
         */
        byBarcodeButton.addActionListener(e -> {
            Selection.sort(records);
            tableModel.fireTableDataChanged();
        });

        return panel;
    }

    private JPanel createProcessLogPanel()
    {
        JPanel panel = new JPanel();
        panel.setLayout(new GridBagLayout());
        panel.setBackground(Color.lightGray);

        JLabel processLogLabel = new JLabel("Process Log");
        addComponent(panel, processLogLabel, GridBagConstraints.BOTH, 0, 0, 1, 1, 0.0f, 0.0f, new Insets(2,2,2,2), GridBagConstraints.WEST);

        JButton processLogButton = new JButton("Process Log");
        addComponent(panel, processLogButton, GridBagConstraints.VERTICAL, 5, 0, 1, 1, 0.0f, 0.0f, new Insets(2,2,2,2), GridBagConstraints.EAST);

        /*
         * This ActionListener will clear any text in the processLogsTextArea then add any data from the processLog LinkedList.
         */
        processLogButton.addActionListener(e -> {
            processLogsTextArea.setText("");
            LinkedList.Node node = processLog.head;
            while (node != null)
            {
                processLogsTextArea.append(node.getData().toString());
                processLogsTextArea.append("\n");
                node = node.getNext();
            }
        });

        processLogsTextArea = new JTextArea();
        processLogsTextArea.setEditable(false);
        JScrollPane processLogScrollPane = new JScrollPane(processLogsTextArea);
        addComponent(panel, processLogScrollPane, GridBagConstraints.BOTH, 0, 1, 6, 1, 100.0f, 100.0f, new Insets(2,2,2,2), GridBagConstraints.CENTER);

        JLabel displayLabel = new JLabel("Display Binary Tree");
        addComponent(panel, displayLabel, GridBagConstraints.BOTH, 0, 2, 1, 1, 0.0f, 0.0f, new Insets(2,2,2,2), GridBagConstraints.WEST);

        JButton preOrderButton = new JButton("Pre-Order");
        addComponent(panel, preOrderButton, GridBagConstraints.BOTH, 1, 2, 1, 1, 0.0f, 0.0f, new Insets(2,2,2,2), GridBagConstraints.WEST);

        /*
         * This ActionListener loops through all the CDRecords in the ArrayList and inserts them into a new BinaryTree.
         * The BinaryTree is then traversed to retrieve the data in a pre-order traversal.
         * The retrieved data is displayed as lines of text in the processLogsTextArea.
         */
        preOrderButton.addActionListener(e -> {
            processLogsTextArea.setText("");
            BinaryTree tree = new BinaryTree();
            for (CDRecord r : records)
            {
                tree.insert(new BinaryTree.Node(r.barcode, r));
            }
            ArrayList<BinaryTree.Node> preOrder = tree.preOrderTraverse();
            for (BinaryTree.Node n : preOrder)
            {
                processLogsTextArea.append(n.getData().toString() + "\n");
            }
        });

        JButton inOrderButton = new JButton("In-Order");
        addComponent(panel, inOrderButton, GridBagConstraints.BOTH, 2, 2, 1, 1, 0.0f, 0.0f, new Insets(2,2,2,2), GridBagConstraints.WEST);

        /*
         * This ActionListener loops through all the CDRecords in the ArrayList and inserts them into a new BinaryTree.
         * The BinaryTree is then traversed to retrieve the data in an in-order traversal.
         * The retrieved data is displayed as lines of text in the processLogsTextArea.
         */
        inOrderButton.addActionListener(e -> {
            processLogsTextArea.setText("");
            BinaryTree tree = new BinaryTree();
            for (CDRecord r : records)
            {
                tree.insert(new BinaryTree.Node(r.barcode, r));
            }
            ArrayList<BinaryTree.Node> inOrder = tree.inOrderTraverse();
            for (BinaryTree.Node n : inOrder)
            {
                processLogsTextArea.append(n.getData().toString() + "\n");
            }
        });

        JButton postOrderButton = new JButton("Post-Order");
        addComponent(panel, postOrderButton, GridBagConstraints.BOTH, 3, 2, 1, 1, 0.0f, 0.0f, new Insets(2,2,2,2), GridBagConstraints.WEST);

        /*
         * This ActionListener loops through all the CDRecords in the ArrayList and inserts them into a new BinaryTree.
         * The BinaryTree is then traversed to retrieve the data in a post-order traversal.
         * The retrieved data is displayed as lines of text in the processLogsTextArea.
         */
        postOrderButton.addActionListener(e -> {
            processLogsTextArea.setText("");
            BinaryTree tree = new BinaryTree();
            for (CDRecord r : records)
            {
                tree.insert(new BinaryTree.Node(r.barcode, r));
            }
            ArrayList<BinaryTree.Node> postOrder = tree.postOrderTraverse();
            for (BinaryTree.Node n : postOrder)
            {
                processLogsTextArea.append(n.getData().toString() + "\n");
            }
        });

        JLabel hashLabel = new JLabel("Hashmap/Set");
        addComponent(panel, hashLabel, GridBagConstraints.BOTH, 0, 3, 1, 1, 0.0f, 0.0f, new Insets(2,2,2,2), GridBagConstraints.WEST);

        JButton saveButton = new JButton("Save");
        addComponent(panel, saveButton, GridBagConstraints.BOTH, 2, 3, 1, 1, 0.0f, 0.0f, new Insets(2,2,2,2), GridBagConstraints.WEST);

        /*
         * This ActionListener takes each entry from the HashMap and writes it as a string to a txt file.
         */
        saveButton.addActionListener(e -> {
            try
            {
                FileWriter fw = new FileWriter("hashmap.txt");
                BufferedWriter bw = new BufferedWriter(fw);

                for (HashMap.Entry<Integer, CDRecord> r : recordHashMap.entrySet())
                {
                    bw.write(r.toString());
                    bw.newLine();
                }
                bw.flush();
                bw.close();
            }
            catch (Exception ex)
            {
                System.err.println("Failed to save hashmap: " + e.toString());
            }
        });

        JButton displayButton = new JButton("Display");
        addComponent(panel, displayButton, GridBagConstraints.BOTH, 1, 3, 1, 1, 0.0f, 0.0f, new Insets(2,2,2,2), GridBagConstraints.WEST);

        /*
         * This ActionListener loops through all the CDRecords in the ArrayList and puts them in a hashmap using the barcode as the hash key.
         * The entries in the HashMap are then retrieved and displayed in the ProcessLogsTextArea.
         */
        displayButton.addActionListener(e -> {
            processLogsTextArea.setText("");
            for (CDRecord r : records)
            {
                recordHashMap.put(r.barcode, r);
            }
            processLogsTextArea.append(recordHashMap.toString());
        });

        return panel;
    }

    /**
     * Function to configure and add UI components to the recordPanel.
     */
    private JPanel createRecordPanel()
    {
        JPanel panel = new JPanel();
        panel.setLayout(new GridBagLayout());
        panel.setBackground(Color.lightGray);

        JLabel titleLabel = new JLabel("Title", SwingConstants.CENTER);
        addComponent(panel, titleLabel, GridBagConstraints.BOTH, 0, 0, 1, 1, 0.0f, 0.0f, new Insets(2, 2, 2, 2), GridBagConstraints.WEST);

        titleText = new JTextField();
        addComponent(panel, titleText, GridBagConstraints.BOTH, 1, 0, 3, 1, 0.0f, 0.0f, new Insets(2, 2, 2, 2), GridBagConstraints.WEST);

        JLabel authorLabel = new JLabel("Author", SwingConstants.CENTER);
        addComponent(panel, authorLabel, GridBagConstraints.BOTH, 0, 1, 1, 1, 0.0f, 0.0f, new Insets(2, 2, 2, 2), GridBagConstraints.WEST);

        authorText = new JTextField();
        addComponent(panel, authorText, GridBagConstraints.BOTH, 1, 1, 2, 1, 0.0f, 0.0f, new Insets(2, 2, 2, 2), GridBagConstraints.WEST);

        JLabel sectionLabel = new JLabel("Section", SwingConstants.CENTER);
        addComponent(panel, sectionLabel, GridBagConstraints.BOTH, 0, 2, 1, 1, 0.0f, 0.0f, new Insets(2, 2, 2, 2), GridBagConstraints.WEST);

        sectionText = new JTextField();
        addComponent(panel, sectionText, GridBagConstraints.BOTH, 1, 2, 1, 1, 0.0f, 0.0f, new Insets(2, 2, 2, 2), GridBagConstraints.WEST);

        JLabel xLabel = new JLabel("X", SwingConstants.CENTER);
        addComponent(panel, xLabel, GridBagConstraints.BOTH, 0, 3, 1, 1, 0.0f, 0.0f, new Insets(2, 2, 2, 2), GridBagConstraints.WEST);

        xText = new JTextField();
        addComponent(panel, xText, GridBagConstraints.BOTH, 1, 3, 1, 1, 0.0f, 0.0f, new Insets(2, 2, 2, 2), GridBagConstraints.WEST);

        JLabel yLabel = new JLabel("Y", SwingConstants.CENTER);
        addComponent(panel, yLabel, GridBagConstraints.BOTH, 0, 4, 1, 1, 0.0f, 0.0f, new Insets(2, 2, 2, 2), GridBagConstraints.WEST);

        yText = new JTextField();
        addComponent(panel, yText, GridBagConstraints.BOTH, 1, 4, 1, 1, 0.0f, 0.0f, new Insets(2, 2, 2, 2), GridBagConstraints.WEST);

        JLabel barcodeLabel = new JLabel("Barcode", SwingConstants.CENTER);
        addComponent(panel, barcodeLabel, GridBagConstraints.BOTH, 0, 5, 1, 1, 0.0f, 0.0f, new Insets(2, 2, 2, 2), GridBagConstraints.WEST);

        barcodeText = new JTextField();
        addComponent(panel, barcodeText, GridBagConstraints.BOTH, 1, 5, 1, 1, 0.0f, 0.0f, new Insets(2, 2, 2, 2), GridBagConstraints.WEST);

        JLabel descriptionLabel = new JLabel("Description", SwingConstants.CENTER);
        addComponent(panel, descriptionLabel, GridBagConstraints.BOTH, 0, 6, 1, 1, 0.0f, 0.0f, new Insets(2, 2, 2, 2), GridBagConstraints.WEST);

        descriptionText = new JTextArea();
        descriptionText.setLineWrap(true);
        JScrollPane descriptionScrollPane = new JScrollPane(descriptionText);
        addComponent(panel, descriptionScrollPane, GridBagConstraints.BOTH, 1, 6, 4, 3, 0.0f, 100.0f, new Insets(2, 2, 2, 2), GridBagConstraints.WEST);


        JButton newItemButton = new JButton("New Item");
        addComponent(panel, newItemButton, GridBagConstraints.BOTH, 1, 9, 1, 1, 0.0f, 0.0f, new Insets(2,2,2,2), GridBagConstraints.WEST);

        /*
         * This ActionListener calls the clearRecordPanel() method so new CD data can be entered into the recordPanel's JTextFields.
         */
        newItemButton.addActionListener(e -> {
            clearRecordPanel();
            focusRecord = null;
            newRecord = true;
        });

        JButton saveUpdateButton = new JButton("Save/Update");
        addComponent(panel, saveUpdateButton, GridBagConstraints.BOTH, 2, 9, 1, 1, 0.0f, 0.0f, new Insets(2,2,2,2), GridBagConstraints.WEST);

        /*
         * This ActionListener will determine if a CDRecord in the ArrayList is to be updated or if a new CDRecord is to be added to the ArrayList.
         * After the ArrayList has been appended or updated all the CDRecords in the ArrayList will then be written to the "records.data" file.
         * The fireTableDataChanged() method will be called on the tableModel to reflect updates or additions to the CDRecord ArrayList.
         */
        saveUpdateButton.addActionListener(e -> {
            if (isRecordPanelNotComplete())
            {
                new ErrorWindow("Record fields cannot be empty.");
            }
            else if(titleText.getText().contains(";") ||
                    authorText.getText().contains(";") ||
                    sectionText.getText().contains(";") ||
                    !StringUtils.isNumeric(xText.getText()) ||
                    !StringUtils.isNumeric(yText.getText()) ||
                    !StringUtils.isNumeric(barcodeText.getText()) ||
                    descriptionText.getText().contains(";") )
            {
                new ErrorWindow("Invalid entry. Do not enter semicolons. Only enter whole numbers in X, Y and Barcode fields.");
            }
            else
            {
                recordPanelRecord = getNewRecordPanelRecord();
                if (newRecord)
                {
                    records.add(recordPanelRecord);
                    focusRecord = recordPanelRecord;
                }
                else
                {
                    recordPanelRecord.onLoan = focusRecord.isOnLoan();
                    focusRecord = recordPanelRecord;
                    records.set(row, focusRecord);

                }
                RecordStorage.saveRecordList("records.data", records);
                tableModel.fireTableDataChanged();
            }
        });

        return panel;
    }

    /**
     * Function to configure and add UI components to the actionRequestPanel.
     */
    private JPanel createActionRequestPanel()
    {
        JPanel panel = new JPanel();
        panel.setLayout(new GridBagLayout());
        panel.setBackground(Color.lightGray);

        JLabel actionRequestLabel = new JLabel("Automation Action Request for the Item above", SwingConstants.CENTER);
        addComponent(panel, actionRequestLabel, GridBagConstraints.BOTH, 0, 0, 3, 1, 0.0f, 0.0f, new Insets(2,2,2,2), GridBagConstraints.WEST);

        JButton retrieveButton = new JButton("Retrieve");
        addComponent(panel, retrieveButton, GridBagConstraints.BOTH, 1, 1, 2,1, 0.0f, 0.0f, new Insets(2,2,2,2), GridBagConstraints.WEST);

        /*
         * This ActionListener will send a message through a socket to the connected Server.
         * The format used for the message String is : time - date - SENT - CD process command - CD barcode.
         */
        retrieveButton.addActionListener(e -> {
            if (focusRecord != null) {
                if (recordPanelRecord.isOnLoan()) {
                    new ErrorWindow("This CD cannot be retrieved as it is already on loan.");
                } else {
                    if (client != null) {
                        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd/MM/yyyy - HH:mm:ss");
                        LocalDateTime now = LocalDateTime.now();
                        String process = dtf.format(now) + " - " + "SENT" + " - " + "Retrieve Item" + " - " + barcodeText.getText();
                        client.sendMessage(process);
                    }
                }
            }
        });

        JButton removeButton = new JButton("Remove");
        addComponent(panel, removeButton, GridBagConstraints.BOTH, 1, 2, 2,1, 0.0f, 0.0f, new Insets(2,2,2,2), GridBagConstraints.WEST);

        /*
         * This ActionListener will send a message through a socket to the connected Server.
         * The format used for the message String is : time - date - SENT - CD process command - CD barcode.
         */
        removeButton.addActionListener(e -> {
            if (focusRecord != null) {
                if (recordPanelRecord.isOnLoan()) {
                    new ErrorWindow("This CD cannot be removed as it is currently on loan.");
                } else {
                    if (client != null) {
                        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd/MM/yyyy - HH:mm:ss");
                        LocalDateTime now = LocalDateTime.now();
                        String process = dtf.format(now) + " - " + "SENT" + " - " + "Remove Item" + " - " + barcodeText.getText();
                        client.sendMessage(process);
                    }
                }
            }
        });

        JButton returnButton = new JButton("Return");
        addComponent(panel, returnButton, GridBagConstraints.BOTH, 1, 3, 2,1, 0.0f, 0.0f, new Insets(2,2,2,2), GridBagConstraints.WEST);

        /*
         * This ActionListener will send a message through a socket to the connected Server.
         * The format used for the message String is : time - date - SENT - CD process command - CD barcode.
         */
        returnButton.addActionListener(e -> {
            if (focusRecord != null) {
                if (!recordPanelRecord.isOnLoan()) {
                    new ErrorWindow("This CD cannot be returned as it has not been loaned.");
                } else {
                    if (client != null) {
                        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd/MM/yyyy - HH:mm:ss");
                        LocalDateTime now = LocalDateTime.now();
                        String process = dtf.format(now) + " - " + "SENT" + " - " + "Return Item" + " - " + barcodeText.getText();
                        client.sendMessage(process);
                    }
                }
            }
        });

        JButton addToCollectionButton = new JButton("Add to Collection");
        addComponent(panel, addToCollectionButton, GridBagConstraints.BOTH, 1, 4, 2,1, 0.0f, 0.0f, new Insets(2,2,2,2), GridBagConstraints.WEST);

        /*
         * This ActionListener will send a message through a socket to the connected Server.
         * The format used for the message String is : time - date - SENT - CD process command - CD barcode.
         */
        addToCollectionButton.addActionListener(e -> {
            if (records.contains(focusRecord) && !newRecord) {
                new ErrorWindow("This CD cannot be added as it already exists.");
            }
            else {
                if (client != null && focusRecord != null) {
                    DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd/MM/yyyy - HH:mm:ss");
                    LocalDateTime now = LocalDateTime.now();
                    String process = dtf.format(now) + " - " + "SENT" + " - " + "Add Item" + " - " + barcodeText.getText();
                    client.sendMessage(process);
                }
            }
        });

        JLabel sortSectionLabel = new JLabel("Sort Section");
        addComponent(panel, sortSectionLabel, GridBagConstraints.BOTH, 1, 5, 1,1, 0.0f, 0.0f, new Insets(2,2,2,2), GridBagConstraints.WEST);

        JTextField sortSectionText = new JTextField(3);
        addComponent(panel, sortSectionText, GridBagConstraints.VERTICAL, 2, 5, 1,1, 0.0f, 0.0f, new Insets(2,2,2,2), GridBagConstraints.WEST);

        JButton randomSortButton = new JButton("Random Collection Sort");
        addComponent(panel, randomSortButton, GridBagConstraints.BOTH, 1, 6, 2,1, 0.0f, 0.0f, new Insets(2,2,2,2), GridBagConstraints.WEST);

        /*
         * This ActionListener will send a message through a socket to the connected Server.
         * The format used for the message String is : time - date - SENT - sort type - chosen section.
         */
        randomSortButton.addActionListener(e -> {
            if (client != null && !sortSectionText.getText().equals("")) {
                if (!StringUtils.isAlpha(sortSectionText.getText()))
                {
                    new ErrorWindow("Please type a valid Section to sort.");
                }
                else {
                    DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd/MM/yyyy - HH:mm:ss");
                    LocalDateTime now = LocalDateTime.now();
                    String process = dtf.format(now) + " - " + "SENT" + " - " + "Random Sort" + " - " + sortSectionText.getText();
                    client.sendMessage(process);
                }
            }
        });

        JButton mostlySortButton = new JButton("Mostly Sorted Sort");
        addComponent(panel, mostlySortButton, GridBagConstraints.BOTH, 1, 7, 2,1, 0.0f, 0.0f, new Insets(2,2,2,2), GridBagConstraints.WEST);

        /*
         * This ActionListener will send a message through a socket to the connected Server.
         * The format used for the message String is : time - date - SENT - sort type - chosen section.
         */
        mostlySortButton.addActionListener(e -> {
            if (client != null && !sortSectionText.getText().equals(""))
            {
                if (!StringUtils.isAlpha(sortSectionText.getText()))
                {
                    new ErrorWindow("Please type a valid Section to sort.");
                }
                else {
                    DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd/MM/yyyy - HH:mm:ss");
                    LocalDateTime now = LocalDateTime.now();
                    String process = dtf.format(now) + " - " + "SENT" + " - " + "Mostly Sort" + " - " + sortSectionText.getText();
                    client.sendMessage(process);
                }
            }
        });

        JButton reverseSortButton = new JButton("Reverse Order Sort");
        addComponent(panel, reverseSortButton, GridBagConstraints.BOTH, 1, 8, 2,1, 0.0f, 0.0f, new Insets(2,2,2,2), GridBagConstraints.WEST);

        /*
         * This ActionListener will send a message through a socket to the connected Server.
         * The format used for the message String is : time - date - SENT - sort type - chosen section.
         */
        reverseSortButton.addActionListener(e -> {
            if (client != null && !sortSectionText.getText().equals(""))
            {
                if (!StringUtils.isAlpha(sortSectionText.getText()))
                {
                    new ErrorWindow("Please type a valid Section to sort.");
                }
                else {
                    DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd/MM/yyyy - HH:mm:ss");
                    LocalDateTime now = LocalDateTime.now();
                    String process = dtf.format(now) + " - " + "SENT" + " - " + "Reverse Sort" + " - " + sortSectionText.getText();
                    client.sendMessage(process);
                }
            }
        });

        return panel;
    }

    /**
     * Function to layout the components in the UI according to the GridBagLayout format.
     * @param ContentPane is the container that the component will be added to.
     * @param component is the current component that will be added to the container.
     * @param fill is the constraints value to automatically resize the component.
     * @param gridX is the GridBagLayout position of the component along the X-axis.
     * @param gridY is the GridBagLayout position of the component along the Y-axis.
     * @param gridWidth is the width of the component along the X-axis.
     * @param gridHeight is the height of the component along the Y-axis.
     * @param weightX is the weight of the component along the X-Axis.
     * @param weightY is the weight of the component along the Y-Axis.
     * @param insets is the amount of padding between the component and the edge of its display area.
     * @param anchor is the value determining where the component will be placed.
     */
    private <C extends Component> void addComponent(Container ContentPane, C component, int fill, int gridX, int gridY, int gridWidth, int gridHeight, float weightX, float weightY, Insets insets, int anchor)
    {
        GridBagConstraints constraints = new GridBagConstraints();
        constraints.fill = fill;
        constraints.gridx = gridX;
        constraints.gridy = gridY;
        constraints.gridwidth = gridWidth;
        constraints.gridheight = gridHeight;
        constraints.weightx = weightX;
        constraints.weighty = weightY;
        constraints.insets = insets;
        constraints.anchor = anchor;

        ContentPane.add(component, constraints);
    }

    /**
     * Function to get the data from the variables in the CDRecord and transfer it to the JTextFields in the recordPanel.
     * @param record is the CDRecord that holds the data to be transferred to the JTextFields.
     */
    private void populateCreateRecordPanel(CDRecord record)
    {
        recordPanelRecord = new CDRecord(record.title, record.author, record.section, record.x, record.y, record.barcode, record.description, record.onLoan);
        titleText.setText(record.getTitle());
        authorText.setText(record.getAuthor());
        sectionText.setText(record.getSection());
        xText.setText(record.getX().toString());
        yText.setText(record.getY().toString());
        barcodeText.setText(record.getBarcode().toString());
        descriptionText.setText(record.getDescription());
    }

    /**
     * Function to clear the JTextFields in the recordPanel so that data can be entered for a new CDRecord.
     */
    private  void clearRecordPanel()
    {
        titleText.setText("");
        authorText.setText("");
        sectionText.setText("");
        xText.setText("");
        yText.setText("");
        barcodeText.setText("");
        descriptionText.setText("");
        recordPanelRecord = null;
    }

    /**
     * Function to ensure CDRecords that are added or updated do not have any empty values.
     */
    private boolean isRecordPanelNotComplete()
    {
        return titleText.getText().equals("")
                || authorText.getText().equals("")
                || sectionText.getText().equals("")
                || xText.getText().equals("")
                || yText.getText().equals("")
                || barcodeText.getText().equals("")
                || descriptionText.getText().equals("");
    }

    /**
     * Function to create a new CDRecord from the data in the recordPanel's JTextFields.
     */
    private CDRecord getNewRecordPanelRecord()
    {
        return new CDRecord(titleText.getText(),
                authorText.getText(),
                sectionText.getText(),
                Integer.parseInt(xText.getText()),
                Integer.parseInt(yText.getText()),
                Integer.parseInt(barcodeText.getText()),
                descriptionText.getText(),
                false);
    }
}
