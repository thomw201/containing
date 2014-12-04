package org.nhl.containing.vehicles;

import com.jme3.asset.AssetManager;
import com.jme3.cinematic.MotionPath;
import com.jme3.cinematic.events.MotionEvent;
import com.jme3.math.FastMath;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import com.jme3.scene.Spatial;
import org.nhl.containing.Container;

import java.util.ArrayList;

/**
 * @author Jeroen
 */
public class Boat extends Transporter {

    private AssetManager assetManager;
    private float speed = 0.5f;
    private ArrayList<Container> containerList;
    // indoorship Dit is x=0 Y=0 Z=0, links onderin.
    private final int inxAs = -8;
    private final int inyAs = 0;
    private final int inzAs = 100;
    // zeeship Dit is x=0 Y=0 Z=0, links onderin.
    private final int zexAs = -23;
    private final int zeyAs = 0;
    private final int zezAs = 160;
    private Boat.ShipSize size;
    private Spatial boat;
    private MotionPath path;

    public Boat(AssetManager assetManager, ShipSize shipSize, ArrayList<Container> containerList) {
        this.assetManager = assetManager;
        this.size = shipSize;
        this.containerList = containerList;
        initBoat();
    }

    /**
     * Initialize a boat.
     */
    public void initBoat() {
        try {
            switch (size) {
                case INLANDSHIP:
                    // Load a model.
                    boat = assetManager.loadModel("Models/medium/ship/seaship.j3o");
                    
                    boat.scale(0.6f, 1, 0.37f);
                    this.attachChild(boat);
                    for (int i = 0; i < containerList.size(); i++) {
                        int x = (inxAs /** (i + 1)*/ + (containerList.get(i).getSpawnX() * 5)) * -1;
                        int y = containerList.get(i).getSpawnY() * 3;
                        int z = inzAs - (containerList.get(i).getSpawnZ());
                        containerList.get(i).setLocalTranslation(x, y, z);
                        this.attachChild(containerList.get(i));
                    }
                    this.rotate(new Quaternion().fromAngleAxis(FastMath.PI * 1.5f, new Vector3f(0, 1, 0)));
                    break;
                case SEASHIP:
                    // Load a model.
                    boat = assetManager.loadModel("Models/medium/ship/seaship.j3o");
                    boat.scale(0.87f, 1, 0.57f);
                    this.attachChild(boat);
                    for (int i = 0; i < containerList.size(); i++) {
                        int x = (zexAs + (containerList.get(i).getSpawnX() * 3)) * -1;
                        int y = containerList.get(i).getSpawnY() * 3;
                        int z = zezAs - (containerList.get(i).getSpawnZ());
                        containerList.get(i).setLocalTranslation(x, y, z);
                        this.attachChild(containerList.get(i));
                    }
                    break;
            }
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

    /**
     * creates waypoints and moves the ship over them direction
     * @param direction set to true to make ship arrive, false to make it leave
     * @param takefirstspot set to true if the main spot of the inlandship is not taken - it will go to second
     */
    public void move(boolean direction, boolean takefirstspot) {
        path = new MotionPath();
        MotionEvent motionControl = new MotionEvent(this, path);
        switch (size) {
            case SEASHIP:
                //seaship arrives
                if (direction) {
                    path.addWayPoint(new Vector3f(-750, 0, 500));
                    path.addWayPoint(new Vector3f(-330, 0, -20));
                } //seaship departs
                else {
                    path.addWayPoint(new Vector3f(-330, 0, -20));
                    path.addWayPoint(new Vector3f(-345, 0, -300));
                }
                break;
            case INLANDSHIP:
                // inlandship arrives
                if (direction) {
                    //at first parking spot
                    if (takefirstspot) {
                        path.addWayPoint(new Vector3f(-500, 0, 260));
                        path.addWayPoint(new Vector3f(-200, 0, 220));
                        path.addWayPoint(new Vector3f(-190, 0, 220));
                    } //arrive at second parking spot
                    else {
                        path.addWayPoint(new Vector3f(-400, 0, 320));
                        path.addWayPoint(new Vector3f(-50, 0, 280));
                        path.addWayPoint(new Vector3f(40, 0, 240));
                        path.addWayPoint(new Vector3f(80, 0, 220));
                    }

                } // inlandship departs
                else {
                    //from primary parking spot
                    if (takefirstspot) {
                        path.addWayPoint(new Vector3f(-200, 0, 220));
                        path.addWayPoint(new Vector3f(-115, 0, 275));
                        path.addWayPoint(new Vector3f(450, 0, 290));
                    }
                    //else depart from secondary
                    else{
                        path.addWayPoint(new Vector3f(80, 0, 220));
                        path.addWayPoint(new Vector3f(450, 0, 250));
                    }
                }
                break;
        }
        path.setCurveTension(0.3f);
        motionControl.setRotation(new Quaternion().fromAngleNormalAxis((float) Math.PI, Vector3f.UNIT_Y));
        //motionControl.setInitialDuration(10f);
        motionControl.setSpeed(speed);
        motionControl.play();
    }
    /**
     * Debug method, displays object name, speed, amount of containers and it's waypoints.
     * @return information about this object
     */
    public String getDebugInfo(){
        String info = size + "\nSpeed: " + speed + "\nLocation: " + this.getLocalTranslation() + "\nCarrying: " + containerList.size() + " containers.\n";
        for (int i = 0; i < path.getNbWayPoints(); i++) {
            info += "Waypoint " + (i+1) + ": " + path.getWayPoint(i) + " ";
        }
        return info + "\n";
    }

    public static enum ShipSize {

        INLANDSHIP, SEASHIP
    }
}
