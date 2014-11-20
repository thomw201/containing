package org.nhl.containing.cranes;

import com.jme3.asset.AssetManager;
import com.jme3.scene.Spatial;

/**
 *
 * @author Jeroen
 */
public class TrainCrane extends Crane {

    private AssetManager assetManager;

    public TrainCrane(AssetManager assetManager) {
        this.assetManager = assetManager;
        initTrainCrane();
    }

    /**
     * Initialize a train crane.
     */
    public void initTrainCrane() {

        // Load a model.
        Spatial trainCrane = assetManager.loadModel("Models/high/crane/traincrane/crane.j3o");
        this.attachChild(trainCrane);
    }
}
