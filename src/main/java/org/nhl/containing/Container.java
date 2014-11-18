package org.nhl.containing;

import com.jme3.asset.AssetManager;
import com.jme3.material.Material;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;

/**
 *
 * @author Jeroen
 */
public class Container extends Node {

    private AssetManager assetManager;

    public Container(AssetManager assetManager) {
        this.assetManager = assetManager;
        initContainer();
    }

    
    /**
     * Initialize a container.
     */
    public void initContainer() {

        /**
         * Load a model and give it a material. 
         */
        Spatial container = assetManager.loadModel("Models/medium/container/container.j3o");
        Material defaultMat = new Material(assetManager, "Common/MatDefs/Misc/ShowNormals.j3md");
        container.setMaterial(defaultMat);
        this.attachChild(container);
    }
}
