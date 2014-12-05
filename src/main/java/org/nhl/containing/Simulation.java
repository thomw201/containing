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

    private void sendOkMessageContainer(Container c) {
        String message = "<OK><OBJECTNAME>" + c.getName() + "</OBJECTNAME>"
                + "<OBJECTID>" + c.getContainerID() + "</OBJECTID></OK>";
        client.writeMessage(message);
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
                l.move(true, 0);
                rootNode.attachChild(l);
            }
        }
        totalContainerList.clear();
        createObjects();
    }

    private void createObjects() {
        if (!inlandshipContainerList.isEmpty()) {
            Boat b = new Boat(assetManager, Boat.ShipSize.INLANDSHIP, inlandshipContainerList);
            b.move(true);
            rootNode.attachChild(b);
        }
        if (!seashipContainerList.isEmpty()) {
            Boat b = new Boat(assetManager, Boat.ShipSize.SEASHIP, seashipContainerList);
            b.move(true);
            rootNode.attachChild(b);
        }
        if (!trainContainerList.isEmpty()) {
            Train t = new Train(assetManager, trainContainerList);
            t.move(true);
            rootNode.attachChild(t);
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
        for (int i = 0; i < 41; i++) {
            c = new Container(assetManager, "Coca Cola", "8-9912", "zeeschip", 0, 0, 0);
            totalContainerList.add(c);
        }
        for (int i = 0; i < 17; i++) {
            c = new Container(assetManager, "Coca Cola", "8-9912", "binnenschip", 0, 0, 0);
            totalContainerList.add(c);
        }
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
        cam.setLocation(new Vector3f(-240, 5, 220));
        flyCam.setMoveSpeed(50);
    }

    /*
     * Method for initializing the scene.
     */
    private void initScene() {
        initLighting();
        initAreas();
        initPlatform();
        //testMethodCranes();
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
        boatArea = new BoatArea(assetManager, BoatArea.AreaType.SEASHIP, 10, 28);
        boatArea.setLocalTranslation(-325, 0, -100);
        rootNode.attachChild(boatArea);

        // Add the inlandBoatArea.
        inlandBoatArea = new BoatArea(assetManager, BoatArea.AreaType.INLANDSHIP, 8, 40);
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
                        train.move(debug);
                        boat.move(debug);
                        lorry.move(debug, 0);
                    } else {
                        debug = true;
                        //testing train code
                        train.move(debug);
                        boat.move(debug);
                        lorry.move(debug, 2);
                    }
                }
            }
        };
        inputManager.addListener(acl, "debugmode");
    }
    
    /**
     * Method to see cranes in action!
     */
    private void testMethodCranes(){
        //TC test
        for (int i = 0; i < 15; i++) {
            trainContainerList.add(new Container(assetManager, "TEST CONTAINER", "8-9912", "trein", 0, 0, 0));
        }
        Train t = new Train(assetManager, trainContainerList);
        t.setLocalTranslation(-180, 0, -180);
        //t.rotate(0, (float) Math.PI / -2f, 0);
        rootNode.attachChild(t);
        Agv agv1 = new Agv(assetManager);
        agv1.rotate(0, (float) Math.PI / 2, 0);
        agv1.setLocalTranslation(-169, 0, -174);
        rootNode.attachChild(agv1);
        trainArea.getTrainCranes().get(3).trainToAgv(trainContainerList.get(0), agv1);
        //SC
        Agv agv2 = new Agv(assetManager);
        agv2.rotate(0, 0, 0);
        agv2.setLocalTranslation(140, 0, -125);
        rootNode.attachChild(agv2);
        Container container1 = new Container(assetManager, "TEST CONTAINER", "8-9912", "binnenschip", 0, 0, 0);
        container1.setLocalTranslation(120, 0, 0);
        rootNode.attachChild(container1);
        trainStorageArea.getStorageCranes().get(0).storageToAgv(container1, agv2);
        //TruckCrane
        Lorry lorry1 = new Lorry(assetManager, new Container(assetManager, "TEST CONTAINER", "8-9912", "vrachtwagen", 0, 0, 0) );
        lorry1.setLocalTranslation(300,0,170);
        rootNode.attachChild(lorry1);
        Agv agv4 = new Agv(assetManager);
        agv4.rotate(0, 0, 0);
        agv4.setLocalTranslation(300, 0, 150);
        rootNode.attachChild(agv4);
        lorryArea.getTruckCranes().get(0).truckToAgv(lorry1.getChild(1), agv4);
        //DC test
        Container container2 = new Container(assetManager, "TEST CONTAINER", "8-0002", "binnenschip", 0, 0, 0);
        container2.setLocalTranslation(-325,0,0);
        rootNode.attachChild(container2);
        Agv agv3 = new Agv(assetManager);
        agv3.rotate(0, 0, 0);
        agv3.setLocalTranslation(-285, 0, 20);
        rootNode.attachChild(agv3);
        boatArea.dockingCranes.get(0).boatToAgv(container2, agv3);
        
        //DC
        Container container5 = new Container(assetManager, "TEST CONTAINER", "8-0002", "binnenschip", 0, 0, 0);
        container5.setLocalTranslation(-200,0,240);
        container5.rotate(0, (float)Math.PI / 2, 0);
        rootNode.attachChild(container5);
        
        Agv agv5 = new Agv(assetManager);
        agv5.rotate(0, (float)Math.PI / 2, 0);
        agv5.setLocalTranslation(-180, 0, 140);
        rootNode.attachChild(agv5);
        inlandBoatArea.dockingCranes.get(0).boatToAgv(container5, agv5);
        
    }
}
