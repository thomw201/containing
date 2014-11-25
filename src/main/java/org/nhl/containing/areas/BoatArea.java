package org.nhl.containing.areas;
 
import com.jme3.asset.AssetManager;
import com.jme3.scene.Spatial;
import java.util.ArrayList;
import java.util.List;
import org.nhl.containing.cranes.DockingCrane;
 
/**
 *
 * @author Jeroen
 */
public class BoatArea extends Area {
 
    private AssetManager assetManager;
    private int craneZAxis = 0;
    private int craneRailsZAxis = -30;
    private int cranes;
    public List<DockingCrane> dockingCranes = new ArrayList();
 
    public BoatArea(AssetManager assetmanager, int cranes) {
        this.assetManager = assetmanager;
        this.cranes = cranes;
        initBoatArea();
    }
 
    /**
     * Initialize a boat area.
     */
    private void initBoatArea() {
 
        // Add docking cranes to the list and scene.
        for (int i = 0; i < cranes; i++) {
            dockingCranes.add(new DockingCrane(assetManager));
            dockingCranes.get(i).setLocalTranslation(0, 0, craneZAxis);
            this.attachChild(dockingCranes.get(i));
            craneZAxis += 18;
        }
 
        // Add crane rails.
        Spatial craneRails = assetManager.loadModel("Models/rails/craneRails.j3o");
        for (int i = 0; i < 28; i++) {
            Spatial nextRail = craneRails.clone();
            nextRail.setLocalTranslation(42f, 0, craneRailsZAxis);
            nextRail.setLocalScale(0.89f, 1, 1);
            this.attachChild(nextRail);
            craneRailsZAxis += 11;
        }
    }
}