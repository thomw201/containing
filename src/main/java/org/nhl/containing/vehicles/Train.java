package org.nhl.containing.vehicles;

import com.jme3.asset.AssetManager;
import com.jme3.scene.Spatial;

/**
 *
 * @author Jeroen
 */
public class Train extends Transporter {

    private AssetManager assetManager;

    public Train(AssetManager assetManager) {
        this.assetManager = assetManager;
        initTrain();
    }

    /**
     * Initialize a train.
     */
    public void initTrain() {

        // Load a model and give it a material.
        Spatial train = assetManager.loadModel("Models/medium/train/train.j3o");
        this.attachChild(train);

        //TODO: Add wagons.
    }
}
