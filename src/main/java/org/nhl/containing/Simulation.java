package org.nhl.containing;

import com.jme3.app.SimpleApplication;
import com.jme3.light.DirectionalLight;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import com.jme3.renderer.RenderManager;
import com.jme3.scene.Spatial;
import org.nhl.containing.cranes.DockingCrane;
import org.nhl.containing.vehicles.Train;

/**
 * test
 *
 * @author normenhansen
 */
public class Simulation extends SimpleApplication {

    @Override
    public void simpleInitApp() {
        cam();
        scene();
    }

    /**
     * Camera settings of the scene.
     */
    public void cam() {
        viewPort.setBackgroundColor(ColorRGBA.Blue);
        cam.setLocation(new Vector3f(0, 5, 0));
        flyCam.setMoveSpeed(50);
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
        container.setLocalTranslation(0, 0, 0);
        rootNode.attachChild(container);

        // Platform for the scene.        
        Spatial platform = assetManager.loadModel("Models/platform/platform.j3o");
        platform.scale(20, 1, 20);
        rootNode.attachChild(platform);
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
    }

    public void readyCheck() {
    }
}
