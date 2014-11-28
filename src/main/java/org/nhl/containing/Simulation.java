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
    //TIJDELIJK
    private int locationInt = -10;
    private Agv agv;
    private Client server;
    private Train train;
    private Boat boat;
    private Lorry lorry;
    private Container c;
    private boolean debug;

    public Simulation() {
        server = new Client();
        totalContainerList = new ArrayList<Container>();
        trainContainerList = new ArrayList<Container>();
        seashipContainerList = new ArrayList<Container>();
        inlandshipContainerList = new ArrayList<Container>();
    }

    @Override
    public void simpleInitApp() {
        initCam();
        initScene();
        initUserInput();
        createContainers();
        Thread serverThread = new Thread(server);
        serverThread.setName("ServerThread");
        serverThread.start();
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
        server.stop();
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
        server.writeMessage(message);
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
        /*if (communication.getMaxValueContainers() != 0) {
            //if (communication.getMaxValueContainers() == totalContainerList.size()) {
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
                    Lorry l = new Lorry(assetManager , c);
                    rootNode.attachChild(l);
                }
            }
            if (!inlandshipContainerList.isEmpty()) {
                Boat b = new Boat(assetManager, Boat.Size.INLANDSHIP, inlandshipContainerList, seashipContainerList);
                b.move(debug);
                rootNode.attachChild(b);
            }
            if (!seashipContainerList.isEmpty()) {
                Boat b = new Boat(assetManager, Boat.Size.SEASHIP, inlandshipContainerList, seashipContainerList);
                b.move(debug);
                rootNode.attachChild(b);
            }
            if (!trainContainerList.isEmpty()) {
                Train t = new Train(assetManager, trainContainerList);
                t.setLocalTranslation(280, 0, -180);
                t.rotate(0, (float) Math.PI / 2f, 0);
                rootNode.attachChild(t);
            }
            //}
        } else {
            c = new Container(assetManager, communication.getContainerOwner(),
                    communication.getContainerIso(), communication.getTransportType(), new Vector3f(0, locationInt += 10, 0));
            totalContainerList.add(c);
        }*/
    }

    /**
     * Voor het testen.
     */
    private void createContainers() {
        for (int i = 0; i < 29; i++) {
            c = new Container(assetManager, "Coca Cola", "8-9912", "trein", new Vector3f(0, 2, 0));
            totalContainerList.add(c);
        }


        c = new Container(assetManager, "Coca Cola", "8-9612", "vrachtauto", new Vector3f(0, 2, 0));
        totalContainerList.add(c);
        for (int i = 0; i < 41; i++) {
            c = new Container(assetManager, "Coca Cola", "8-9912", "zeeschip", new Vector3f(0, 2, 0));
            totalContainerList.add(c);
        }
        for (int i = 0; i < 17; i++) {
            c = new Container(assetManager, "Coca Cola", "8-9912", "binnenschip", new Vector3f(0, 2, 0));
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
