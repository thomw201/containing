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
    /**
     * this method changes the value of a parking space
     * @param nr parking spot location
     * @param status TRUE = taken, FALSE = free.
     */
    public void setParkingPlace(int nr, boolean status){
        hasLorry.set(nr, status);
    }
    /**
     * loops through the array to find a parking spot that isn't taken
     * @return the number of free parking spot.
     */
    public int getParkingPlace(){
        return 0;
    }
}
