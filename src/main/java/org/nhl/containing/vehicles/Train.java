package org.nhl.containing.vehicles;

import com.jme3.asset.AssetManager;
import com.jme3.cinematic.MotionPath;
import com.jme3.cinematic.events.MotionEvent;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import com.jme3.scene.Spatial;
import org.nhl.containing.Container;

/**
 *
 * @author Jeroen
 */
public class Train extends Transporter {

    private AssetManager assetManager;
    private int numberOfWagons;
    private int wagonZAxis = -11;
    private int containerZAxis = -11;
    private float speed = 0.5f;
    private Container container;

    public Train(AssetManager assetManager, int numberOfWagons, Container c) {
        this.assetManager = assetManager;
        this.numberOfWagons = numberOfWagons;
        this.container = c;
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
            container.setLocalTranslation(0, 1f, containerZAxis);
            this.attachChild(container);
            wagonZAxis -= 15;
        }
    }
    /**
     * creates waypoints and lets the train drive over them
     * direction TRUE = the train arrives
     * direction FALSE = the train departs
     */
    public void move(boolean direction){
        MotionPath path = new MotionPath();
        MotionEvent motionControl= new MotionEvent(this, path);
        //train arrives
        if (direction) {
            path.addWayPoint(new Vector3f(250, 0, -180));
            path.addWayPoint(new Vector3f(-200, 0, -180));
            motionControl.setDirectionType(MotionEvent.Direction.PathAndRotation);
        }
        //train leaves
        else{
            path.addWayPoint(new Vector3f(-200, 0, -180));
            path.addWayPoint(new Vector3f(250, 0, -180));
        }
        motionControl.setRotation(new Quaternion().fromAngleNormalAxis(0, Vector3f.UNIT_Y));
        motionControl.setInitialDuration(10f);
        motionControl.setSpeed(speed);
        motionControl.play();
    }
    
}
