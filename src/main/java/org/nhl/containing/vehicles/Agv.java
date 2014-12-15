package org.nhl.containing.vehicles;

import com.jme3.asset.AssetManager;
import com.jme3.cinematic.MotionPath;
import com.jme3.cinematic.events.MotionEvent;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import com.jme3.scene.Spatial;
import org.nhl.containing.Container;

public class Agv extends Vehicle {

    private AssetManager assetManager;
    private Container container;
    private float speed = 0.5f;
    private MotionPath path;
    MotionEvent motionControl;

    public Agv(AssetManager assetManager, int id) {
        super(id);
        this.assetManager = assetManager;
        initAgv();
        initMotionPaths();
    }

    /**
     * Initialize an Agv.
     */
    private void initAgv() {
        // Load a model.
        Spatial agv = assetManager.loadModel("Models/low/agv/agv.j3o");
        this.attachChild(agv);
    }

    /**
     * Initialize motionpath and motionevent
     */
    private void initMotionPaths() {
        path = new MotionPath();
        motionControl = new MotionEvent(this, path);
        motionControl.setSpeed(speed);
        // set the speed and direction of the AGV using motioncontrol
        motionControl.setDirectionType(MotionEvent.Direction.PathAndRotation);
        motionControl.setRotation(new Quaternion().fromAngleNormalAxis(0, Vector3f.UNIT_Y));
    }

    /**
     * Method that moves this agv over the given path
     *
     * @param path character arraylist filled with the waypoints
     */
    public void move(char[] route) {
        for (char waypoint : route) {
            switch (waypoint) {
                case 'A':
                    path.addWayPoint(new Vector3f(580, 0, -140));
                    break;
                case 'B':
                    path.addWayPoint(new Vector3f(580, 0, 135));
                    break;
                case 'C':
                    path.addWayPoint(new Vector3f(330, 0, -140));
                    break;
                case 'D':
                    path.addWayPoint(new Vector3f(330, 0, 136));
                    break;
                case 'E':
                    path.addWayPoint(new Vector3f(70, 0, -140));
                    break;
                case 'F':
                    path.addWayPoint(new Vector3f(70, 0, 135));
                    break;
                case 'G':
                    path.addWayPoint(new Vector3f(-210, 0, -140));
                    break;
                case 'H':
                    path.addWayPoint(new Vector3f(-210, 0, 135));
                    break;
                case 'I':
                    path.addWayPoint(new Vector3f(60, 0, -171));
                    path.addWayPoint(new Vector3f(30, 0, -171));
                    break;
                //Enter seaship path
                case 'J':
                    path.addWayPoint(new Vector3f(-240, 0, -150));
                    path.addWayPoint(new Vector3f(-283, 0, -150));
                    break;
                //Enter inlandship path
                case 'K':
                    path.addWayPoint(new Vector3f(-230, 0, 162));
                    path.addWayPoint(new Vector3f(-285, 0, 162));
                    path.addWayPoint(new Vector3f(-285, 0, 177));
                    break;
            }
        }
        path.setCurveTension(0.1f);
        motionControl.play();
    }

    /**
     * Makes the agv park on the trainplatform AGV should be at location I
     *
     * @param location the parking place
     */
    public void parkAtTrainPlatform(int location) {
        path.clearWayPoints();
        path.addWayPoint(new Vector3f(30, 0, -171));
        path.addWayPoint(new Vector3f(-175 + (20 * location), 0, -172));
        path.addWayPoint(new Vector3f(-188 + (20 * location), 0, -176));
        path.addWayPoint(new Vector3f(-190 + (20 * location), 0, -176));
        path.setCurveTension(0.3f);
        motionControl.play();
    }

    /**
     * Makes the agv park on the seashipplatform AGV should be at location J
     *
     * @param location the parking place
     */
    public void parkAtSeashipPlatform(int location) {
        path.clearWayPoints();
        path.addWayPoint(new Vector3f(-284, 0, -184));
        path.addWayPoint(new Vector3f(-284, 0, 80 - (20 * location)));
        path.addWayPoint(new Vector3f(-289, 0, 135 - (20 * location)));
        path.setCurveTension(0.3f);
        motionControl.play();
    }

    /**
     * Makes the agv park on the inlandshipplatform the AGV should be at
     * location K
     *
     * @param location the parking place
     */
    public void parkAtInlandshipPlatform(int location) {
        path.clearWayPoints();
        path.addWayPoint(new Vector3f(-285, 0, 177));
        path.addWayPoint(new Vector3f(130 - (20 * location), 0, 177));
        path.addWayPoint(new Vector3f(145 - (20 * location), 0, 183));
        path.addWayPoint(new Vector3f(147 - (20 * location), 0, 183));
        path.setCurveTension(0.1f);
        motionControl.play();
    }

    /**
     * Method for making a parked AGV leave the Seashipplatform
     */
    public void leaveSeashipPlatform() {
        path.clearWayPoints();
        path.addWayPoint(new Vector3f(this.getWorldTranslation().x, this.getWorldTranslation().y, this.getWorldTranslation().z));
        path.addWayPoint(new Vector3f(this.getWorldTranslation().x + 7, 0, this.getWorldTranslation().z + 5));
        path.addWayPoint(new Vector3f(this.getWorldTranslation().x + 7, 0, 156));
        path.addWayPoint(new Vector3f(-205, 0, 156));
        path.setCurveTension(0.3f);
        motionControl.play();
    }

    /**
     * Method for making a parked AGV leave the inlandship platform
     */
    public void leaveInlandshipPlatform() {
        path.clearWayPoints();
        path.addWayPoint(new Vector3f(this.getWorldTranslation().x, this.getWorldTranslation().y, this.getWorldTranslation().z));
        path.addWayPoint(new Vector3f(this.getWorldTranslation().x+15, 0, this.getWorldTranslation().z-7));
        path.addWayPoint(new Vector3f(180, 0, 177));
        path.addWayPoint(new Vector3f(180, 0, 140));
        path.setCurveTension(0.1f);
        motionControl.play();
    }

    /**
     * Method for making a parked AGV leave the trainplatform
     */
    public void leaveTrainPlatform() {
        path.clearWayPoints();
        path.addWayPoint(new Vector3f(this.getWorldTranslation().x, this.getWorldTranslation().y, this.getWorldTranslation().z));
        path.addWayPoint(new Vector3f(this.getWorldTranslation().x - 5, this.getWorldTranslation().y, this.getWorldTranslation().z + 5));
        path.addWayPoint(new Vector3f(-210, 0, -171));
        path.addWayPoint(new Vector3f(-210, 0, -140));
        path.setCurveTension(0.3f);
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
        //get waypoints for the AGV (does not exist in this class yet)
//        for (int i = 0; i < path.getNbWayPoints(); i++) {
//            info += "Waypoint " + (i+1) + ": " + path.getWayPoint(i) + " ";
//        }
        return info + "\n";
    }

    /**
     * Gets all created waypoints
     *
     * @return string with the waypoints
     */
    public String getWaypoints() {
        String info = "\nAGV Waypoints: ";
        for (int j = 0; j < path.getNbWayPoints(); j++) {
            info += "Waypoint " + (j + 1) + ": " + path.getWayPoint(j) + " ";
        }
        return info + "\n";
    }
}
