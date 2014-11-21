package org.nhl.containing.vehicles;

import com.jme3.asset.AssetManager;
import com.jme3.scene.Spatial;

/**
 *
 * @author Jeroen
 */
public class Boat extends Transporter {

    private AssetManager assetManager;

    public Boat(AssetManager assetManager) {
        this.assetManager = assetManager;
        initBoat();
    }

    /**
     * Initialize a boat.
     */
    public void initBoat() {

        // Load a model.
        Spatial boat = assetManager.loadModel("Models/medium/ship/seaship.j3o");
        this.attachChild(boat);
    }
}
