package org.nhl.containing.cranes;

import com.jme3.asset.AssetManager;
import com.jme3.material.Material;
import com.jme3.scene.Spatial;
import org.nhl.containing.cranes.Crane;

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

        /**
         * Load a model and give it a material. 
         */
        Spatial moveableCrane = assetManager.loadModel("Models/medium/crane/dockingcrane.j3o");
        Material defaultMat = new Material(assetManager, "Common/MatDefs/Misc/ShowNormals.j3md");
        moveableCrane.setMaterial(defaultMat);
        this.attachChild(moveableCrane);
    }
}
