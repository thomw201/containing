/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.nhl.containing.communication;

/**
 *
 */
public class SpeedMessage extends Message {
    private float speed;

    public SpeedMessage(int id, float speed) {
        super(id, Message.SPEED);
        this.speed = speed;
    }

    public float getSpeed() {
        return speed;
    }
}
