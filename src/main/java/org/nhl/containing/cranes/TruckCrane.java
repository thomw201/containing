package org.nhl.containing.cranes;

import com.jme3.asset.AssetManager;
import com.jme3.cinematic.MotionPath;
import com.jme3.cinematic.events.MotionEvent;
import com.jme3.math.Vector3f;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import org.nhl.containing.vehicles.Agv;
import org.nhl.containing.vehicles.Lorry;

public class TruckCrane extends Crane {

    private AssetManager assetManager;
    private Agv agv;
    private Lorry lorry;
    private Spatial container;
    private MotionPath containerPathUp = new MotionPath();
    private MotionPath containerPathDown = new MotionPath();
    private MotionPath cranePath = new MotionPath();
    private MotionPath newCranePath = new MotionPath();
    private MotionPath cranePathBack = new MotionPath();
    private int cranePathCounter;
    private int newCranePathCounter;
    private int containerPathUpCounter;
    private int containerPathDownCounter;
    private int cranePathBackCounter;
    private CraneDirection direction;

    public TruckCrane(AssetManager assetManager) {
        this.assetManager = assetManager;
        this.name = "TruckCrane";
        initTruckCrane();
    }

    /**
     * Initialize a truck crane.
     */
    private void initTruckCrane() {

        // Load a model.
        Node truckCrane = (Node) assetManager.loadModel("Models/high/crane/truckcrane/crane.j3o");
        this.attachChild(truckCrane);
    }

    /**
     * Let the crane move to the container's position. And put the container on
     * the Agv.
     *
     * @param container Request a container.
     * @param agv Agv that needs the container.
     */
    public void truckToAgv(Spatial container, Agv agv) {
        this.container = container;
        this.agv = agv;
        this.direction = CraneDirection.LORRYTOAGV;
        initWaypoints();
        moveToContainer();
    }

    /**
     * Let the crane move to the container's position. And put the container on
     * the lorry.
     *
     * @param container
     * @param lorry
     */
    public void agvToTruck(Spatial container, Lorry lorry) {
        this.container = container;
        this.lorry = lorry;
        this.direction = CraneDirection.AGVTOLORRY;
        initWaypoints();
        moveToContainer();
    }

    /**
     * Move the crane to the container.
     */
    private void moveToContainer() {
        cranePathCounter = cranePath.getNbWayPoints();
        cranePath.setCurveTension(0.0f);
        cranePath.enableDebugShape(assetManager, this);
        cranePath.addListener(this);

        MotionEvent motionControl = new MotionEvent(this, cranePath);
        motionControl.setDirectionType(MotionEvent.Direction.None);
        motionControl.setSpeed(speed);
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
        motionControl.setSpeed(speed);
        motionControl.play();
        motionControl.dispose();
    }

    /**
     * Move back to the vehicle.
     */
    private void createPathToVehicle() {
        newCranePathCounter = newCranePath.getNbWayPoints();
        newCranePath.setCurveTension(0.0f);
        newCranePath.enableDebugShape(assetManager, this);
        newCranePath.addListener(this);
        MotionEvent motionControl = new MotionEvent(this, newCranePath);
        motionControl.setDirectionType(MotionEvent.Direction.None);
        motionControl.setSpeed(speed);
        motionControl.play();
        motionControl.dispose();
    }

    /**
     * Put the container on the vehicle.
     */
    private void putContainerOnVehicle() {
        container.rotate(container.getWorldRotation());

        containerPathDownCounter = containerPathDown.getNbWayPoints();
        containerPathDown.setCurveTension(0.0f);
        containerPathDown.enableDebugShape(assetManager, this);
        containerPathDown.addListener(this);

        MotionEvent motionControl = new MotionEvent(container, containerPathDown);
        motionControl.setDirectionType(MotionEvent.Direction.None);
        motionControl.setSpeed(speed);
        motionControl.play();

        motionControl.dispose();
    }

