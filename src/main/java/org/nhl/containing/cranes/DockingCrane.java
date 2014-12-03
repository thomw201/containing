package org.nhl.containing.cranes;

import com.jme3.asset.AssetManager;
import com.jme3.cinematic.MotionPath;
import com.jme3.cinematic.events.MotionEvent;
import com.jme3.math.Vector3f;
import com.jme3.scene.Spatial;
import org.nhl.containing.Container;
import org.nhl.containing.vehicles.Agv;

/**
 * @author Jeroen
 */
public class DockingCrane extends Crane {

    private AssetManager assetManager;
    private Agv agv;
    private Container container;
    private MotionPath containerPathUp;
    private MotionPath containerPathDown;
    private MotionPath cranePath;
    private MotionPath newCranePath;
    private int cranePathCounter;
    private int newCranePathCounter;
    private int containerPathUpCounter;
    private int containerPathDownCounter;

    public DockingCrane(AssetManager assetManager) {
        this.assetManager = assetManager;
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
     * Let the crane move to the container's position. And Put the container on the Agv.
     * @param container Request a container.
     * @param agv Agv that needs the container.
     */
    public void boatToAgv(Container container, Agv agv){
        this.container = container;
        this.agv = agv;

        cranePath = new MotionPath();
        cranePath.addWayPoint(getLocalTranslation());
        cranePath.addWayPoint(new Vector3f(getLocalTranslation().x, 0, (container.getWorldTranslation().z - getWorldTranslation().z) + getLocalTranslation().z));
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
        containerPathUp = new MotionPath();
        containerPathUp.addWayPoint(new Vector3f(container.getWorldTranslation().x - this.getWorldTranslation().x, 0, 0));
        containerPathUp.addWayPoint(new Vector3f(container.getWorldTranslation().x - this.getWorldTranslation().x, 22, 0));
        containerPathUp.addWayPoint(new Vector3f((agv.getWorldTranslation().x - getWorldTranslation().x), 22, 0));
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
        newCranePath = new MotionPath();
        newCranePath.addWayPoint(getLocalTranslation());
        newCranePath.addWayPoint(new Vector3f(getLocalTranslation().x, 0, (agv.getWorldTranslation().z - getWorldTranslation().z) + getLocalTranslation().z));
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
        container.rotate(container.getWorldRotation());
        containerPathDown = new MotionPath();
        containerPathDown.addWayPoint(new Vector3f((agv.getWorldTranslation().x - getWorldTranslation().x), 22, 0));
        containerPathDown.addWayPoint(new Vector3f((agv.getWorldTranslation().x - getWorldTranslation().x), 1, 0));
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
     * Method gets automatically called everytime a waypoint is reached.
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
            createPathtoAgv();
            wayPointIndex = 0;
            containerPathUpCounter = 0;
        }
        
        if (newCranePathCounter == wayPointIndex + 1) {
            putContainerOnAgv();
            wayPointIndex = 0;
            newCranePathCounter = 0;
        }
        
        if (containerPathDownCounter == wayPointIndex + 1){
            detachChild(container);
            agv.attachChild(container);
            container.setLocalTranslation(0, 1, 0);
        }
    }
}
