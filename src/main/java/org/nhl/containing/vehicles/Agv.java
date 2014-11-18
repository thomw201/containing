package org.nhl.containing.vehicles;

import com.jme3.asset.AssetManager;
import com.jme3.material.Material;
import com.jme3.scene.Spatial;

/**
 *
 * @author Jeroen
 */
public class Agv extends Vehicle {
    private AssetManager assetManager;

    public Agv(AssetManager assetManager) {
        this.assetManager = assetManager;
        initAgv();
    }

    
    /**
     * Initialize a Agv.
     */
    public void initAgv() {

        /**
         * Load a model and give it a material. 
         */
        Spatial agv = assetManager.loadModel("Models/medium/agv/agv.j3o");
        Material defaultMat = new Material(assetManager, "Common/MatDefs/Misc/ShowNormals.j3md");
        agv.setMaterial(defaultMat);
        this.attachChild(agv);
    }
}
