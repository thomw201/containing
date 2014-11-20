package org.nhl.containing.cranes;

import com.jme3.asset.AssetManager;
import com.jme3.scene.Spatial;

/**
 *
 * @author Jeroen
 */
public class StorageCrane extends Crane {

    private AssetManager assetManager;

    public StorageCrane(AssetManager assetManager) {
        this.assetManager = assetManager;
        initStorageCrane();
    }

    /**
     * Initialize a storage crane.
     */
    public void initStorageCrane() {

        // Load a model.
        Spatial storageCrane = assetManager.loadModel("Models/high/crane/storagecrane/crane.j3o");
        this.attachChild(storageCrane);
    }
}
