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
import org.nhl.containing.areas.BoatArea;
import org.nhl.containing.areas.StorageArea;
import org.nhl.containing.areas.TrainArea;
import org.nhl.containing.communication.Client;
import org.nhl.containing.vehicles.*;
import org.nhl.containing.vehicles.Agv;
import org.nhl.containing.vehicles.Train;
import org.nhl.containing.vehicles.Vehicle;

import java.util.ArrayList;
import org.nhl.containing.communication.Message;
import org.nhl.containing.communication.Message.Command;

/**
 * test
 *
 * @author normenhansen
 */
public class Simulation extends SimpleApplication {

    private ArrayList<Container> totalContainerList;
    private ArrayList<Container> trainContainerList;
    private ArrayList<Container> seashipContainerList;
    private ArrayList<Container> inlandshipContainerList;
    private TrainArea trainArea;
    private BoatArea boatArea;
    private BoatArea inlandBoatArea;
    private StorageArea boatStorageArea;
    private StorageArea trainStorageArea;
    private StorageArea lorryStorageArea;
    private Agv agv;
    private Client client;
    private Train train;
    private Boat boat;
    private Lorry lorry;
    private Container c;
    private boolean debug;

    public Simulation() {
        client = new Client();
        totalContainerList = new ArrayList<Container>();
        trainContainerList = new ArrayList<Container>();
        seashipContainerList = new ArrayList<Container>();
        inlandshipContainerList = new ArrayList<Container>();
    }

    @Override
    public void simpleInitApp() {
        initCam();
        initUserInput();
        initScene();
        Thread clientThread = new Thread(client);
        clientThread.setName("ClientThread");
        clientThread.start();
    }

    @Override
    public void simpleUpdate(float tpf) {
        createObject();
    }

    @Override
    public void simpleRender(RenderManager rm) {
        //TODO: add render code
    }

    @Override
    public void destroy() {
        super.destroy();
        client.stop();
    }

    /**
     * Sends an OK-type message to the controller of the object that is ready
     * for a new task -SUBJECT TO CHANGE, MAYBE SUPERCLASS OBJECT IN THE
     * FUTURE!-
     *
     * @param veh -SUBJECT TO CHANGE, MAYBE SUPERCLASS OBJECT IN THE FUTURE!-
     */
    private void sendOkMessage(Vehicle veh) {
        String message = "<OK><OBJECT>" + veh.getName() + "</OBJECT><OBJECTID>" + veh.getId() + "</OBJECTID></OK>";
        client.writeMessage(message);
    }

    /**
     * This method will process all incomming create commands.
     * <p/>
     * Create containers and add them to a list<container>. When the
     * list<container> == maxValueContainer (so max count of the sended
     * commands) Then take apart the List<Container> and divide them to there
     * TransportList. When the List<container> == empty then create the
     * vehicles.
     */
    private void createObject() {
        ArrayList<Message> incomingMessages = client.getMessages();
        if (incomingMessages != null) {
            for (Message msg : incomingMessages) {
                if (msg.getCommand() == Command.Create) {
                    
                    //Creates a container from the incoming message
                    c = new Container(assetManager, msg.getContainerOwner(),
                            msg.getContainerIso(), msg.getTransportType(),
                            msg.getxLoc(), msg.getyLoc(), msg.getzLoc());
                    totalContainerList.add(c);
                    
                    //Places the newly created container on the right vehicle
                        for (Container con : totalContainerList) {
                            if (con.getTransportType().equals("binnenschip")) {
                                inlandshipContainerList.add(con);
                            }
                            if (con.getTransportType().equals("zeeschip")) {
                                seashipContainerList.add(con);
                            }
                            if (con.getTransportType().equals("trein")) {
                                trainContainerList.add(con);
                            }
                            if (con.getTransportType().equals("vrachtauto")) {
                                // Lorry can only contain 1 container, so has to create immediately.
                                Lorry l = new Lorry(assetManager, con);
                                l.move(true);
                                rootNode.attachChild(l);
                            }
                        }
                        if (!inlandshipContainerList.isEmpty()) {
                            Boat b = new Boat(assetManager, Boat.ShipSize.INLANDSHIP, inlandshipContainerList, seashipContainerList);
                            b.move(true);
                            rootNode.attachChild(b);
                        }
                        if (!seashipContainerList.isEmpty()) {
                            Boat b = new Boat(assetManager, Boat.ShipSize.SEASHIP, inlandshipContainerList, seashipContainerList);
                            b.move(true);
                            rootNode.attachChild(b);
                        }
                        if (!trainContainerList.isEmpty()) {
                            Train t = new Train(assetManager, trainContainerList);
                            t.move(true);
                            rootNode.attachChild(t);
                        }
                }
            }
        }
    }

