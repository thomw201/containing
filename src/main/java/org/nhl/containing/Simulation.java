package org.nhl.containing;

import com.jme3.app.SimpleApplication;
import com.jme3.light.DirectionalLight;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import com.jme3.renderer.RenderManager;
import com.jme3.scene.Geometry;
import com.jme3.scene.shape.Box;

/**
 * test
 *
 * @author normenhansen
 */
public class Simulation extends SimpleApplication {

    @Override
    public void simpleInitApp() {

        cam();

        // A Directional Light.
        DirectionalLight sun = new DirectionalLight();
        sun.setColor(ColorRGBA.White);
        rootNode.addLight(sun);

        // Add an object to the scene.
        Container container = new Container(assetManager);
        rootNode.attachChild(container);

        // Platform for the scene.
        Box platform = new Box(500, 0.3f, 500);
        Geometry platformGeom = new Geometry("Platform", platform);
        platformGeom.setLocalTranslation(0, -15, 0);
        Material mat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        mat.setColor("Color", ColorRGBA.Gray);
        platformGeom.setMaterial(mat);

        rootNode.attachChild(platformGeom);
    }

    /**
     * Camera settings of the scene.
     */
    public void cam() {
        viewPort.setBackgroundColor(ColorRGBA.Blue);
        cam.setLocation(new Vector3f(0, 5, 0));
        flyCam.setMoveSpeed(50);
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
