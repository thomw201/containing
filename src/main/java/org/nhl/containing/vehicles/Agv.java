package org.nhl.containing.vehicles;

import com.jme3.asset.AssetManager;
import com.jme3.scene.Spatial;
import org.nhl.containing.Container;

/**
 * @author Jeroen
 */
public class Agv extends Vehicle {

    private AssetManager assetManager;
    private Container container;
    private float speed;
    
    public Agv(AssetManager assetManager) {
        this.assetManager = assetManager;
        initAgv();
    }

    /**
     * Initialize an Agv.
     */
    private void initAgv() {

        // Load a model.
        Spatial agv = assetManager.loadModel("Models/medium/agv/agv.j3o");
        this.attachChild(agv);
    }
        /**
     * Debug method, displays object name, speed, amount of containers and it's waypoints.
     * @return debug information about the object
     */
    public String getDebugInfo(){
        String info = this.getClass().getSimpleName() + "\nSpeed: " + speed + "\nLocation: " + this.getLocalTranslation() + "\nCarrying: ";
        if(container != null){
            info+= "1 Container.\n";
        }
        else{
            info += "nothing.\n";
        }
        //get waypoints for the AGV (does not exist in this class yet)
//        for (int i = 0; i < path.getNbWayPoints(); i++) {
//            info += "Waypoint " + (i+1) + ": " + path.getWayPoint(i) + " ";
//        }
        return info + "\n";
    }
}
