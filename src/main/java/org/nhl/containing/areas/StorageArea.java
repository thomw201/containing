package org.nhl.containing.areas;

import com.jme3.asset.AssetManager;
import com.jme3.math.Vector3f;
import com.jme3.scene.Spatial;
import org.nhl.containing.cranes.StorageCrane;

import java.util.ArrayList;
import java.util.List;
import org.nhl.containing.Container;

public class StorageArea extends Area {

    private List<StorageCrane> storageCranes = new ArrayList();
    private List<Container> containers = new ArrayList();
    private List<List<Vector3f>> storageLanes = new ArrayList();
    private AssetManager assetManager;
    private int craneXAxis = 0;
    private float craneRailsXAxis = 2.5f;
    private int craneRailsZAxis = 0;
    private int cranes;
    private float loc = -8;
    private float x;
    private float y = 0;
    private float z = 20;
    private int n;
    private int containerCounter = 0;

    public StorageArea(AssetManager assetManager, int cranes) {
        this.assetManager = assetManager;
        this.cranes = cranes;
        initStorageArea();
    }

    /**
     * Initialize a storage area.
     */
    private void initStorageArea() {

        // Add storage cranes to the list and scene.
        for (int i = 0; i < cranes; i++) {
            storageCranes.add(new StorageCrane(assetManager, this));
            storageCranes.get(i).setLocalTranslation(craneXAxis, 0, 0);
            storageCranes.get(i).setId(i);
            this.attachChild(storageCranes.get(i));
            craneXAxis += 50;
        }

        // Add crane rails.
        Spatial craneRails = assetManager.loadModel("Models/rails/stripRails.j3o");
        for (int i = 0; i < cranes; i++) {
            for (int j = 0; j < 22; j++) {
                Spatial nextRail = craneRails.clone();
                nextRail.setLocalTranslation(craneRailsXAxis, 0, craneRailsZAxis);
                this.attachChild(nextRail);
                craneRailsZAxis += 11;
            }
            craneRailsZAxis = 0;
            craneRailsXAxis += 50;
        }
        
        // Create for every crane a list with vectors for containers.
        for (int i = 0; i < cranes; i++) {
            n = i;
            List<Vector3f> containerSpots = new ArrayList();
            x = getStorageCranes().get(i).getLocalTranslation().x - 2.5f + loc;
            for (int j = 0; j < 540; j++) {
                containerSpots.add(getNextSpot());
            }
            storageLanes.add(containerSpots);
            y = 0;
            z = 20;
        }

    }

    // Get next location for the containers.
    private Vector3f getNextSpot() {
        x += 2.5f;
        if (x >= getStorageCranes().get(n).getLocalTranslation().x + 15f + loc) { 
            x = getStorageCranes().get(n).getLocalTranslation().x + loc;
            z += 13.5f;
        }
        if (z >= 222.5f) {
            x = getStorageCranes().get(n).getLocalTranslation().x + loc;
            z = 20;
            y += 2.9f;
        }
        
        containerCounter++;
        return new Vector3f(x, y, z);
    }

    public List<StorageCrane> getStorageCranes() {
        return storageCranes;
    }
    
    public List<List<Vector3f>> getStorageLanes(){
        return storageLanes;
    }

    public void addContainer(Container container) {
        containers.add(container);
    }

    public void removeContainer(Container container) {
        containers.remove(container);
    }
}
