package Sockets;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

/**
 * The Server class is a runnable class with no GUI.
 * Server will continuously check for messages from its clients.
 * Server will send any messages it receives back to all clients.
 */
public class Server implements MessageSender
{
    public static void main(String[] args) {
        new Server(20000, (msg, sender) -> sender.sendMessage(msg));
    }

    ServerSocket connectionListener;
    MessageListener messageListener;
    ArrayList<ServerClient> clients;

    public Server(int port, MessageListener messageListener)
    {
        this.messageListener = messageListener;
        MessageSender sender = this;
        clients = new ArrayList<>();

        try
        {
            connectionListener = new ServerSocket(port);
            System.out.println("Started Listening...");

            boolean running = true;
            while (running)
            {
                ServerClient client = new ServerClient(connectionListener.accept());
                clients.add(client);
                System.out.println("Client connected: " + client.connection.getInetAddress().getHostAddress());

                (new Thread(() -> {
                    System.out.println("Starting client message listener...");
                    while(client.connection.isConnected())
                    {
                        try
                        {
                            messageListener.message(client.inputStream.readLine(), sender);
                        }
                        catch (Exception e)
                        {
                            System.err.println("Failed to read message: " + e);
                            break;
                        }
                    }
                })).start();
            }
        }
        catch (Exception e)
        {
            System.err.println("Failed to listen for connections: " + e);
        }
    }

    /**
     * sendMessage(String) is overridden from the implemented MessageSender interface.
     * @param msg is the message to be sent to each client connected to the server.
     */
    @Override
    public void sendMessage(String msg)
    {
        for (ServerClient client : clients)
        {
            if (client.connection.isClosed())
            {
                clients.remove(client);
            }
            else
            {
                client.outputStream.println(msg);
            }
        }
    }

    static class ServerClient
    {
        Socket connection;
        PrintWriter outputStream;
        BufferedReader inputStream;

        ServerClient(Socket connection)
        {
            this.connection = connection;
            try
            {
                outputStream = new PrintWriter(connection.getOutputStream(), true);
                inputStream = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            }
            catch (Exception e)
            {
                System.err.println("failed to create client: " + e);
            }
        }
    }
}
