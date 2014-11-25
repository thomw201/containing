package org.nhl.containing;

import com.jme3.animation.LoopMode;
import com.jme3.app.SimpleApplication;
import com.jme3.cinematic.MotionPath;
import com.jme3.cinematic.events.MotionEvent;
import com.jme3.input.KeyInput;
import com.jme3.input.controls.ActionListener;
import com.jme3.input.controls.KeyTrigger;
import com.jme3.light.DirectionalLight;
import com.jme3.math.ColorRGBA;
import com.jme3.math.FastMath;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import com.jme3.renderer.RenderManager;
import com.jme3.scene.Spatial;
import org.nhl.containing.cranes.DockingCrane;
import org.nhl.containing.vehicles.Train;
import org.nhl.containing.vehicles.Agv;
import org.nhl.containing.vehicles.Vehicle;
import org.nhl.containing.vehicles.*;

/**
 * test
 *
 * @author normenhansen
 */
public class Simulation extends SimpleApplication {

    private Agv agv;
    private MotionPath agvPath;
    private MotionEvent motionControl;
    private boolean debug;
    private Communication communication;
    private Train train;
    private Boat boat;
    private Lorry lorry;
    public static final Quaternion YAW180   = new Quaternion().fromAngleAxis(FastMath.PI  ,   new Vector3f(0,1,0));

    public Simulation() {
        communication = new Communication();
    }

    @Override
    public void simpleInitApp() {
        cam();
        scene();
        userInput();
        communication.Start();
    }

    /**
     * Camera settings of the scene.
     */
    public void cam() {
        viewPort.setBackgroundColor(ColorRGBA.Blue);
        cam.setLocation(new Vector3f(0, 5, 0));
        flyCam.setMoveSpeed(50);
    }

    /**
     * Sends an OK-type message to the controller of the object that is ready
     * for a new task -SUBJECT TO CHANGE, MAYBE SUPERCLASS OBJECT IN THE
     * FUTURE!-
     *
     * @param veh -SUBJECT TO CHANGE, MAYBE SUPERCLASS OBJECT IN THE FUTURE!-
     */
    public void sendOkMessage(Vehicle veh) {
        String message = "<OK><OBJECT>" + veh.getName() + "</OBJECT><OBJECTID>" + veh.getId() + "</OBJECTID></OK>";
        communication.sendMessage(message);
    }

    /*
     * Method for initializing the scene.
     */
    public void scene() {

        // Light pointing diagonal from the top right to the bottom left.
        DirectionalLight light = new DirectionalLight();
        light.setDirection((new Vector3f(-0.5f, -0.5f, -0.5f)).normalizeLocal());
        light.setColor(ColorRGBA.White);
        rootNode.addLight(light);

        // A second light pointing diagonal from the bottom left to the top right.
        DirectionalLight secondLight = new DirectionalLight();
        secondLight.setDirection((new Vector3f(0.5f, 0.5f, 0.5f)).normalizeLocal());
        secondLight.setColor(ColorRGBA.White);
        rootNode.addLight(secondLight);

        // Add dockingcranes to the scene.
        DockingCrane dockingCrane = new DockingCrane(assetManager);
        dockingCrane.setLocalTranslation(10, 0, 0);
        rootNode.attachChild(dockingCrane);
        DockingCrane secondDockingCrane = new DockingCrane(assetManager);
        secondDockingCrane.setLocalTranslation(10, 0, 30);
        rootNode.attachChild(secondDockingCrane);

        // Add a train to the scene.
        train = new Train(assetManager, 10);
        rootNode.attachChild(train);
        //call method that makes the train arrive
        train.move(true);
        // Add a container to the scene.
        Container container = new Container(assetManager);
        container.setLocalTranslation(100, 0, 0);
        rootNode.attachChild(container);
        //Add an AGV and create its path
        agv = new Agv(assetManager);
        agv.setLocalTranslation(-230, 0, -180);
        rootNode.attachChild(agv);
        createAGVPath();
        //Add a lorry
        lorry = new Lorry(assetManager);
        rootNode.attachChild(lorry);
        lorry.move(true);
        //Add a boat
        boat = new Boat(assetManager);
        rootNode.attachChild(boat);
        boat.scale(1, 1, 0.5f);
        boat.move(true);
        
        // Platform for the scene.        
        Spatial platform = assetManager.loadModel("Models/platform/platform.j3o");
        platform.scale(20, 1, 20);
        rootNode.attachChild(platform);
    }
    /**
     * This method creates waypoints on the AGV roads and lets an AGV drive over them
     */
    void createAGVPath() {
        MotionPath agvPath = new MotionPath();
        MotionEvent agvmotionControl = new MotionEvent(agv, agvPath);
        //Create the AGV waypoints
        agvPath.addWayPoint(new Vector3f(-230, 0, -180));
        agvPath.addWayPoint(new Vector3f(-230, 0, -80));
        agvPath.addWayPoint(new Vector3f(-230, 0, 20));
        agvPath.addWayPoint(new Vector3f(-230, 0, 125));
        agvPath.addWayPoint(new Vector3f(-130, 0, 125));
        agvPath.addWayPoint(new Vector3f(30, 0, 125));
        agvPath.addWayPoint(new Vector3f(80, 0, 125));
        agvPath.addWayPoint(new Vector3f(180, 0, 125));
        agvPath.addWayPoint(new Vector3f(290, 0, 125));
        agvPath.addWayPoint(new Vector3f(290, 0, 20));
        agvPath.addWayPoint(new Vector3f(290, 0, -80));
        agvPath.addWayPoint(new Vector3f(290, 0, -140));
        agvPath.addWayPoint(new Vector3f(190, 0, -140));
        agvPath.addWayPoint(new Vector3f(90, 0, -140));
        agvPath.addWayPoint(new Vector3f(0, 0, -140));
        agvPath.addWayPoint(new Vector3f(-90, 0, -140));
        agvPath.addWayPoint(new Vector3f(-190, 0, -140));
        // set the speed and direction of the AGV using motioncontrol
        agvmotionControl.setDirectionType(MotionEvent.Direction.PathAndRotation);
        agvmotionControl.setRotation(new Quaternion().fromAngleNormalAxis(0, Vector3f.UNIT_Y));
        agvmotionControl.setInitialDuration(10f);
        agvmotionControl.setSpeed(0.2f);
        //make the vehicles start moving
        agvmotionControl.setLoopMode(LoopMode.Loop);
        agvmotionControl.play();
        //make waypoints visible
        //agvPath.disableDebugShape();
    }
    
    @Override
    public void simpleUpdate(float tpf) {
        //TODO: add update code
    }

    @Override
    public void simpleRender(RenderManager rm) {
        //TODO: add render code
    }

    public void getData() {
    }

    public void userInput() {
        inputManager.addMapping("debugmode", new KeyTrigger(KeyInput.KEY_P));
        ActionListener acl = new ActionListener() {
            public void onAction(String name, boolean keyPressed, float tpf) {
                if (name.equals("debugmode") && keyPressed) {
                    if (!debug) {
                        debug = false;
                        //testing train code
                        train.move(debug);
                        boat.move(debug);
                        lorry.move(debug);
                    } else {
                        debug = true;
                        //testing train code
                        train.move(debug);
                        boat.move(debug);
                        lorry.move(debug);
                    }
                }
            }
        };
        inputManager.addListener(acl, "debugmode");
    }

    public void readyCheck() {
    }
}
