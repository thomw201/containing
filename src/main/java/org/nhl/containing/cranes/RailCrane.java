package org.nhl.containing.cranes;

import com.jme3.asset.AssetManager;
import com.jme3.material.Material;
import com.jme3.scene.Spatial;
import org.nhl.containing.cranes.Crane;

/**
 *
 * @author Jeroen
 */
public class RailCrane extends Crane {
    private AssetManager assetManager;

    public RailCrane(AssetManager assetManager) {
        this.assetManager = assetManager;
        initRailCrane();
    }

    
    /**
     * Initialize a rail crane.
     */
    public void initRailCrane() {

        /**
         * Load a model and give it a material. 
         */
        Spatial railCrane = assetManager.loadModel("Models/medium/crane/storagecrane.j3o");
        Material defaultMat = new Material(assetManager, "Common/MatDefs/Misc/ShowNormals.j3md");
        railCrane.setMaterial(defaultMat);
        this.attachChild(railCrane);
    }
}
