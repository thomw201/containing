package org.nhl.containing.communication;


import java.lang.String;

/**
 *  Data container for messages
 *
 */
public abstract class Message {
    private int id;
    
    public static final int CREATE_MESSAGE = 1;
    public static final int ARRIVE_MESSAGE = 2;
    
    public Message(int id){
        this.id = id;
    }
    
    public int getId() {
        return id;
    }
    
    public abstract int getMessageType();
}