    /**
     * Voor het testen.
     */
    private void createContainers() {
        for (int i = 0; i < 29; i++) {
            c = new Container(assetManager, "Coca Cola", "8-9912", "trein", 0, 0, 0);
            totalContainerList.add(c);
        }
        c = new Container(assetManager, "Coca Cola", "8-9612", "vrachtauto", 0, 0, 0);
        totalContainerList.add(c);
        for (int i = 0; i < 41; i++) {
            c = new Container(assetManager, "Coca Cola", "8-9912", "zeeschip", 0, 0, 0);
            totalContainerList.add(c);
        }
        for (int i = 0; i < 17; i++) {
            c = new Container(assetManager, "Coca Cola", "8-9912", "binnenschip", 0, 0, 0);
            totalContainerList.add(c);
        }
        createObject();
    }

    /**
     * This method creates waypoints on the AGV roads and lets an AGV drive over
     * them
     */
    private void createAGVPath() {
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

    public void getData() {
    }

    public void readyCheck() {
    }

    /**
     * Camera settings of the scene.
     */
    private void initCam() {
        viewPort.setBackgroundColor(ColorRGBA.Blue);
        cam.setLocation(new Vector3f(0, 5, 0));
        flyCam.setMoveSpeed(50);
    }

    /*
     * Method for initializing the scene.
     */
    private void initScene() {
        initLighting();
        initAreas();
        initPlatform();
    }

    private void initLighting() {
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
    }

    private void initAreas() {
        // Add the TrainArea.
        trainArea = new TrainArea(assetManager, 4);
        trainArea.setLocalTranslation(-160, 0, -180);
        rootNode.attachChild(trainArea);

        // Add the BoatArea (Sea).
        boatArea = new BoatArea(assetManager, 10);
        boatArea.setLocalTranslation(-325, 0, -100);
        rootNode.attachChild(boatArea);

        // Add the inlandBoatArea.
        inlandBoatArea = new BoatArea(assetManager, 8);
        inlandBoatArea.rotate(0, (float) (0.5 * Math.PI), 0);
        inlandBoatArea.scale(0.8f, 0.8f, 0.8f);
        inlandBoatArea.setLocalTranslation(-240, 0, 220);
        rootNode.attachChild(inlandBoatArea);

        // Add the StorageArea for boat containers.
        boatStorageArea = new StorageArea(assetManager, 4);
        boatStorageArea.setLocalTranslation(-200, 0, -130);
        rootNode.attachChild(boatStorageArea);

        // Add the StorageArea for train containers.
        trainStorageArea = new StorageArea(assetManager, 4);
        trainStorageArea.setLocalTranslation(-20, 0, -130);
        rootNode.attachChild(trainStorageArea);

        // Add the StorageArea for lorry containers.
        lorryStorageArea = new StorageArea(assetManager, 4);
        lorryStorageArea.setLocalTranslation(150, 0, -130);
        rootNode.attachChild(lorryStorageArea);
    }

    private void initPlatform() {
        // Platform for the scene.
        Spatial platform = assetManager.loadModel("Models/platform/platform.j3o");
        platform.scale(20, 1, 20);
        rootNode.attachChild(platform);
    }

    private void initUserInput() {
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
}
