package org.nhl.containing.areas;

import com.jme3.asset.AssetManager;
import java.util.ArrayList;
import java.util.List;
import org.nhl.containing.cranes.TruckCrane;

/**
 *
 * @author Jeroen
 */
public class LorryArea extends Area {
    
    private AssetManager assetManager;
    private int cranes;
    private int craneXAxis = 0;
    public List<Boolean> hasLorry = new ArrayList();
    public List<TruckCrane> truckCranes = new ArrayList();
    
    public LorryArea(AssetManager assetManager, int cranes){
        this.assetManager = assetManager;
        this.cranes = cranes;
        for (int i = 0; i < 20; i++) {
            hasLorry.add(i, false);
        }
        initLorryArea();
    }
    
    /**
     * Initialize a lorry area.
     */
    private void initLorryArea(){
        // Add truck cranes to the list and scene.
        for (int i = 0; i < cranes; i++) {
            truckCranes.add(new TruckCrane(assetManager));
            truckCranes.get(i).setLocalTranslation(craneXAxis, 0, 0);
            this.attachChild(truckCranes.get(i));
            craneXAxis += 14;
        }
    }
}
