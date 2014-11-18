package org.nhl.containing.vehicles;

import com.jme3.asset.AssetManager;
import com.jme3.material.Material;
import com.jme3.scene.Spatial;

/**
 *
 * @author Jeroen
 */
public class Lorry extends Transporter {
    private AssetManager assetManager;

    public Lorry(AssetManager assetManager) {
        this.assetManager = assetManager;
        initLorry();
    }

    
    /**
     * Initialize a lorry.
     */
    public void initLorry() {

        /**
         * Load a model and give it a material. 
         */
        Spatial lorry = assetManager.loadModel("Models/medium/truck.j3o");
        Material defaultMat = new Material(assetManager, "Common/MatDefs/Misc/ShowNormals.j3md");
        lorry.setMaterial(defaultMat);
        this.attachChild(lorry);
    }
}
