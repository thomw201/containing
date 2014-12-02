package org.nhl.containing.vehicles;

import com.jme3.asset.AssetManager;
import com.jme3.cinematic.MotionPath;
import com.jme3.cinematic.events.MotionEvent;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import com.jme3.scene.Spatial;
import org.nhl.containing.Container;

/**
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
     * this methods makes the lorrys arrive at the given position
     *
     * @param direction direction set to true for incoming, false for outgoing
     * @param parkingPlace 0-19, the parking place the lorry will drive to/from
     */
    public void move(boolean direction, int parkingPlace) {
        MotionPath path = new MotionPath();
        MotionEvent motionControl = new MotionEvent(this, path);
        if (direction) {
            path.addWayPoint(new Vector3f(566 - (14 * parkingPlace), 0, 185));
            path.addWayPoint(new Vector3f(566 - (14 * parkingPlace), 0, 159));
        } else {
            path.addWayPoint(new Vector3f(566 - (14 * parkingPlace), 0, 159));
            path.addWayPoint(new Vector3f(566 - (14 * parkingPlace), 0, 185));
        }
        motionControl.setSpeed(speed);
        motionControl.play();

    }
}
