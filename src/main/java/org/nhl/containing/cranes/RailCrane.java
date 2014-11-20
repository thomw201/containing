package org.nhl.containing.cranes;

import com.jme3.asset.AssetManager;
import com.jme3.scene.Spatial;

/**
 *
 * @author Jeroen
 */
public class RailCrane extends Crane {

    private AssetManager assetManager;

    public RailCrane(AssetManager assetManager) {
        this.assetManager = assetManager;
        initRailCrane();
    }

    /**
     * Initialize a rail crane.
     */
    public void initRailCrane() {

        // Load a model and give it a material.
        Spatial railCrane = assetManager.loadModel("Models/medium/crane/storagecrane.j3o");
        this.attachChild(railCrane);
    }
}
