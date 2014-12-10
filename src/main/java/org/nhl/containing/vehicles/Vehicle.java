package org.nhl.containing.vehicles;

import com.jme3.scene.Node;

public class Vehicle extends Node {
    private int id;

    public Vehicle(int id) {
        super();
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}
