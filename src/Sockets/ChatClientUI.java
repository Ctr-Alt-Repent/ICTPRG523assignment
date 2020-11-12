package Sockets;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

/**
 * The ChatClientUI class is a test class to learn to use sockets to send messages.
 * ChatClientUI will keep an ArrayList of messages it receives and display it on screen.
 */
public class ChatClientUI {
    JFrame window;
    Client client;
    JList chatHistory;
    ArrayList<String> chatHistoryData;

    public ChatClientUI()
    {
        window = new JFrame("Chat Client");
        window.setMinimumSize(new Dimension(400, 400));
        window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        Container content = window.getContentPane();
        content.setLayout(new GridBagLayout());

        chatHistoryData = new ArrayList<>();

        createUI();

        window.pack();
        window.setVisible(true);
    }

    private void createUI()
    {
        JTextField serverAddress = new JTextField();
        addComponent(serverAddress, 0, 0, 2, 1, 1f, 0f);

        JButton connect = new JButton("Connect");
        addComponent(connect, 0, 1, 2, 1,1f, 0f);
        connect.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.out.println("Connect Pressed with: " + serverAddress.getText());

                client = new Client(serverAddress.getText(), new MessageListener() {
                    @Override
                    public void message(String msg, MessageSender sender) {
                        // Add the message to the chat history.
                        chatHistoryData.add(msg);
                        chatHistory.setListData(chatHistoryData.toArray());
                    }
                });
            }
        });

        chatHistory = new JList();
        addComponent(chatHistory, 0, 2, 2, 1, 1f, 1f);

        JTextField chatBox = new JTextField();
        addComponent(chatBox, 0, 3, 1, 1,1f, 0f);

        JButton send = new JButton("Send");
        addComponent(send, 1, 3, 1, 1, 0f, 0f);
        send.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.out.println("Send message: " + chatBox.getText());
                if (client != null)
                {
                    client.sendMessage(chatBox.getText());
                }
                chatBox.setText("");
            }
        });
    }

    private <C extends Component> C addComponent(C component, int gridX, int gridY, int gridWidth, int gridHeight, float weightX, float weightY)
    {
        GridBagConstraints constraints = new GridBagConstraints();
        constraints.fill = GridBagConstraints.BOTH;
        constraints.gridx = gridX;
        constraints.gridy = gridY;
        constraints.gridwidth = gridWidth;
        constraints.gridheight = gridHeight;
        constraints.weightx = weightX;
        constraints.weighty = weightY;

        window.getContentPane().add(component, constraints);

        return component;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                ChatClientUI client = new ChatClientUI();
            }
        });
    }
}
