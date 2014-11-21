package org.nhl.containing.vehicles;

import com.jme3.scene.Node;

/**
 *
 * @author Jeroen
 */
public class Vehicle extends Node{
    protected int id;

    public void setId(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }
}
