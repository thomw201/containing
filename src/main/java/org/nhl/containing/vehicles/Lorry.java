package org.nhl.containing.vehicles;

import com.jme3.asset.AssetManager;
import com.jme3.cinematic.MotionPath;
import com.jme3.cinematic.events.MotionEvent;
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
    private Container container;
    private MotionPath path;

    public Lorry(AssetManager assetManager, Container c) {
        this.assetManager = assetManager;
        this.container = c;
        initLorry();
    }

    /**
     * Initialize a lorry.
     */
    public void initLorry() {


        // Load a model.
        Spatial lorry = assetManager.loadModel("Models/medium/truck.j3o");
        lorry.setLocalTranslation(0, 0, lorryZAxis);
        container.setLocalTranslation(0, 1, lorryZAxis);
        this.attachChild(lorry);
        this.attachChild(container);
    }

    /**
     * this methods makes the lorrys arrive at the given position
     *
     * @param direction direction set to true for incoming, false for outgoing
     * @param parkingPlace 0-19, the parking place the lorry will drive to/from
     */
    public void move(boolean direction, int parkingPlace) {
        path = new MotionPath();
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

    /**
     * Debug method, displays object name, speed, amount of containers and it's
     * waypoints.
     *
     * @return debug information about the object
     */
    public String getDebugInfo() {
        String info = this.getClass().getSimpleName() + "\nSpeed: " + speed + "\nLocation: " + this.getLocalTranslation() + "\nCarrying: ";
        if (container != null) {
            info += "1 Container.\n";
        } else {
            info += "nothing.\n";
        }
        for (int i = 0; i < path.getNbWayPoints(); i++) {
            info += "Waypoint " + (i + 1) + ": " + path.getWayPoint(i) + " ";
        }
        return info + "\n";
    }

    /**
     * debug method that returns the waypoints of the given truck
     *
     * @param place parking place number
     * @return string containing the waypoints/debug info
     */
    public String getWaypoints(int place) {
        String info = "\nWaypoints truck #" + place + " : ";
        this.move(true, place);
        for (int j = 0; j < path.getNbWayPoints(); j++) {
            info += "Waypoint " + (j + 1) + ": " + path.getWayPoint(j) + " ";
        }
        return info + "\n";
    }
}
