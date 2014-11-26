package org.nhl.containing;

import com.jme3.asset.AssetManager;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;

/**
 *
 * @author Jeroen
 */
public class Container extends Node {

    private AssetManager assetManager;
    private String owner;
    private String containerID;
    private String transportType;
    private Vector3f Location;

    public Container(AssetManager assetManager, String owner, String containerID, String transportType, Vector3f Location) {
        this.assetManager = assetManager;
        this.owner = owner;
        this.containerID = containerID;
        this.transportType = transportType;
        this.Location = Location;
        initContainer();
    }

    /**
     * Initialize a container.
     */
    public void initContainer() {

        // Load a model.
        Spatial container = assetManager.loadModel("Models/medium/container/container.j3o");
        Material mat = new Material(assetManager,
          "Common/MatDefs/Misc/Unshaded.j3md");  // create a simple material
        mat.setColor("Color", ColorRGBA.White);   // set color of material to blue
        container.setMaterial(mat);
        this.attachChild(container);
    }

    public Vector3f getLocation() {
        return Location;
    }

    public String getContainerID() {
        return containerID;
    }

    public String getOwner() {
        return owner;
    }

    public String getTransportType() {
        return transportType;
    }

    public void setLocation(Vector3f Location) {
        this.Location = Location;
    }
}