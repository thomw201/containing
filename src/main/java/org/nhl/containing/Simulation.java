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
import java.util.List;
import java.util.PriorityQueue;
import org.nhl.containing.communication.ArriveMessage;
import org.nhl.containing.communication.ContainerBean;
import org.nhl.containing.communication.CreateMessage;
import org.nhl.containing.communication.Message;
import org.nhl.containing.communication.Xml;

/**
 * test
 *
 * @author normenhansen
 */
public class Simulation extends SimpleApplication {

    private List<Transporter> transporterPool;
    private TrainArea trainArea;
    private LorryArea lorryArea;
    private BoatArea boatArea;
    private BoatArea inlandBoatArea;
    private StorageArea boatStorageArea;
    private StorageArea trainStorageArea;
    private StorageArea lorryStorageArea;
    private Client client;
    private boolean debug;

    public Simulation() {
        client = new Client();
        transporterPool = new ArrayList<Transporter>();
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
        handleMessages();
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
    
    private void handleMessages() {
        List<String> xmlMessages = new ArrayList<String>();
        while (true) {
            String xmlMessage = client.getMessage();
            if (xmlMessage == null) {
                break;
            }
            xmlMessages.add(xmlMessage);
        }
        
        for (String xmlMessage : xmlMessages) {
            handleMessage(xmlMessage);
        }
    }
    
    private void handleMessage(String xmlMessage) {
        Message message = null;
        try {
            message = Xml.parseXmlMessage(xmlMessage);
        } catch (Exception e) {
            e.printStackTrace();
        }

        switch (message.getMessageType()) {
            case Message.CREATE:
                handleCreateMessage((CreateMessage) message);
                break;
            case Message.ARRIVE:
                //handleArriveMessage((ArriveMessage) message);
                break;
        }
    }
    
    private void handleCreateMessage(CreateMessage message) {
        List<Container> containers = new ArrayList<Container>();
        for (ContainerBean containerBean : message.getContainerBeans()) {
            Container container = new Container(assetManager, containerBean.getOwner(),
                    containerBean.getIso(), containerBean.getxLoc(),
                    containerBean.getyLoc(), containerBean.getzLoc());
            containers.add(container);
        }

        if (message.getTransporterType().equals("vrachtauto")) {
            Lorry lorry = new Lorry(assetManager, containers.get(0));
            transporterPool.add(lorry);
        } else if (message.getTransporterType().equals("trein")) {
            Train train = new Train(assetManager, containers);
            transporterPool.add(train);
        } else if (message.getTransporterType().equals("binnenschip")) {
            Boat boat = new Boat(assetManager, Boat.ShipSize.INLANDSHIP, containers);
            transporterPool.add(boat);
        } else if (message.getTransporterType().equals("zeeschip")) {
            Boat boat = new Boat(assetManager, Boat.ShipSize.SEASHIP, containers);
            transporterPool.add(boat);
        }
        sendOkMessage(message);
    }
    
    private void handleArriveMessage(ArriveMessage message) {
    }

    /**
     * Sends an OK-type message to the controller of the object that is ready
     * for a new task -SUBJECT TO CHANGE, MAYBE SUPERCLASS OBJECT IN THE
     * FUTURE!-
     *
     * @param veh -SUBJECT TO CHANGE, MAYBE SUPERCLASS OBJECT IN THE FUTURE!-
     */
    private void sendOkMessage(Message message) {
        client.writeMessage("<Ok><id>" + message.getId() + "</id></Ok>");
    }
    
    /**
     * Reads the message object and creates and returns a Transporter from its
     * information.
     * 
     * @param message Create message.
     * @return Transporter as described in the message.
     */
    private Transporter createTransporterFromMessage(Message message) {
        return null;
    }

    /**
     * This method creates waypoints on the AGV roads and lets an AGV drive over
     * them
     */
    private void createAGVPath() {
        Agv agv = new Agv(assetManager);
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

    /**
     * Method to see cranes in action!
     */
    private void testMethodCranes() {
        //TC test
        List<Container> containers = new ArrayList<Container>();
        for (int i = 0; i < 15; i++) {
            containers.add(new Container(assetManager, "TEST CONTAINER", "8-9912", 0, 0, 0));
        }
        Train t = new Train(assetManager, containers);
        t.setLocalTranslation(-180, 0, -180);
        //t.rotate(0, (float) Math.PI / -2f, 0);
        rootNode.attachChild(t);
        Agv agv1 = new Agv(assetManager);
        agv1.rotate(0, (float) Math.PI / 2, 0);
        agv1.setLocalTranslation(-169, 0, -174);
        rootNode.attachChild(agv1);
        trainArea.getTrainCranes().get(3).trainToAgv(containers.get(0), agv1);
        //SC
        Agv agv2 = new Agv(assetManager);
        agv2.rotate(0, 0, 0);
        agv2.setLocalTranslation(140, 0, -125);
        rootNode.attachChild(agv2);
        Container container1 = new Container(assetManager, "TEST CONTAINER", "8-9912", 0, 0, 0);
        container1.setLocalTranslation(120, 0, 0);
        rootNode.attachChild(container1);
        trainStorageArea.getStorageCranes().get(0).storageToAgv(container1, agv2);
        //TruckCrane
        Lorry lorry1 = new Lorry(assetManager, new Container(assetManager, "TEST CONTAINER", "8-9912", 0, 0, 0));
        lorry1.setLocalTranslation(300, 0, 170);
        rootNode.attachChild(lorry1);
        Agv agv4 = new Agv(assetManager);
        agv4.rotate(0, 0, 0);
        agv4.setLocalTranslation(300, 0, 150);
        rootNode.attachChild(agv4);
        lorryArea.getTruckCranes().get(0).truckToAgv(lorry1.getChild(1), agv4);
        //DC test
        Container container2 = new Container(assetManager, "TEST CONTAINER", "8-0002", 0, 0, 0);
        container2.setLocalTranslation(-325, 0, 0);
        rootNode.attachChild(container2);
        Agv agv3 = new Agv(assetManager);
        agv3.rotate(0, 0, 0);
        agv3.setLocalTranslation(-285, 0, 20);
        rootNode.attachChild(agv3);
        boatArea.dockingCranes.get(0).boatToAgv(container2, agv3);

        //DC
        Container container5 = new Container(assetManager, "TEST CONTAINER", "8-0002", 0, 0, 0);
        container5.setLocalTranslation(-200, 0, 240);
        container5.rotate(0, (float) Math.PI / 2, 0);
        rootNode.attachChild(container5);

        Agv agv5 = new Agv(assetManager);
        agv5.rotate(0, (float) Math.PI / 2, 0);
        agv5.setLocalTranslation(-180, 0, 140);
        rootNode.attachChild(agv5);
        inlandBoatArea.dockingCranes.get(0).boatToAgv(container5, agv5);
    }
}
