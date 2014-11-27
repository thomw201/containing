package org.nhl.containing.cranes;

import com.jme3.asset.AssetManager;
import com.jme3.cinematic.MotionPath;
import com.jme3.cinematic.MotionPathListener;
import com.jme3.cinematic.events.MotionEvent;
import com.jme3.cinematic.events.MotionTrack;
import com.jme3.math.FastMath;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import com.jme3.scene.Spatial;
import org.nhl.containing.Container;
import org.nhl.containing.vehicles.Agv;

/**
 *
 * @author Jeroen
 */
public class TrainCrane extends Crane implements MotionPathListener{

    private AssetManager assetManager;
    private static final Quaternion YAW090 = new Quaternion().fromAngleAxis(FastMath.PI / 2, new Vector3f(0, 1, 0));
    Agv agv;
    private Container container;
    MotionPath path;
    TrainCrane tc = this;

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
     * Move a container to the Agv.
     */
    public void moveContainer(Container container, Agv agv) {
        this.container = container;
        this.agv = agv;
        tc.attachChild(container);
        container.rotate(0, (float) Math.PI / 2, 0);
        path = new MotionPath();
        path.addWayPoint(new Vector3f(0, 1, 0));
        path.addWayPoint(new Vector3f(0, 5, 0));
        path.addWayPoint(new Vector3f(0, 5, 6));
        path.addWayPoint(new Vector3f(0, 1, 6));
        path.setCurveTension(0.0f);
        path.enableDebugShape(assetManager, this);
        path.addListener(this);
        MotionEvent motionControl = new MotionEvent(tc.getChild(1), path);
        motionControl.setDirectionType(MotionEvent.Direction.None);
        motionControl.play();
        
    }

    public void onWayPointReach(MotionEvent motionControl, int wayPointIndex) {
                if(path.getNbWayPoints() ==  wayPointIndex + 1){
                    tc.detachChild(tc.getChild(1));
                    agv.attachChild(container);
                    container.rotate(0, (float) Math.PI / 2, 0);
                    container.setLocalTranslation(0,1,0);
                    agv.setLocalTranslation(30, 0, 0);
                }
            }

}