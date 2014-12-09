package org.nhl.containing.communication;


import java.lang.String;

/**
 *  Data container for messages
 *
 */
public abstract class Message {
    private int id;
    
    public Message(int id){
        this.id = id;
    }
    
    public int getId() {
        return id;
    }
}
