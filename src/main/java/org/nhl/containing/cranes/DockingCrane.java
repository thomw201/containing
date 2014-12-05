package org.nhl.containing.cranes;

import com.jme3.asset.AssetManager;
import com.jme3.cinematic.MotionPath;
import com.jme3.cinematic.events.MotionEvent;
import com.jme3.math.Vector3f;
import com.jme3.scene.Spatial;
import org.nhl.containing.Container;
import org.nhl.containing.areas.BoatArea;
import org.nhl.containing.vehicles.Agv;

/**
 * @author Jeroen
 */
public class DockingCrane extends Crane {

    private AssetManager assetManager;
    private Agv agv;
    private Container container;
    private MotionPath containerPathUp = new MotionPath();
    private MotionPath containerPathDown = new MotionPath();
    private MotionPath cranePath = new MotionPath();
    private MotionPath newCranePath = new MotionPath();
    private int cranePathCounter;
    private int newCranePathCounter;
    private int containerPathUpCounter;
    private int containerPathDownCounter;
    private BoatArea.AreaType type;

    public DockingCrane(AssetManager assetManager, BoatArea.AreaType type) {
        this.assetManager = assetManager;
        this.type = type;
        initDockingCrane();
    }

    /**
     * Initialize a docking crane.
     */
    private void initDockingCrane() {

        // Load a model.
        Spatial moveableCrane = assetManager.loadModel("Models/high/crane/dockingcrane/crane.j3o");
        this.attachChild(moveableCrane);
    }

    /**
     * Let the crane move to the container's position. And Put the container on
     * the Agv.
     *
     * @param container Request a container.
     * @param agv Agv that needs the container.
     */
    public void boatToAgv(Container container, Agv agv) {
        this.container = container;
        this.agv = agv;

        initWaypoints();

        cranePathCounter = cranePath.getNbWayPoints();
        cranePath.setCurveTension(0.0f);
        cranePath.enableDebugShape(assetManager, this);
        cranePath.addListener(this);

        MotionEvent motionControl = new MotionEvent(this, cranePath);
        motionControl.setDirectionType(MotionEvent.Direction.None);
        motionControl.play();
        motionControl.dispose();
    }

    /**
     * Put the container on the crane.
     */
    private void moveContainer() {
        container.rotate(container.getWorldRotation());
        containerPathUpCounter = containerPathUp.getNbWayPoints();
        containerPathUp.setCurveTension(0.0f);
        containerPathUp.enableDebugShape(assetManager, this);
        containerPathUp.addListener(this);

        MotionEvent motionControl = new MotionEvent(container, containerPathUp);
        motionControl.setDirectionType(MotionEvent.Direction.None);
        motionControl.play();
        motionControl.dispose();
    }

    /**
     * Move back to the Agv.
     */
    private void createPathtoAgv() {
        newCranePathCounter = newCranePath.getNbWayPoints();
        newCranePath.setCurveTension(0.0f);
        newCranePath.enableDebugShape(assetManager, this);
        newCranePath.addListener(this);
        MotionEvent motionControl = new MotionEvent(this, newCranePath);
        motionControl.setDirectionType(MotionEvent.Direction.None);
        motionControl.play();
        motionControl.dispose();
    }

    /**
     * Put the container on the Agv.
     */
    private void putContainerOnAgv() {
        containerPathDownCounter = containerPathDown.getNbWayPoints();
        containerPathDown.setCurveTension(0.0f);
        containerPathDown.enableDebugShape(assetManager, this);
        containerPathDown.addListener(this);

        MotionEvent motionControl = new MotionEvent(container, containerPathDown);
        motionControl.setDirectionType(MotionEvent.Direction.None);
        motionControl.play();

        motionControl.dispose();
    }

    /**
     * Initialize waypoints depending on the type of platform.
     */
    private void initWaypoints() {
        switch (type) {
            case SEASHIP:
                cranePath.addWayPoint(getLocalTranslation());
                cranePath.addWayPoint(new Vector3f(getLocalTranslation().x, 0, (container.getWorldTranslation().z - getWorldTranslation().z) + getLocalTranslation().z));

                containerPathUp.addWayPoint(new Vector3f(container.getWorldTranslation().x - getWorldTranslation().x, 0, 0));
                containerPathUp.addWayPoint(new Vector3f(container.getWorldTranslation().x - getWorldTranslation().x, 22, 0));
                containerPathUp.addWayPoint(new Vector3f((agv.getWorldTranslation().x - getWorldTranslation().x), 22, 0));

                newCranePath.addWayPoint(new Vector3f(getLocalTranslation().x, 0, (container.getWorldTranslation().z - getWorldTranslation().z) + getLocalTranslation().z));
                newCranePath.addWayPoint(new Vector3f(getLocalTranslation().x, 0, (agv.getWorldTranslation().z - getWorldTranslation().z) + getLocalTranslation().z));

                containerPathDown.addWayPoint(new Vector3f((agv.getWorldTranslation().x - getWorldTranslation().x), 22, 0));
                containerPathDown.addWayPoint(new Vector3f((agv.getWorldTranslation().x - getWorldTranslation().x), 1, 0));


                break;
            case INLANDSHIP:
                cranePath.addWayPoint(getLocalTranslation());
                cranePath.addWayPoint(new Vector3f((container.getWorldTranslation().x - getWorldTranslation().x) + getLocalTranslation().x, 0, getLocalTranslation().z));

                containerPathUp.addWayPoint(new Vector3f((getWorldTranslation().z - container.getWorldTranslation().z), 0, 0));
                containerPathUp.addWayPoint(new Vector3f((getWorldTranslation().z - container.getWorldTranslation().z), 22, 0));
                containerPathUp.addWayPoint(new Vector3f((getWorldTranslation().z - agv.getWorldTranslation().z), 22, 0));

                newCranePath.addWayPoint(new Vector3f((container.getWorldTranslation().x - getWorldTranslation().x) + getLocalTranslation().x, 0, getLocalTranslation().z));
                newCranePath.addWayPoint(new Vector3f((agv.getWorldTranslation().x - getWorldTranslation().x) + getLocalTranslation().x, 0, getLocalTranslation().z));

                containerPathDown.addWayPoint(new Vector3f((getWorldTranslation().z - agv.getWorldTranslation().z), 22, 0));
                containerPathDown.addWayPoint(new Vector3f((getWorldTranslation().z - agv.getWorldTranslation().z), 1, 0));

                break;

        }
    }

    /**
     * Method gets automatically called everytime a waypoint is reached.
     *
     * @param motionControl motioncontrol of the path.
     * @param wayPointIndex Index of the current waypoint.
     */
    @Override
    public void onWayPointReach(MotionEvent motionControl, int wayPointIndex) {
        if (cranePathCounter == wayPointIndex + 1) {
            cranePath.clearWayPoints();
            System.out.println(container.getWorldTranslation());
            System.out.println(container.getLocalTranslation());
            System.out.println(this.getWorldTranslation());
            moveContainer();
            wayPointIndex = 0;
            cranePathCounter = 0;
            this.attachChild(container);
        }

        if (containerPathUpCounter == wayPointIndex + 1) {
            createPathtoAgv();
            wayPointIndex = 0;
            containerPathUpCounter = 0;
        }

        if (newCranePathCounter == wayPointIndex + 1) {
            putContainerOnAgv();
            wayPointIndex = 0;
            newCranePathCounter = 0;
        }

        if (containerPathDownCounter == wayPointIndex + 1) {
            detachChild(container);
            agv.attachChild(container);
            container.setLocalTranslation(0, 1, 0);
        }
    }
}
