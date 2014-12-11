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
public class Agv extends Vehicle {

    private AssetManager assetManager;
    private Container container;
    private float speed = 1f;
    private MotionPath path;
    private MotionPath trainPlatformPath;
    private MotionPath trainPlatformPath2;
    private MotionEvent motionControl;
    private MotionEvent trainPathMotionControl;
    private MotionEvent trainPathMotionControl2;

    public Agv(AssetManager assetManager) {
        this.assetManager = assetManager;
        initAgv();
        initPaths();
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
     * initialize the paths
     */
    private void initPaths(){
        path = new MotionPath();
        motionControl = new MotionEvent(this, path);
        // set the speed and direction of the AGV using motioncontrol
        motionControl.setDirectionType(MotionEvent.Direction.PathAndRotation);
        motionControl.setRotation(new Quaternion().fromAngleNormalAxis(0, Vector3f.UNIT_Y));
        motionControl.setSpeed(speed);
        trainPlatformPath = new MotionPath();
        trainPathMotionControl = new MotionEvent(this, trainPlatformPath);
        // set the speed and direction of the AGV using motioncontrol
        trainPathMotionControl.setDirectionType(MotionEvent.Direction.PathAndRotation);
        trainPathMotionControl.setRotation(new Quaternion().fromAngleNormalAxis(0, Vector3f.UNIT_Y));
        trainPathMotionControl.setSpeed(speed);
        trainPlatformPath2 = new MotionPath();
        trainPathMotionControl2 = new MotionEvent(this, trainPlatformPath2);
        // set the speed and direction of the AGV using motioncontrol
        trainPathMotionControl2.setDirectionType(MotionEvent.Direction.PathAndRotation);
        trainPathMotionControl2.setRotation(new Quaternion().fromAngleNormalAxis(0, Vector3f.UNIT_Y));
        trainPathMotionControl2.setSpeed(speed);
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
                //Enter train path
                case 'I':
                    path.addWayPoint(new Vector3f(60, 0, -171));
                    break;
                //Enter seaship path
                case 'J':
                    path.addWayPoint(new Vector3f(-240, 0, -150));
                    path.addWayPoint(new Vector3f(-285, 0, -150));
                    break;
                //Enter inlandship path
                case 'K':
                    path.addWayPoint(new Vector3f(-230, 0, 162));
                    path.addWayPoint(new Vector3f(-285, 0, 162));
                    path.addWayPoint(new Vector3f(-285, 0, 180));
                    break;
            }
        }
        path.setCurveTension(0.1f);
        motionControl.play();
    }

    public void parkAtTrainPlatform(int location) {
        path.addWayPoint(new Vector3f(-175 + (20 * location), 0, -172));
        path.addWayPoint(new Vector3f(-188 + (20 * location), 0, -176));
        path.addWayPoint(new Vector3f(-190 + (20 * location), 0, -176));
    }

    public void leaveTrainPlatform() {
        trainPlatformPath2.clearWayPoints();
        trainPlatformPath2.addWayPoint(new Vector3f(this.getWorldTranslation().x, this.getWorldTranslation().y, this.getWorldTranslation().z));
        trainPlatformPath2.addWayPoint(new Vector3f(this.getWorldTranslation().x-5, this.getWorldTranslation().y, this.getWorldTranslation().z+5));
        trainPlatformPath2.addWayPoint(new Vector3f(-210, 0, -171));
        trainPlatformPath2.addWayPoint(new Vector3f(-210, 0, -140));
        trainPlatformPath2.setCurveTension(0.3f);
        trainPathMotionControl2.play();
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
     * Creates all AGV waypoints and returns the coörds
     *
     * @return string with the waypoints
     */
    public String getWaypoints() {
        char[] debugarr = {'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H'};
        move(debugarr);
        String info = "\nAGV Waypoints: ";
        for (int j = 0; j < path.getNbWayPoints(); j++) {
            info += "Waypoint " + (j + 1) + ": " + path.getWayPoint(j) + " ";
        }
        return info + "\n";
    }
}
