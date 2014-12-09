/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.nhl.containing.communication;

/**
 * Data class that holds information pertaining the arrival of a transporter.
 */
public class ArriveMessage extends Message {

    // TODO
    public ArriveMessage(int id) {
        super(id, Message.ARRIVE);
    }
}
