package org.nhl.containing.communication;


import java.lang.String;

/**
 *  Data container for messages
 *
 */
public abstract class Message {

    public static final int CREATE = 1;
    public static final int ARRIVE = 2;

    private int id;
    private final int messageType;
    
    public Message(int id, int messageType){
        this.id = id;
        this.messageType = messageType;
    }
    
    public int getId() {
        return id;
    }
    
    public int getMessageType() {
        return messageType;
    }
}
