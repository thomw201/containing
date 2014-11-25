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
import java.util.ArrayList;
import java.util.List;
import org.nhl.containing.areas.BoatArea;
import org.nhl.containing.areas.StorageArea;
import org.nhl.containing.areas.TrainArea;
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

    private List<Container> totalContainerList;
    private List<Container> trainContainerList;
    private List<Container> seashipContainerList;
    private List<Container> inlandshipContainerList;
    //TIJDELIJK
    private int locationInt = 0;
    private Agv agv;
    private Communication communication;
    private Train train;
    private Boat boat;
    private Lorry lorry;
    private Container c;
    private boolean debug;
    public static final Quaternion YAW180 = new Quaternion().fromAngleAxis(FastMath.PI, new Vector3f(0, 1, 0));

    public Simulation() {
        communication = new Communication();
        totalContainerList = new ArrayList<Container>();
        trainContainerList = new ArrayList<Container>();
        seashipContainerList = new ArrayList<Container>();
        inlandshipContainerList = new ArrayList<Container>();
    }

    @Override
    public void simpleInitApp() {
        cam();
        scene();
        userInput();
        createContainers();
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

        // Add the TrainArea.
        TrainArea trainArea = new TrainArea(assetManager, 4);
        trainArea.setLocalTranslation(-160, 0, -180);
        rootNode.attachChild(trainArea);

        // Add the BoatArea (Sea).
        BoatArea boatArea = new BoatArea(assetManager, 10);
        boatArea.setLocalTranslation(-325, 0, -100);
        rootNode.attachChild(boatArea);

        // Add the inlandBoatArea.
        BoatArea inlandBoatArea = new BoatArea(assetManager, 8);
        inlandBoatArea.rotate(0, (float) (0.5 * Math.PI), 0);
        inlandBoatArea.scale(0.8f, 0.8f, 0.8f);
        inlandBoatArea.setLocalTranslation(-240, 0, 220);
        rootNode.attachChild(inlandBoatArea);

        // Add the StorageArea for boat containers.
        StorageArea boatStorageArea = new StorageArea(assetManager, 4);
        boatStorageArea.setLocalTranslation(-200, 0, -130);
        rootNode.attachChild(boatStorageArea);

        // Add the StorageArea for train containers.
        StorageArea trainStorageArea = new StorageArea(assetManager, 4);
        trainStorageArea.setLocalTranslation(-20, 0, -130);
        rootNode.attachChild(trainStorageArea);

        // Add the StorageArea for train containers.
        StorageArea lorryStorageArea = new StorageArea(assetManager, 4);
        lorryStorageArea.setLocalTranslation(150, 0, -130);
        rootNode.attachChild(lorryStorageArea);

        // Platform for the scene.        
        Spatial platform = assetManager.loadModel("Models/platform/platform.j3o");
        platform.scale(20, 1, 20);
        rootNode.attachChild(platform);
    }

    /**
     * This methode will process all incomming create commands.
     *
     * Create containers and add them to a list<container>. When the
     * list<container> == maxValueContainer (so max count of the sended
     * commands) Then take apart the List<Container> and divide them to there
     * TransportList. When the List<container> == empty then create the
     * vehicles.
     */
    private void createObject() {
        if (communication.getMaxValueContainers() != 0) {
            if (communication.getMaxValueContainers() == totalContainerList.size()) {
                for (Container c : totalContainerList) {
                    if (c.getTransportType().equals("binnenschip")) {
                        inlandshipContainerList.add(c);
                    }
                    if (c.getTransportType().equals("zeeschip")) {
                        seashipContainerList.add(c);
                    }
                    if (c.getTransportType().equals("trein")) {
                        trainContainerList.add(c);
                    }
                    if (c.getTransportType().equals("vrachtauto")) {
                        // Lorry can only contain 1 container, so has to create immediately.
                        Lorry l = new Lorry(assetManager);
                        c.attachChild(l);
                    }
                }
                for (Container c : inlandshipContainerList) {
                    Boat b = new Boat(assetManager, Boat.Size.INLANDSHIP);
                }
                for (Container c : seashipContainerList) {
                    Boat b = new Boat(assetManager, Boat.Size.SEASHIP);
                }
                for (Container c : trainContainerList) {
                    Train t = new Train(assetManager, trainContainerList.size(), c);
                    rootNode.attachChild(t);
                }
            }
        } else {
            c = new Container(assetManager, communication.getContainerOwner(),
                    communication.getContainerIso(), communication.getTransportType(), new Vector3f(0, locationInt += 10, 0));
            totalContainerList.add(c);
        }
    }

    private void createContainers() {
        c = new Container(assetManager, "Coca Cola", "8-9912", "trein", new Vector3f(0, locationInt += 10, 0));
        totalContainerList.add(c);
        c = new Container(assetManager, "Coca Cola", "8-9612", "vrachtauto", new Vector3f(0, locationInt += 10, 0));
        totalContainerList.add(c);
        c = new Container(assetManager, "Coca Cola", "8-9912", "zeeschip", new Vector3f(0, locationInt += 10, 0));
        totalContainerList.add(c);
        c = new Container(assetManager, "Coca Cola", "8-9912", "binnenschip", new Vector3f(0, locationInt += 10, 0));
        totalContainerList.add(c);
        createObject();
    }

    /**
     * This method creates waypoints on the AGV roads and lets an AGV drive over
     * them
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

    @Override
    public void destroy() {
        super.destroy();
        communication.stopClient();
    }
}
