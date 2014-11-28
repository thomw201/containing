package org.nhl.containing.vehicles;

import com.jme3.asset.AssetManager;
import com.jme3.cinematic.MotionPath;
import com.jme3.cinematic.events.MotionEvent;
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
    private ArrayList<Container> inlandshipContainerList;
    private ArrayList<Container> seashipContainerList;
    /**
     * TIJDELIJK! indoorship Dit is x=0 Y=0 Z=0, links onderin.
     */
    private int inxAs = -8;
    private int inyAs = 0;
    private int inzAs = 100;
    /**
     * zeeship Dit is x=0 Y=0 Z=0, links onderin.
     */
    private int zexAs = -23;
    private int zeyAs = 0;
    private int zezAs = 160;
    private Boat.Size size;

    ;
    private Spatial boat;
    public Boat(AssetManager assetManager, Size size,
                ArrayList<Container> inlandshipContainerList, ArrayList<Container> seashipContainerList) {
        this.assetManager = assetManager;
        this.size = size;
        this.inlandshipContainerList = inlandshipContainerList;
        this.seashipContainerList = seashipContainerList;
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
                    boat.scale(0.4f, 1, 0.37f);
                    this.attachChild(boat);
                    for (Container c : inlandshipContainerList) {
                        c.setLocalTranslation(inxAs, 0, inzAs);
                        this.attachChild(c);
                        if (inxAs < 3) {
                            inxAs += 5;
                        } else {
                            inzAs -= 14;
                        }
                    }
                    break;
                case SEASHIP:
                    // Load a model.
                    boat = assetManager.loadModel("Models/medium/ship/seaship.j3o");
                    this.attachChild(boat);
                    boat.scale(0.87f, 1, 0.57f);
                    for (Container c : seashipContainerList) {
                        c.setLocalTranslation(speed, speed, speed);
                        this.attachChild(c);
                        c.setLocalTranslation(zexAs, zeyAs, zezAs);
                        this.attachChild(c);
                        if (zexAs < 22) {
                            zexAs += 3;
                        } else if (zezAs >= -119) {
                            zezAs -= 14;
                        } else {
                            zeyAs += 2;
                        }
                    }
                    break;
            }
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

    /**
     * creates the trains waypoints and lets the train drive over them direction
     * TRUE = the train arrives direction FALSE = the train departs
     */
    public void move(boolean direction) {
        MotionPath path = new MotionPath();
        MotionEvent motionControl = new MotionEvent(this, path);
        switch (size) {
            case SEASHIP:
                //seaship arrives
                if (direction) {
                    path.addWayPoint(new Vector3f(-700, 0, 500));
                    path.addWayPoint(new Vector3f(-330, 0, 0));
                } //seaship departs
                else {
                    path.addWayPoint(new Vector3f(-330, 0, 0));
                    path.addWayPoint(new Vector3f(-345, 0, -300));
                }
                break;
            case INLANDSHIP:
                motionControl.setDirectionType(MotionEvent.Direction.PathAndRotation);
                if (direction) {
                    path.addWayPoint(new Vector3f(-400, 0, 200));
                    path.addWayPoint(new Vector3f(-150, 0, 250));
                } //seaship leaves
                else {
                    path.addWayPoint(new Vector3f(-330, 0, 0));
                    path.addWayPoint(new Vector3f(-150, 0, 500));
                    break;
                }
                path.setCurveTension(0.01f);
        }
        motionControl.setRotation(new Quaternion().fromAngleNormalAxis((float) Math.PI, Vector3f.UNIT_Y));
        motionControl.setInitialDuration(10f);
        motionControl.setSpeed(speed);
        motionControl.play();
    }

    public enum Size {

        INLANDSHIP, SEASHIP
    }
}
