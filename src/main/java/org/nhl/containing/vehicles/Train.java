package org.nhl.containing.vehicles;

import com.jme3.asset.AssetManager;
import com.jme3.scene.Spatial;

/**
 *
 * @author Jeroen
 */
public class Train extends Transporter {

    private AssetManager assetManager;
    private int numberOfWagons;
    private int wagonZAxis = -11;

    public Train(AssetManager assetManager, int numberOfWagons) {
        this.assetManager = assetManager;
        this.numberOfWagons = numberOfWagons;
        initTrain();
    }

    /**
     * Initialize a train.
     */
    public void initTrain() {

        // Load a model.
        Spatial train = assetManager.loadModel("Models/medium/train/train.j3o");
        this.attachChild(train);

        //Load wagons.
        Spatial wagon = assetManager.loadModel("Models/medium/train/wagon.j3o");

        for (int i = 0; i < numberOfWagons; i++) {
            Spatial nextWagon = wagon.clone();
            nextWagon.setLocalTranslation(0, 0, wagonZAxis);
            this.attachChild(nextWagon);
            wagonZAxis -= 15;
        }
    }
}
