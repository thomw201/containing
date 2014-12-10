/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.nhl.containing.communication;

/**
 * Data class that holds information pertaining the arrival of a transporter.
 */
public class ArriveMessage extends Message {

    private int transporterId;
    private int depotIndex;

    // TODO
    public ArriveMessage(int id, int transporterId, int depotIndex) {
        super(id, Message.ARRIVE);
        this.transporterId = transporterId;
        this.depotIndex = depotIndex;
    }

    public int getTransporterId() {
        return transporterId;
    }

    public int getDepotIndex() {
        return depotIndex;
    }
}
