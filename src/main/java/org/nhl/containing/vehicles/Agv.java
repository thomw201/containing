package org.nhl.containing.vehicles;

import com.jme3.asset.AssetManager;
import com.jme3.scene.Spatial;

/**
 * @author Jeroen
 */
public class Agv extends Vehicle {

    private AssetManager assetManager;

    public Agv(AssetManager assetManager) {
        this.assetManager = assetManager;
        initAgv();
    }

    /**
     * Initialize an Agv.
     */
    public void initAgv() {

        // Load a model.
        Spatial agv = assetManager.loadModel("Models/medium/agv/agv.j3o");
        this.attachChild(agv);
    }
}
