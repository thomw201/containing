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
public class Lorry extends Transporter {

    private AssetManager assetManager;
    private float speed = 1f;
    private int lorryZAxis = 11;
    private Container containter;
    public Lorry(AssetManager assetManager, Container c) {
        this.assetManager = assetManager;
        this.containter = c;
        initLorry();
    }

    /**
     * Initialize a lorry.
     */
    public void initLorry() {


        // Load a model.
        Spatial lorry = assetManager.loadModel("Models/medium/truck.j3o");
        lorry.setLocalTranslation(0, 0, lorryZAxis);
        containter.setLocalTranslation(0, 1, lorryZAxis);
        this.attachChild(lorry);
        this.attachChild(containter);
    }
     /**
     * creates waypoints and lets the lorrys drive over them 
     * direction TRUE = arrive 
     * direction FALSE = depart
     */
    public void move(boolean direction) {
        MotionPath path = new MotionPath();
        MotionEvent motionControl = new MotionEvent(this, path);
        //boat arrives
        if (direction) {
            path.addWayPoint(new Vector3f(250, 0, 180));
            path.addWayPoint(new Vector3f(200, 0, 180));
        } //boat leaves
        else {
            path.addWayPoint(new Vector3f(200, 0, 180));
            path.addWayPoint(new Vector3f(250, 0, 180));
        }
        motionControl.setRotation(new Quaternion().fromAngleNormalAxis(0, Vector3f.UNIT_Y));
        motionControl.setDirectionType(MotionEvent.Direction.PathAndRotation);
        motionControl.setInitialDuration(10f);
        motionControl.setSpeed(speed);
        motionControl.play();
    }
}
