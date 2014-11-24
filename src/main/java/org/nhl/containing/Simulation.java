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
import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import com.jme3.renderer.RenderManager;
import com.jme3.scene.Spatial;
import org.nhl.containing.cranes.DockingCrane;
import org.nhl.containing.vehicles.Train;
import org.nhl.containing.vehicles.Agv;
import org.nhl.containing.vehicles.Vehicle;

/**
 * test
 *
 * @author normenhansen
 */
public class Simulation extends SimpleApplication {

    private Agv avg;
    private MotionPath avgPath;
    private MotionEvent motionControl;
    private boolean debug;
    private Communication communication;

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
        Train train = new Train(assetManager, 10);
        train.setLocalTranslation(30, 0, 30);

        rootNode.attachChild(train);

        // Add a container to the scene.
        Container container = new Container(assetManager);
        container.setLocalTranslation(100, 0, 0);
        rootNode.attachChild(container);
        //Add an AVG and create its path
        avg = new Agv(assetManager);
        avg.setLocalTranslation(-230, 0, -180);
        rootNode.attachChild(avg);
        createPath();
        motionControl.setLoopMode(LoopMode.Loop);
        motionControl.play();

        // Platform for the scene.        
        Spatial platform = assetManager.loadModel("Models/platform/platform.j3o");
        platform.scale(20, 1, 20);
        rootNode.attachChild(platform);
    }

    void createPath() {
        avgPath = new MotionPath();
        avgPath.addWayPoint(new Vector3f(-230, 0, -180));
        avgPath.addWayPoint(new Vector3f(-230, 0, -80));
        avgPath.addWayPoint(new Vector3f(-230, 0, 20));
        avgPath.addWayPoint(new Vector3f(-230, 0, 125));
        avgPath.addWayPoint(new Vector3f(-130, 0, 125));
        avgPath.addWayPoint(new Vector3f(30, 0, 125));
        avgPath.addWayPoint(new Vector3f(80, 0, 125));
        avgPath.addWayPoint(new Vector3f(180, 0, 125));
        avgPath.addWayPoint(new Vector3f(290, 0, 125));
        avgPath.addWayPoint(new Vector3f(290, 0, 20));
        avgPath.addWayPoint(new Vector3f(290, 0, -80));
        avgPath.addWayPoint(new Vector3f(290, 0, -140));
        avgPath.addWayPoint(new Vector3f(190, 0, -140));
        avgPath.addWayPoint(new Vector3f(90, 0, -140));
        avgPath.addWayPoint(new Vector3f(0, 0, -140));
        avgPath.addWayPoint(new Vector3f(-90, 0, -140));
        avgPath.addWayPoint(new Vector3f(-190, 0, -140));
        motionControl = new MotionEvent(avg, avgPath);
        motionControl.setDirectionType(MotionEvent.Direction.PathAndRotation);
        motionControl.setRotation(new Quaternion().fromAngleNormalAxis(0, Vector3f.UNIT_Y));
        motionControl.setInitialDuration(10f);
        motionControl.setSpeed(0.2f);
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
                    if (debug) {
                        debug = false;
                        avgPath.disableDebugShape();
                    } else {
                        debug = true;
                        avgPath.enableDebugShape(assetManager, rootNode);
                    }
                }
            }
        };
        inputManager.addListener(acl, "debugmode");
    }

    public void readyCheck() {
    }
}
