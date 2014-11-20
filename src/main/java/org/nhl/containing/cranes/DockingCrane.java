package org.nhl.containing.cranes;

import com.jme3.asset.AssetManager;
import com.jme3.scene.Spatial;

/**
 *
 * @author Jeroen
 */
public class DockingCrane extends Crane {

    private AssetManager assetManager;

    public DockingCrane(AssetManager assetManager) {
        this.assetManager = assetManager;
        initDockingCrane();
    }

    /**
     * Initialize a docking crane.
     */
    public void initDockingCrane() {

        // Load a model.
        Spatial moveableCrane = assetManager.loadModel("Models/high/crane/dockingcrane/crane.j3o");
        this.attachChild(moveableCrane);
    }
}
