package CDArchiveProject;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * The ErrorWindow class is a simple popup window that can be called by the main application.
 * ErrorWindow will display a given String alerting the user of incorrect use of the program.
 */
public class ErrorWindow {

    JFrame window;
    JLabel errorMessage;
    JButton closeButton;

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                ErrorWindow errorWindow = new ErrorWindow("This is not an error. This is a test.", 500, 500);
            }
        });
    }

    public ErrorWindow(String errorMessage)
    {
        window = new JFrame("Error");
        //window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        window.getContentPane().setLayout(new GridBagLayout());

        createUI(errorMessage);
        window.pack();
        window.setMinimumSize(new Dimension(350, 100));
        window.setSize(new Dimension(350, 100));
        window.setLocation(MouseInfo.getPointerInfo().getLocation().x-150,MouseInfo.getPointerInfo().getLocation().y-70);
        window.setVisible(true);
    }

    public ErrorWindow(String errorMessage, int width, int height)
    {
        window = new JFrame("Error");
        //window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        window.getContentPane().setLayout(new GridBagLayout());

        createUI(errorMessage);
        window.pack();
        window.setMinimumSize(new Dimension(350, 100));
        window.setSize(new Dimension(width, height));
        window.setLocation(MouseInfo.getPointerInfo().getLocation().x-150,MouseInfo.getPointerInfo().getLocation().y-70);
        window.setVisible(true);
    }

    /**
     * Function to configure components for the UI.
     */
    private void createUI(String errMsg)
    {
        errorMessage = new JLabel(errMsg);
        addComponent(window, errorMessage, GridBagConstraints.BOTH, 0,0,1,1,0.0f,1.0f, new Insets(2,2,2,2), GridBagConstraints.CENTER);

        closeButton = new JButton("Close");
        addComponent(window, closeButton, GridBagConstraints.VERTICAL, 0,1,1,1,0.0f,0.0f, new Insets(2,2,10,2), GridBagConstraints.CENTER);
        closeButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                window.dispose();
            }
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
        constraints.insets = new Insets(2,2,2,2);
        constraints.anchor = anchor;

        ContentPane.add(component, constraints);
    }
}
