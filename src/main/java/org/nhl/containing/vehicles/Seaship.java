package org.nhl.containing.vehicles;

import com.jme3.asset.AssetManager;
import com.jme3.bounding.BoundingBox;
import com.jme3.cinematic.MotionPath;
import com.jme3.cinematic.events.MotionEvent;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import com.jme3.scene.Spatial;
import java.util.List;
import org.nhl.containing.Container;

/**
 *
 * @author Thom
 */
public class Seaship extends Transporter {

    private AssetManager assetManager;
    private float speed = 0.5f;
    private List<Container> containerList;
    // zeeship Dit is x=0 Y=0 Z=0, links onderin ( als je vanaf de achterkant kijkt ) 
    // Hier passen op de x as 16 containers op
    private final int zexAs = -23;
    private final int zeyAs = 0;
    private final int zezAs = 160;
    private Spatial boat;
    private MotionPath path;
    private MotionEvent motionControl;

    public Seaship(AssetManager assetManager, int id, List<Container> containerList) {
        super(id);
        this.assetManager = assetManager;
        this.containerList = containerList;
        initSeaship();
        initMotionPaths();
    }
    /**
     * Initialize a boat.
     */
    public void initSeaship() {
        try {
            // Load a model.
            boat = assetManager.loadModel("Models/medium/ship/seaship.j3o");
            boat.scale(0.87f, 1, 0.57f);
            this.attachChild(boat);
            for (int i = 0; i < containerList.size(); i++) {
                float containerLength = containerList.get(i).getBoundingBox().getYExtent() * 10;
                float x = zexAs + containerList.get(i).getSpawnY() * 3;
                float z = zezAs - containerList.get(i).getSpawnX() * containerLength;
                float y = zeyAs + containerList.get(i).getSpawnZ() * 3f;
                containerList.get(i).setLocalTranslation(x, y, z);
                this.attachChild(containerList.get(i));
            }
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }
        /**
     * Initialize motionpath and motionevent
     */
    private void initMotionPaths() {
        path = new MotionPath();
        motionControl = new MotionEvent(this, path);
        motionControl.setSpeed(speed);
        motionControl.setRotation(new Quaternion().fromAngleNormalAxis((float) Math.PI, Vector3f.UNIT_Y));
    }
    /**
     * Lets the seaship arrive
     * @param location not used
     */
    @Override
    public void arrive(int location) {
        path.clearWayPoints();
        path.addWayPoint(new Vector3f(-750, 0, 500));
        path.addWayPoint(new Vector3f(-330, 0, -20));
        path.setCurveTension(0.3f);
        motionControl.play();
    }
    /**
     * Makes this seaship depart
     */
    @Override
    public void depart() {
        path.clearWayPoints();
        path.addWayPoint(new Vector3f(-330, 0, -20));
        path.addWayPoint(new Vector3f(-345, 0, -300));
        path.setCurveTension(0.3f);
        motionControl.play();
    }
        /**
     * Debug method, displays object name, speed, amount of containers and it's
     * waypoints.
     *
     * @return information about this object
     */
    public String getDebugInfo() {
        String info = this.getClass().getSimpleName() + "\nSpeed: " + speed + "\nLocation: " + this.getLocalTranslation() + "\nCarrying: " + containerList.size() + " containers.\n";
        for (int i = 0; i < path.getNbWayPoints(); i++) {
            info += "Waypoint " + (i + 1) + ": " + path.getWayPoint(i) + " ";
        }
        return info + "\n";
    }

    /**
     * Debug method, displays object name, speed, amount of containers and it's
     * waypoints.
     *
     * @return information about this object
     */
    public String getWaypoints() {
        String info = "\nSeaship waypoints ";
        for (int j = 0; j < path.getNbWayPoints(); j++) {
            info += "Waypoint " + (j + 1) + ": " + path.getWayPoint(j) + " ";
        }
        return info + "\n";
    }
}
