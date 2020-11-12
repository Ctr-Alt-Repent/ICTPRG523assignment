package Sockets;

/**
 * The MessageListener interface contains 1 function that must be overridden.
 * message(String, MessageSender) must be overridden by the implementing program
 * to process the message received in the String msg parameter.
 */
public interface MessageListener {
    void message(String msg, MessageSender sender);
}
