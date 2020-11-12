package Sockets;

/**
 * The MessageSender interface contains 1 function that must be overridden.
 * sendMessage(String) must be overridden by the implementing program
 * to send the message in the String msg parameter.
 */
public interface MessageSender {
    void sendMessage(String msg);
}