    /**
     * Initialize the waypoints depending on the requested direction.
     */
    private void initWaypoints() {
        switch (direction) {
            case AGVTOLORRY:
                cranePath.addWayPoint(getLocalTranslation());
                cranePath.addWayPoint(new Vector3f(getLocalTranslation().x, 0, (container.getWorldTranslation().z - getWorldTranslation().z) + getLocalTranslation().z));
                containerPathUp.addWayPoint(new Vector3f(container.getWorldTranslation().x - this.getWorldTranslation().x, 1, 0));
                containerPathUp.addWayPoint(new Vector3f(container.getWorldTranslation().x - this.getWorldTranslation().x, 7, 0));
                newCranePath.addWayPoint(new Vector3f(getLocalTranslation().x, 0, (container.getWorldTranslation().z - getWorldTranslation().z) + getLocalTranslation().z));
                newCranePath.addWayPoint(new Vector3f(getLocalTranslation().x, 0, (lorry.getWorldTranslation().z - getWorldTranslation().z) + getLocalTranslation().z + 11));
                containerPathDown.addWayPoint(new Vector3f((lorry.getWorldTranslation().x - getWorldTranslation().x), 7, 0));
                containerPathDown.addWayPoint(new Vector3f((lorry.getWorldTranslation().x - getWorldTranslation().x), 1, 0));
                cranePathBack.addWayPoint(new Vector3f(getLocalTranslation().x, 0, (lorry.getWorldTranslation().z - getWorldTranslation().z) + getLocalTranslation().z + 11));
                cranePathBack.addWayPoint(getLocalTranslation());
                break;
            case LORRYTOAGV:
                cranePath.addWayPoint(getLocalTranslation());
                cranePath.addWayPoint(new Vector3f(getLocalTranslation().x, 0, (container.getWorldTranslation().z - getWorldTranslation().z) + getLocalTranslation().z));
                containerPathUp.addWayPoint(new Vector3f(container.getWorldTranslation().x - this.getWorldTranslation().x, 1, 0));
                containerPathUp.addWayPoint(new Vector3f(container.getWorldTranslation().x - this.getWorldTranslation().x, 7, 0));
                newCranePath.addWayPoint(new Vector3f(getLocalTranslation().x, 0, (container.getWorldTranslation().z - getWorldTranslation().z) + getLocalTranslation().z));
                newCranePath.addWayPoint(new Vector3f(getLocalTranslation().x, 0, (agv.getWorldTranslation().z - getWorldTranslation().z) + getLocalTranslation().z));
                containerPathDown.addWayPoint(new Vector3f((agv.getWorldTranslation().x - getWorldTranslation().x), 7, 0));
                containerPathDown.addWayPoint(new Vector3f((agv.getWorldTranslation().x - getWorldTranslation().x), 1, 0));
                cranePathBack.addWayPoint(new Vector3f(getLocalTranslation().x, 0, (agv.getWorldTranslation().z - getWorldTranslation().z) + getLocalTranslation().z));
                cranePathBack.addWayPoint(getLocalTranslation());
                break;

        }
    }

    /**
     * Move back to the start location.
     */
    private void returnToStart() {
        cranePathBackCounter = cranePathBack.getNbWayPoints();
        cranePathBack.setCurveTension(0.0f);
        cranePathBack.enableDebugShape(assetManager, this);
        cranePathBack.addListener(this);
        MotionEvent motionControl = new MotionEvent(this, cranePathBack);
        motionControl.setDirectionType(MotionEvent.Direction.None);
        motionControl.play();
        motionControl.dispose();
    }

    /**
     * Completely reset the crane.
     */
    private void resetCrane() {
        containerPathUp.clearWayPoints();
        containerPathDown.clearWayPoints();
        cranePath.clearWayPoints();
        newCranePath.clearWayPoints();
        cranePathBack.clearWayPoints();
        cranePathCounter = 0;
        newCranePathCounter = 0;
        containerPathUpCounter = 0;
        containerPathDownCounter = 0;
        cranePathBackCounter = 0;
        containerPathUp = new MotionPath();
        containerPathDown = new MotionPath();
        cranePath = new MotionPath();
        newCranePath = new MotionPath();
        cranePathBack = new MotionPath();
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
            moveContainer();
            wayPointIndex = 0;
            cranePathCounter = 0;
            this.attachChild(container);

        }

        if (containerPathUpCounter == wayPointIndex + 1) {
            createPathToVehicle();
            wayPointIndex = 0;
            containerPathUpCounter = 0;

        }

        if (newCranePathCounter == wayPointIndex + 1) {
            putContainerOnVehicle();
            wayPointIndex = 0;
            newCranePathCounter = 0;
        }

        if (containerPathDownCounter == wayPointIndex + 1) {

            switch (direction) {
                case LORRYTOAGV:
                    detachChild(container);
                    agv.attachChild(container);
                    container.setLocalTranslation(0, 1, 0);
                    break;
                case AGVTOLORRY:
                    detachChild(container);
                    lorry.attachChild(container);
                    container.setLocalTranslation(0, 1, 11);
                    break;
            }
            containerPathDownCounter = 0;
            wayPointIndex = 0;
            returnToStart();

            detachChild(container);
            agv.attachChild(container);
            container.setLocalTranslation(0, 1, 0);

        }

        if (cranePathBackCounter == wayPointIndex + 1) {
            wayPointIndex = 0;
            resetCrane();
            setArrived(true);
        }
    }

    private enum CraneDirection {

        AGVTOLORRY, LORRYTOAGV
    }
}
