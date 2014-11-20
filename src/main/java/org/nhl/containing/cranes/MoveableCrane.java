package org.nhl.containing.cranes;

import com.jme3.asset.AssetManager;
import com.jme3.scene.Spatial;

/**
 *
 * @author Jeroen
 */
public class MoveableCrane extends Crane {

    private AssetManager assetManager;

    public MoveableCrane(AssetManager assetManager) {
        this.assetManager = assetManager;
        initMoveableCrane();
    }

    /**
     * Initialize a moveable crane.
     */
    public void initMoveableCrane() {

        // Load a model.
        Spatial moveableCrane = assetManager.loadModel("Models/medium/crane/dockingcrane.j3o");
        this.attachChild(moveableCrane);
    }
}
