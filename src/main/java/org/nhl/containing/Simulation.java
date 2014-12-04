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
import org.nhl.containing.areas.*;
import org.nhl.containing.communication.Client;
import org.nhl.containing.vehicles.*;

import java.util.ArrayList;
import org.nhl.containing.communication.Message;
import org.nhl.containing.communication.Message.Command;
import org.nhl.containing.communication.Xml;

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
    private ArrayList<Message> incomingMessages;
    private ArrayList<Lorry> totalLorryList;
    private TrainArea trainArea;
    private LorryArea lorryArea;
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
    private boolean debug;

    public Simulation() {
        client = new Client();
        totalContainerList = new ArrayList<Container>();
        trainContainerList = new ArrayList<Container>();
        seashipContainerList = new ArrayList<Container>();
        inlandshipContainerList = new ArrayList<Container>();
        incomingMessages = new ArrayList<Message>();
        totalLorryList = new ArrayList<Lorry>();
    }

    @Override
    public void simpleInitApp() {
        initCam();
        initUserInput();
        initScene();
        Thread clientThread = new Thread(client);
        clientThread.setName("ClientThread");
        clientThread.start();
        try {
            Thread.sleep(1000);
        } catch (Throwable e) {
        }
        //createContainers();
        createAGVPath();
    }

    @Override
    public void simpleUpdate(float tpf) {
        if (client != null) {
            msgCheck();
        }
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
    private void sendOkMessageLorry(Vehicle veh) {
        String message = "<OK><OBJECTNAME>" + veh.getName() + "</OBJECTNAME>"
                + "<OBJECTID>" + veh.getId() + "</OBJECTID></OK>";
        client.writeMessage(message);
    }

    private void sendOkMessageTrain(Vehicle veh) {
        String message = "<OK><OBJECTNAME>" + veh.getName() + "</OBJECTNAME>"
                + "<OBJECTID>" + veh.getId() + "</OBJECTID>"
                + "<OBJECTSIZE>" + trainContainerList.size() + "</OBJECTSIZE></OK>";
        client.writeMessage(message);
        trainContainerList.clear();
    }

    private void sendOkMessageInlandShip(Vehicle veh) {
        String message = "<OK><OBJECTNAME>" + veh.getName() + "</OBJECTNAME>"
                + "<OBJECTID>" + veh.getId() + "</OBJECTID>"
                + "<OBJECTSIZE>" + inlandshipContainerList.size() + "</OBJECTSIZE></OK>";
        client.writeMessage(message);
        inlandshipContainerList.clear();
    }

    private void sendOkMessageSeaShip(Vehicle veh) {
        String message = "<OK><OBJECTNAME>" + veh.getName() + "</OBJECTNAME>"
                + "<OBJECTID>" + veh.getId() + "</OBJECTID>"
                + "<OBJECTSIZE>" + seashipContainerList.size() + "</OBJECTSIZE></OK>";
        client.writeMessage(message);
        seashipContainerList.clear();
    }

    /**
     * This functions will process all incomming create commands.
     * <p/>
     * Create containers and add them to a list<container>. When the
     * list<container> == maxValueContainer (so max count of the sended
     * commands) Then take apart the List<Container> and divide them to there
     * TransportList. When the List<container> == empty then create the
     * vehicles.
     */
    public void msgCheck() {
        String incoming = client.getMessage();
        if (incoming == null) {
            return;
        }
        
        incomingMessages.addAll(Xml.decodeXMLMessage(incoming));
        for (Message msg : incomingMessages) {
            if (msg.getCommand() == Command.Create) {
                createContainer(msg);
            }
        }
        sortContainers();
    }

    private void createContainer(Message msg) {

        //Creates a container from the incoming message
        Container container = new Container(assetManager, msg.getContainerOwner(),
                msg.getContainerIso(), msg.getTransportType(),
                msg.getxLoc(), msg.getyLoc(), msg.getzLoc());
        totalContainerList.add(container);
    }

    private void sortContainers() {//Places the newly created container on the right vehicle
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
                rootNode.attachChild(l);
                totalLorryList.add(l);
            }
        }
        totalContainerList.clear();
        createObjects();
    }

    private void createObjects() {
        if (!inlandshipContainerList.isEmpty()) {
            Boat b = new Boat(assetManager, Boat.ShipSize.INLANDSHIP, inlandshipContainerList);
            b.move(true, true);
            rootNode.attachChild(b);
            sendOkMessageInlandShip(b);
        }
        if (!seashipContainerList.isEmpty()) {
            Boat b = new Boat(assetManager, Boat.ShipSize.SEASHIP, seashipContainerList);
            b.move(true, true);
            rootNode.attachChild(b);
            sendOkMessageSeaShip(b);
        }
        if (!trainContainerList.isEmpty()) {
            Train t = new Train(assetManager, trainContainerList);
            t.move(true);
            rootNode.attachChild(t);
            sendOkMessageTrain(t);
        }
        for (int i = 0 ; i < totalLorryList.size() ; i++)
        {
            totalLorryList.get(i).move(true, i);
            sendOkMessageLorry(totalLorryList.get(i));
        }
    }

    /**
     * Voor het testen.
     */
    private void createContainers() {
        Container c;
        for (int i = 0; i < 29; i++) {
            c = new Container(assetManager, "Coca Cola", "8-9912", "trein", 0, 0, 0);
            totalContainerList.add(c);
        }
        c = new Container(assetManager, "Coca Cola", "8-9612", "vrachtauto", 0, 0, 0);
        totalContainerList.add(c);
        for (int i = 0; i < 100; i++) {
            c = new Container(assetManager, "Coca Cola", "8-9912", "zeeschip", i, 0, 0);
            totalContainerList.add(c);


        }
        for (int i = 0; i < 50; i++) {
            c = new Container(assetManager, "Coca Cola", "8-9912", "binnenschip", 0, 0, 0);
            totalContainerList.add(c);
        }
        sortShit();
    }

    private void sortShit() {
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
                l.move(true, 0);
                rootNode.attachChild(l);
            }
        }
        totalContainerList.clear();
        createObjects();
    }

    /**
     * This method creates waypoints on the AGV roads and lets an AGV drive over
     * them
     */
    private void createAGVPath() {
        agv = new Agv(assetManager);
        rootNode.attachChild(agv);
        MotionPath agvPath = new MotionPath();
        MotionEvent agvmotionControl = new MotionEvent(agv, agvPath);
        //Create the AGV waypoints
        //waypoint A
        agvPath.addWayPoint(new Vector3f(580, 0, -140));
        //waypont C
        agvPath.addWayPoint(new Vector3f(330, 0, -140));
        //waypoint E
        agvPath.addWayPoint(new Vector3f(70, 0, -140));
        //waypoint G
        agvPath.addWayPoint(new Vector3f(-210, 0, -140));
        //waypoint H
        agvPath.addWayPoint(new Vector3f(-210, 0, 135));
        //waypoint F
        agvPath.addWayPoint(new Vector3f(70, 0, 135));
        //waypoint D
        agvPath.addWayPoint(new Vector3f(330, 0, 136));
        //waypoint B
        agvPath.addWayPoint(new Vector3f(580, 0, 135));
        
        
        agvPath.setCurveTension(0.1f);
        // set the speed and direction of the AGV using motioncontrol
        agvmotionControl.setDirectionType(MotionEvent.Direction.PathAndRotation);
        agvmotionControl.setRotation(new Quaternion().fromAngleNormalAxis(0, Vector3f.UNIT_Y));
        agvmotionControl.setInitialDuration(10f);
        agvmotionControl.setSpeed(1f);
        //make the vehicles start moving
        //agvmotionControl.setLoopMode(LoopMode.Loop);
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
        // Add lorry area.
        lorryArea = new LorryArea(assetManager, 20);
        lorryArea.setLocalTranslation(300, 0, 170);
        rootNode.attachChild(lorryArea);

        // Add the TrainArea.
        trainArea = new TrainArea(assetManager, 4);
        trainArea.setLocalTranslation(-160, 0, -180);
        rootNode.attachChild(trainArea);

        // Add the BoatArea (Sea).
        boatArea = new BoatArea(assetManager, 10, 28);
        boatArea.setLocalTranslation(-325, 0, -100);
        rootNode.attachChild(boatArea);

        // Add the inlandBoatArea.
        inlandBoatArea = new BoatArea(assetManager, 8, 40);
        inlandBoatArea.rotate(0, (float) (0.5 * Math.PI), 0);
        inlandBoatArea.scale(0.8f, 0.8f, 0.8f);
        inlandBoatArea.setLocalTranslation(-240, 0, 220);
        rootNode.attachChild(inlandBoatArea);

        // Add the StorageArea for boat containers.
        boatStorageArea = new StorageArea(assetManager, 4);
        boatStorageArea.setLocalTranslation(-150, 0, -130);
        rootNode.attachChild(boatStorageArea);

        // Add the StorageArea for train containers.
        trainStorageArea = new StorageArea(assetManager, 4);
        trainStorageArea.setLocalTranslation(130, 0, -130);
        rootNode.attachChild(trainStorageArea);

        // Add the StorageArea for lorry containers.
        lorryStorageArea = new StorageArea(assetManager, 4);
        lorryStorageArea.setLocalTranslation(380, 0, -130);
        rootNode.attachChild(lorryStorageArea);
    }

    private void initPlatform() {
        // Platform for the scene.
        Spatial platform = assetManager.loadModel("Models/platform/platform.j3o");
        //vergroot platform
        platform.scale(30, 1, 20);
        //schuif platform op
        platform.setLocalTranslation(150, 0, 0);
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
                        createAGVPath();
                    } else {
                        debug = true;
                        //testing train code
                    }
                }
            }
        };
        inputManager.addListener(acl, "debugmode");
    }
}
