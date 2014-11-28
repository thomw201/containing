package org.nhl.containing.vehicles;

import com.jme3.asset.AssetManager;
import com.jme3.scene.Spatial;
import org.nhl.containing.Container;

/**
 *
 * @author Jeroen
 */
public class Agv extends Vehicle {

    private AssetManager assetManager;
    public Container container;
    
    public Agv(AssetManager assetManager) {
        this.assetManager = assetManager;
        initAgv();
    }

    /**
     * Initialize an Agv.
     */
    private void initAgv() {

        // Load a model.
        Spatial agv = assetManager.loadModel("Models/medium/agv/agv.j3o");
        this.attachChild(agv);
    }
}
