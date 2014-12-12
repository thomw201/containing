package org.nhl.containing.vehicles;

import com.jme3.asset.AssetManager;
import com.jme3.bounding.BoundingBox;
import com.jme3.cinematic.MotionPath;
import com.jme3.cinematic.events.MotionEvent;
import com.jme3.math.FastMath;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import com.jme3.scene.Spatial;
import java.util.List;
import org.nhl.containing.Container;

public class Inlandship extends Transporter {

    private AssetManager assetManager;
    private float speed = 0.5f;
    private List<Container> containerList;
    private BoundingBox boundingBox;
    private Spatial boat;
    private MotionPath path;
    private MotionEvent motionControl;

    public Inlandship(AssetManager assetManager, int id, List<Container> containerList) {
        super(id);
        this.assetManager = assetManager;
        this.containerList = containerList;
        initInlandship();
        initMotionPaths();
    }

    /**
     * Initialize the inlandship
     */
    public void initInlandship() {
        try {
            // Load a model.
            boat = assetManager.loadModel("Models/medium/ship/seaship.j3o");
            boat.scale(0.4f, 1, 0.37f);
            boundingBox = (BoundingBox) boat.getWorldBound();

            float inxAs = boundingBox.getXExtent();
            float inyAs = boundingBox.getZExtent() - 7;
            float inzAs = 0;
            this.attachChild(boat);
            for (int i = 0; i < containerList.size(); i++) {
                float containerLength = containerList.get(i).getBoundingBox().getYExtent();
                float containerWidth = containerList.get(i).getBoundingBox().getXExtent();
                float x = (inxAs - containerWidth) - containerList.get(i).getSpawnY() * 3;
                float y = inzAs + containerList.get(i).getSpawnZ() * 3f;
                float z = (inyAs - containerLength) - containerList.get(i).getSpawnX() * (containerLength * 10);
                containerList.get(i).setLocalTranslation(x, y, z);
                this.attachChild(containerList.get(i));
            }
            this.rotate(new Quaternion().fromAngleAxis(FastMath.PI * 1.5f, new Vector3f(0, 1, 0)));
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
     * Makes inlandship arrive at the given position
     *
     * @param location (0-1) 0 for main parking place, 1 for secondary place
     */
    @Override
    public void arrive(int location) {
        path.clearWayPoints();
        switch (location) {
            case 0:
                path.addWayPoint(new Vector3f(-500, 0, 260));
                path.addWayPoint(new Vector3f(-200, 0, 220));
                path.addWayPoint(new Vector3f(-190, 0, 220));
                break;
            case 1:
                path.addWayPoint(new Vector3f(-400, 0, 320));
                path.addWayPoint(new Vector3f(-50, 0, 280));
                path.addWayPoint(new Vector3f(40, 0, 240));
                path.addWayPoint(new Vector3f(80, 0, 220));
                break;
            default:
                throw new IllegalArgumentException(location + " is an invalid location");
        }
        path.setCurveTension(0.3f);
        motionControl.play();
    }

    /**
     * Makes this inlandship depart
     */
    @Override
    public void depart() {
        path.clearWayPoints();
        //if x coörd = -190 this inlandship is parked at the first(main) position
        if (this.getLocalTranslation().x == -190) {
            path.addWayPoint(new Vector3f(-200, 0, 220));
            path.addWayPoint(new Vector3f(-115, 0, 275));
            path.addWayPoint(new Vector3f(450, 0, 290));
        } else {
            //else depart from secondary parkingplace
            path.addWayPoint(new Vector3f(80, 0, 220));
            path.addWayPoint(new Vector3f(450, 0, 250));
        }
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
        String info = "\nInlandship's waypoints ";
        for (int j = 0; j < path.getNbWayPoints(); j++) {
            info += "Waypoint " + (j + 1) + ": " + path.getWayPoint(j) + " ";
        }
        return info + "\n";
    }
}
