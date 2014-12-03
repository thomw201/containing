package org.nhl.containing.cranes;

import com.jme3.asset.AssetManager;
import com.jme3.cinematic.MotionPath;
import com.jme3.cinematic.events.MotionEvent;
import com.jme3.math.FastMath;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import com.jme3.scene.Spatial;
import org.nhl.containing.Container;
import org.nhl.containing.vehicles.Agv;

/**
 * @author Jeroen
 */
public class TrainCrane extends Crane {

    private static final Quaternion YAW090 = new Quaternion().fromAngleAxis(FastMath.PI / 2, new Vector3f(0, 1, 0));
    private AssetManager assetManager;
    private Agv agv;
    private Container container;
    private MotionPath containerPath;
    private MotionPath cranePath;

    public TrainCrane(AssetManager assetManager) {
        this.assetManager = assetManager;
        initTrainCrane();
    }

    /**
     * Initialize a train crane.
     */
    private void initTrainCrane() {

        // Load a model.
        Spatial trainCrane = assetManager.loadModel("Models/high/crane/traincrane/crane.j3o");
        trainCrane.setLocalRotation(YAW090);
        this.attachChild(trainCrane);
    }

    /**
     * Let the crane move to the container's position. And Put the container on
     * the Agv.
     *
     * @param container Request a container.
     * @param agv Agv that needs the container.
     */
    public void trainToAgv(Container container, Agv agv) {

        this.container = container;
        this.agv = agv;

        // Crane movement.
        cranePath = new MotionPath();
        cranePath.addWayPoint(getLocalTranslation());
        cranePath.addWayPoint(new Vector3f((container.getWorldTranslation().x - getWorldTranslation().x) + getLocalTranslation().x, 0, 0));
        cranePath.setCurveTension(0.0f);
        cranePath.enableDebugShape(assetManager, this);
        cranePath.addListener(this);

        MotionEvent motionControl = new MotionEvent(this, cranePath);
        motionControl.setDirectionType(MotionEvent.Direction.None);
        motionControl.play();
        motionControl.dispose();
    }

    /**
     * Move a container to the Agv.
     */
    private void moveContainer() {
        attachChild(container);
        container.rotate(0, (float) Math.PI / 2, 0);
        containerPath = new MotionPath();
        containerPath.addWayPoint(new Vector3f(0, 1, 0));
        containerPath.addWayPoint(new Vector3f(0, 5, 0));
        containerPath.addWayPoint(new Vector3f(0, 5, 6));
        containerPath.addWayPoint(new Vector3f(0, 1, 6));
        containerPath.setCurveTension(0.0f);
        containerPath.enableDebugShape(assetManager, this);
        containerPath.addListener(this);
        
        MotionEvent motionControl = new MotionEvent(getChild(2), containerPath);
        motionControl.setDirectionType(MotionEvent.Direction.None);
        motionControl.play();
    }

    /**
     * Method gets automatically called everytime a waypoint is reached.
     * @param motionControl motioncontrol of the path.
     * @param wayPointIndex Index of the current waypoint.
     */
    @Override
    public void onWayPointReach(MotionEvent motionControl, int wayPointIndex) {
        if (cranePath.getNbWayPoints() == wayPointIndex + 1) {
            cranePath.clearWayPoints();
            moveContainer();
        }

        if (containerPath.getNbWayPoints() == wayPointIndex + 1) {
            detachChild(getChild(1));
            agv.attachChild(container);
            container.rotate(0, (float) Math.PI / 2, 0);
            container.setLocalTranslation(0, 1, 0);
        }
        
    }
}
