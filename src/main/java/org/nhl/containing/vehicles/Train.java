package org.nhl.containing.vehicles;

import com.jme3.asset.AssetManager;
import com.jme3.cinematic.MotionPath;
import com.jme3.cinematic.events.MotionEvent;
import com.jme3.math.FastMath;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import java.util.ArrayList;
import org.nhl.containing.Container;

/**
 *
 * @author Jeroen
 */
public class Train extends Transporter {

    private AssetManager assetManager;
    private int wagonZAxis = -11;
    private float speed = 0.8f;
    private ArrayList<Container> trainContainerList;
    
    public Train(AssetManager assetManager, ArrayList<Container> trainContainerList) {
        this.assetManager = assetManager;
        this.trainContainerList = trainContainerList;
        initTrain();
        this.rotate(new Quaternion().fromAngleAxis(FastMath.PI*3/2, new Vector3f(0,1,0)));
    }

    /**
     * Initialize a train.
     */
    public void initTrain() {
        // Load a model.
        Spatial train = assetManager.loadModel("Models/medium/train/train.j3o");
        this.attachChild(train);

        //Load wagons.
        Node wagon =(Node) assetManager.loadModel("Models/medium/train/wagon.j3o");

        for (int i = 0; i < trainContainerList.size(); i++) {
            Node nextWagon = (Node)wagon.clone();
            nextWagon.setLocalTranslation(0, 0, wagonZAxis);
            trainContainerList.get(i).setLocalTranslation(0, 1, 0);
            nextWagon.attachChild(trainContainerList.get(i));
            
            this.attachChild(nextWagon);
            
            wagonZAxis -= 15;
        }
    }

    /**
     * creates waypoints and lets the train drive over them direction TRUE = the
     * train arrives direction FALSE = the train departs
     */
    public void move(boolean direction) {
        MotionPath path = new MotionPath();
        MotionEvent motionControl = new MotionEvent(this, path);
        //train arrives
        if (direction) {
            path.addWayPoint(new Vector3f(250, 0, -180));
            path.addWayPoint(new Vector3f(-200, 0, -180));
        } //train leaves
        else {
            path.addWayPoint(new Vector3f(-200, 0, -180));
            path.addWayPoint(new Vector3f(250, 0, -180));
            //this.rotate(new Quaternion().fromAngleAxis(FastMath.PI*3/2, new Vector3f(0,1,0)));
        }
        motionControl.setInitialDuration(10f);
        motionControl.setSpeed(speed);
        motionControl.play();
    }
}
