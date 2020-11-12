package CDArchiveProject;

//*******************************
//* Unit: ICTPRG523             *
//* Project: CD Archive Project *
//* Name: Joel Gallaher         *
//******************************/

import Sockets.Client;
import javax.swing.*;
import java.awt.*;
import java.time.format.DateTimeFormatter;
import java.time.LocalDateTime;
import org.apache.commons.lang3.StringUtils;

/**
 * The AutomationConsole class is meant to control a robot to manipulate a physical collection of CDs.
 * Current development only receives commands and returns a successful message once the command has been followed.
 */
public class AutomationConsole {

    JFrame window;
    Client client;
    String process;
    String lastProcess = "";
    String[] messageComponents;
    JTextField itemBarcodeText;
    JTextField currentActionText;
    JTextField sectionText;
    boolean isCD;

    public static void main(String[] args) {
        SwingUtilities.invokeLater(AutomationConsole::new);
    }

    public AutomationConsole()
    {

        client = new Client("localhost", (msg, sender) -> {
            process = msg;
            if (!process.equals(lastProcess)) {
                messageComponents = msg.split(" - ");
                switch (messageComponents[3]) {
                    case "Retrieve Item" -> currentActionText.setText("Retrieve");
                    case "Remove Item" -> currentActionText.setText("Remove");
                    case "Return Item" -> currentActionText.setText("Return");
                    case "Add Item" -> currentActionText.setText("Add");
                    case "Random Sort", "Mostly Sort", "Reverse Sort" -> currentActionText.setText("Sort");
                    default -> currentActionText.setText("Undefined");
                }
                if (StringUtils.isAlpha(messageComponents[4])) {
                    sectionText.setText(messageComponents[4]);
                    itemBarcodeText.setText("");
                    isCD = false;
                } else {
                    itemBarcodeText.setText(messageComponents[4]);
                    sectionText.setText("");
                    isCD = true;
                }
            }
            else
            {
                process = null;
                lastProcess = null;
            }
        });

        window = new JFrame("Automation Console");
        window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        window.getContentPane().setLayout(new GridBagLayout());

        createUI();
        window.pack();
        window.setMinimumSize(new Dimension(400, 150));
        window.setSize(new Dimension(400, 150));
        window.setVisible(true);
    }

    /**
     * Function to configure components for the UI.
     */
    private void createUI()
    {
        JLabel currentActionLabel = new JLabel("Current Requested Action");
        addComponent(window, currentActionLabel, GridBagConstraints.VERTICAL, 0,0,1,1,0.0f,0.0f, new Insets(2,2,2,2), GridBagConstraints.WEST);

        currentActionText = new JTextField(10);
        addComponent(window, currentActionText, GridBagConstraints.VERTICAL, 1,0,1,1,0.0f,0.0f, new Insets(2,2,2,2), GridBagConstraints.WEST);


        JButton processButton = new JButton("Process");
        addComponent(window, processButton, GridBagConstraints.BOTH, 2,0,2,1,0.0f,0.0f, new Insets(2,2,2,2), GridBagConstraints.WEST);

        /*
         * This ActionListener will send a confirmation message back to the server signaling successful processing of the received command.
         * The format used for the message String is : time - date - RCVD - command completed - Item actioned.
         */
        processButton.addActionListener(e -> {
            if (client != null && process != null)
            {
                DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd/MM/yyyy - HH:mm:ss");
                LocalDateTime now = LocalDateTime.now();
                process = dtf.format(now) + " - " + "RCVD";
                switch (currentActionText.getText())
                {
                    case "Retrieve" -> process += " - Item Retrieved - ";
                    case "Remove" ->   process += " - Item Removed - ";
                    case "Return" ->   process += " - Item Returned - ";
                    case "Add" ->      process += " - Item Added - ";
                    case "Sort" ->     process += " - Section Sorted - ";
                    default ->         process +=  "- Undefined - ";
                }
                if (isCD)
                {
                    process += itemBarcodeText.getText();
                }
                else
                {
                    process += sectionText.getText();
                }
                lastProcess = process;
                client.sendMessage(process);
                clearConsole();
            }
        });

        JLabel itemBarcodeLabel = new JLabel("Barcode of Selected Item");
        addComponent(window, itemBarcodeLabel, GridBagConstraints.VERTICAL, 0,1,1,1,0.0f,0.0f, new Insets(2,2,2,2), GridBagConstraints.WEST);

        itemBarcodeText = new JTextField(10);
        addComponent(window, itemBarcodeText, GridBagConstraints.VERTICAL, 1,1,1,1,0.0f,0.0f, new Insets(2,2,2,2), GridBagConstraints.WEST);

        JLabel sectionLabel = new JLabel("Section");
        addComponent(window, sectionLabel, GridBagConstraints.VERTICAL, 0,2,1,1,0.0f,0.0f, new Insets(2,2,2,2), GridBagConstraints.WEST);

        sectionText = new JTextField(2);
        addComponent(window, sectionText, GridBagConstraints.VERTICAL, 1,2,1,1,0.0f,0.0f, new Insets(2,2,2,2), GridBagConstraints.WEST);

        // This button currently has no function.
        JButton addItemButton = new JButton("Add Item");
        addComponent(window, addItemButton, GridBagConstraints.BOTH, 2,1,1,1,0.0f,0.0f, new Insets(2,2,2,2), GridBagConstraints.WEST);

        JButton exitButton = new JButton("Exit");
        addComponent(window, exitButton, GridBagConstraints.VERTICAL, 2, 2,1,1,0.0f,0.0f, new Insets(2,2,2,2), GridBagConstraints.EAST);

        /*
         * This ActionListener will disconnect the client from the Server before closing the ArchiveConsole program.
         */
        exitButton.addActionListener(e -> {
            client.disconnect();
            System.exit(0);
        });
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
     * Function to clear the UI of data from processed commands to be ready for the next received command.
     */
    private void clearConsole()
    {
        currentActionText.setText("");
        itemBarcodeText.setText("");
        sectionText.setText("");
    }
}