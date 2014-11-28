package org.nhl.containing.cranes;

import com.jme3.asset.AssetManager;
import com.jme3.scene.Spatial;

/**
 * @author Jeroen
 */
public class TruckCrane extends Crane {

    private AssetManager assetManager;

    public TruckCrane(AssetManager assetManager) {
        this.assetManager = assetManager;
        initTruckCrane();
    }

    /**
     * Initialize a truck crane.
     */
    public void initTruckCrane() {

        // Load a model.
        Spatial truckCrane = assetManager.loadModel("Models/high/crane/truckcrane/crane.j3o");
        this.attachChild(truckCrane);
    }
}
