package org.nhl.containing;

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
import java.io.IOException;
import org.nhl.containing.areas.*;
import org.nhl.containing.communication.Client;
import org.nhl.containing.vehicles.*;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import javax.xml.parsers.ParserConfigurationException;
import org.nhl.containing.communication.messages.ArriveMessage;
import org.nhl.containing.communication.ContainerBean;
import org.nhl.containing.communication.messages.CreateMessage;
import org.nhl.containing.communication.messages.Message;
import org.nhl.containing.communication.messages.SpeedMessage;
import org.nhl.containing.communication.Xml;
import org.xml.sax.SAXException;

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
    private HUD HUD;
    private boolean debug;
    private Calendar cal;
    private Date currentDate;
    private long sumTime = Integer.MAX_VALUE;
    private long lastTime;
    private final static int TIME_MULTIPLIER = 200;

    public Simulation() {
        client = new Client();
        transporterPool = new ArrayList<>();
    }
 
    @Override
    public void simpleInitApp() {
        guiFont = assetManager.loadFont("Interface/Fonts/TimesNewRoman.fnt");
        initCam();
        initUserInput();
        initScene();
        initDate();
        HUD = new HUD(this.guiNode, guiFont);
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
        updateDate();
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
        } catch (ParserConfigurationException | SAXException | IOException e) {
            e.printStackTrace();
        } catch (IllegalArgumentException e) {
            // Maybe just stacktrace here? Depends on how robust we want this to
            // be. With the current code, it just discards the erroneous
            // message.
            System.out.println(xmlMessage + " is not a valid message");
            return;
        }

        switch (message.getMessageType()) {
            case Message.CREATE:
                handleCreateMessage((CreateMessage) message);
                break;
            case Message.ARRIVE:
                handleArriveMessage((ArriveMessage) message);
                break;
            case Message.SPEED:
                handleSpeedMessage((SpeedMessage) message);
                break;
            default:
                throw new IllegalArgumentException(message.getMessageType()
                        + " is not a legal message type");
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

        switch (message.getTransporterType()) {
            case "vrachtauto":
                Lorry lorry = new Lorry(assetManager,
                        message.getTransporterIdentifier(), containers.get(0));
                transporterPool.add(lorry);
                break;
            case "trein":
                Train train = new Train(assetManager,
                        message.getTransporterIdentifier(), containers);
                transporterPool.add(train);
                break;
            case "binnenschip":
                Inlandship inland = new Inlandship(assetManager,
                        message.getTransporterIdentifier(), containers);
                transporterPool.add(inland);
                break;
            case "zeeschip":
                Seaship sea = new Seaship(assetManager,
                        message.getTransporterIdentifier(), containers);
                transporterPool.add(sea);
                break;
            default:
                throw new IllegalArgumentException(message.getTransporterType()
                        + " is not a legal transporter type");
        }
        sendOkMessage(message);
    }

    private void handleArriveMessage(ArriveMessage message) {
        Transporter transporter = null;
        boolean nobreak = true;

        for (Transporter poolTransporter : transporterPool) {
            if (poolTransporter.getId() == message.getTransporterId()) {
                transporter = poolTransporter;
                nobreak = false;
                break;
            }
        }
        if (nobreak) {
            throw new IllegalArgumentException("Transporter " + message.getTransporterId()
                    + " does not exist");
        }
        // Tell transporter to *arrive*. Rename move() to arrive(). move() is
        // too ambiguous. move() should probably be an *abstract* method within
        // Transporter, to be actually defined within each of the subclasses.
        //
        // Set `processingMessageId` within the Transporter/Vehicle/Object to
        // this message ID. Once the Transporter has arrived at its destination,
        // check for this in a separate function in simpleUpdate(), and then
        // send an OK message for the Transporter/Vehicle/Object's
        // `processingMessageId`.
        //
        // The `processingMesageId` also applies to cranes and AGVs. Cranes,
        // AGVs and transporters should all therefore be subclassed from the
        // same class, OR all implement the same interface (ProcessesMessage).
        // An interface is the cleanest solution, and can be found within the
        // backend. The default value should be -1 (i.e., not processing any
        // messages).
        //
        // Finally, pop transporter from transporterPool, and add it to
        // `transporters`. This list doesn't exist yet, but is trivial to
        // create. The reason we don't want to keep the transporter in the pool,
        // is because we want a way of discerning whether a transporter can
        // actually be interacted with.
    }

    private void handleSpeedMessage(SpeedMessage message) {
        speed = message.getSpeed();
        sendOkMessage(message);
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
        Agv agv = new Agv(assetManager, -1);
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
     * Initialises the simulation date.
     */
    private void initDate() {
        cal = Calendar.getInstance();
        cal.set(Calendar.YEAR, 2004);
        cal.set(Calendar.MONTH, 11);
        cal.set(Calendar.DAY_OF_MONTH, 1);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        currentDate = cal.getTime();
        lastTime = System.currentTimeMillis();
    }
    
        /**
     * Updates the simulation date.
     * <p/>
     * Compares the time since the last function call to the current time. This is the delta time.
     * The delta time is added to the simulation date, multiplied by the specified TIME_MULTIPLIER.
     */
    private void updateDate() {
        long curTime = System.currentTimeMillis();
        int deltaTime = (int) (curTime - lastTime);
        sumTime += deltaTime;
        cal.add(Calendar.MILLISECOND, deltaTime * TIME_MULTIPLIER);
        currentDate = cal.getTime();
        lastTime = curTime;
        
        HUD.updateDateText(currentDate);
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
        boatStorageArea.setLocalTranslation(-150, 0, -120);
        rootNode.attachChild(boatStorageArea);

        // Add the StorageArea for train containers.
        trainStorageArea = new StorageArea(assetManager, 4);
        trainStorageArea.setLocalTranslation(130, 0, -120);
        rootNode.attachChild(trainStorageArea);

        // Add the StorageArea for lorry containers.
        lorryStorageArea = new StorageArea(assetManager, 4);
        lorryStorageArea.setLocalTranslation(380, 0, -120);
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
                        Inlandship test = new Inlandship(assetManager, 0, new ArrayList());
                        rootNode.attachChild(test);
                        test.arrive(0);
                        Seaship test2 = new Seaship(assetManager, 0, new ArrayList());
                        rootNode.attachChild(test2);
                        test2.arrive(0);
                        Train traintest = new Train(assetManager, 0, new ArrayList());
                        rootNode.attachChild(traintest);
                        traintest.arrive(0);
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
        Train t = new Train(assetManager, -1, containers);
        t.setLocalTranslation(-180, 0, -180);
        //t.rotate(0, (float) Math.PI / -2f, 0);
        rootNode.attachChild(t);
        Agv agv1 = new Agv(assetManager, -1);
        agv1.rotate(0, (float) Math.PI / 2, 0);
        agv1.setLocalTranslation(-169, 0, -174);
        rootNode.attachChild(agv1);
        trainArea.getTrainCranes().get(3).trainToAgv(containers.get(0), agv1);
        //SC
        Agv agv2 = new Agv(assetManager, -1);
        agv2.rotate(0, 0, 0);
        agv2.setLocalTranslation(140, 0, -125);
        rootNode.attachChild(agv2);
        Container container1 = new Container(assetManager, "TEST CONTAINER", "8-9912", 0, 0, 0);
        container1.setLocalTranslation(120, 0, 0);
        rootNode.attachChild(container1);
        trainStorageArea.getStorageCranes().get(0).storageToAgv(container1, agv2);
        //TruckCrane
        Lorry lorry1 = new Lorry(assetManager, -1, new Container(assetManager, "TEST CONTAINER", "8-9912", 0, 0, 0));
        lorry1.setLocalTranslation(300, 0, 170);
        rootNode.attachChild(lorry1);
        Agv agv4 = new Agv(assetManager, -1);
        agv4.rotate(0, 0, 0);
        agv4.setLocalTranslation(300, 0, 150);
        rootNode.attachChild(agv4);
        lorryArea.getTruckCranes().get(0).truckToAgv(lorry1.getChild(1), agv4);
        //DC test
        Container container2 = new Container(assetManager, "TEST CONTAINER", "8-0002", 0, 0, 0);
        container2.setLocalTranslation(-325, 0, 0);
        rootNode.attachChild(container2);
        Agv agv3 = new Agv(assetManager, -1);
        agv3.rotate(0, 0, 0);
        agv3.setLocalTranslation(-285, 0, 20);
        rootNode.attachChild(agv3);
        boatArea.dockingCranes.get(0).boatToAgv(container2, agv3);

        //DC
        Container container5 = new Container(assetManager, "TEST CONTAINER", "8-0002", 0, 0, 0);
        container5.setLocalTranslation(-200, 0, 240);
        container5.rotate(0, (float) Math.PI / 2, 0);
        rootNode.attachChild(container5);

        Agv agv5 = new Agv(assetManager, -1);
        agv5.rotate(0, (float) Math.PI / 2, 0);
        agv5.setLocalTranslation(-180, 0, 140);
        rootNode.attachChild(agv5);
        inlandBoatArea.dockingCranes.get(0).boatToAgv(container5, agv5);
    }
}
