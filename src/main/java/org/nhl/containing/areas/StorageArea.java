package org.nhl.containing.areas;

import com.jme3.asset.AssetManager;
import com.jme3.scene.Spatial;
import org.nhl.containing.cranes.StorageCrane;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Jeroen
 */
public class StorageArea extends Area {

    public List<StorageCrane> storageCranes = new ArrayList();
    private AssetManager assetManager;
    private int craneXAxis = 0;
    private float craneRailsXAxis = 2.5f;
    private int craneRailsZAxis = 0;
    private int cranes;

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
            storageCranes.add(new StorageCrane(assetManager));
            storageCranes.get(i).setLocalTranslation(craneXAxis, 0, 0);
            this.attachChild(storageCranes.get(i));
            craneXAxis += 35;
        }

        // Add crane rails.
        Spatial craneRails = assetManager.loadModel("Models/rails/stripRails.j3o");
        for (int i = 0; i < cranes; i++) {
            for (int j = 0; j < 23; j++) {
                Spatial nextRail = craneRails.clone();
                nextRail.setLocalTranslation(craneRailsXAxis, 0, craneRailsZAxis);
                this.attachChild(nextRail);
                craneRailsZAxis += 11;
            }
            craneRailsZAxis = 0;
            craneRailsXAxis += 35;
        }

    }
}