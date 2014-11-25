package org.nhl.containing.cranes;
 
import com.jme3.asset.AssetManager;
import com.jme3.math.FastMath;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import com.jme3.scene.Spatial;
 
/**
 *
 * @author Jeroen
 */
public class TrainCrane extends Crane {
 
    private AssetManager assetManager;
    private static final Quaternion YAW090   = new Quaternion().fromAngleAxis(FastMath.PI/2,   new Vector3f(0,1,0));
   
    public TrainCrane(AssetManager assetManager) {
        this.assetManager = assetManager;
        initTrainCrane();
    }
 
    /**
     * Initialize a train crane.
     */
    private void initTrainCrane() {
 
        // Load a model.
        Spatial trainCrane = assetManager.loadModel("Models/high/crane/traincrane/crane.j3o");
        trainCrane.setLocalRotation(YAW090);
        this.attachChild(trainCrane);
    }
}