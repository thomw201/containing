package org.nhl.containing;

import com.jme3.asset.AssetManager;
import com.jme3.font.BitmapFont;
import com.jme3.font.BitmapText;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.FastMath;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import com.jme3.renderer.queue.RenderQueue;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;

/**
 * @author Jeroen
 */
public class Container extends Node {

    private AssetManager assetManager;
    private String owner;
    private String containerID;
    private String transportType;
    private Vector3f Location;

    public Container(AssetManager assetManager, String owner, String containerID, String transportType, Vector3f Location) {
        this.assetManager = assetManager;
        this.owner = owner;
        this.containerID = containerID;
        this.transportType = transportType;
        this.Location = Location;
        initContainer();
    }

    /**
     * Initialize a container.
     */
    public void initContainer() {
        // Load a model.
        Spatial container = assetManager.loadModel("Models/medium/container/container.j3o");
        Material mat = new Material(assetManager,
                "Common/MatDefs/Misc/Unshaded.j3md");  // create a simple material
        mat.setColor("Color", ColorRGBA.White);   // set color of material to blue
        mat.setColor("Color", ColorRGBA.randomColor());
        container.setMaterial(mat);
        this.attachChild(container);
        //create a random text color
        ColorRGBA textColor = ColorRGBA.randomColor();
        //place the company name on the side
        drawText(1.2f, 2.6f, 6, textColor, new Quaternion().fromAngleAxis(FastMath.PI / 2, new Vector3f(0, 1, 0)));
        drawText(-1.2f, 2.6f, -6, textColor, new Quaternion().fromAngleAxis(FastMath.PI * 1.5f, new Vector3f(0, 1, 0)));
    }

    /**
     * This method writes the company name on the side of the container Has
     * parameters for placing the text on the right and left side.
     */
    public void drawText(float x, float y, float z, ColorRGBA color, Quaternion rotation) {
        BitmapFont guiFont = assetManager.loadFont("Interface/Fonts/Default.fnt");
        BitmapText companyText = new BitmapText(guiFont, false);
        companyText.setSize(1);      // font size
        companyText.setColor(color);                             // font color
        companyText.setText(owner);             // the text
        companyText.rotate(rotation);
        companyText.setLocalTranslation(x, y, z); // position
        companyText.setQueueBucket(RenderQueue.Bucket.Translucent);
        this.attachChild(companyText);
    }

    public Vector3f getLocation() {
        return Location;
    }

    public void setLocation(Vector3f Location) {
        this.Location = Location;
    }

    public String getContainerID() {
        return containerID;
    }

    public String getOwner() {
        return owner;
    }

    public String getTransportType() {
        return transportType;
    }
}