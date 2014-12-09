package org.nhl.containing.communication;

/**
 * Small data Java bean class that holds information about containers.
 * </p>
 * Not technically a true bean, but it has an empty constructor and getters and
 * setters a plenty.
 * </p>
 * The scope of this class should be miniscule. It's just a temporary data class
 * until the data can be transferred to an actual Container.
 *
 * @author Rubykuby
 */
public class ContainerBean {

    private String iso;
    private String owner;
    private int xLoc;
    private int yLoc;
    private int zLoc;

    public ContainerBean() {
    }

    public String getIso() {
        return iso;
    }

    public void setIso(String iso) {
        this.iso = iso;
    }

    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    public int getxLoc() {
        return xLoc;
    }

    public void setxLoc(int xLoc) {
        this.xLoc = xLoc;
    }

    public int getyLoc() {
        return yLoc;
    }

    public void setyLoc(int yLoc) {
        this.yLoc = yLoc;
    }

    public int getzLoc() {
        return zLoc;
    }

    public void setzLoc(int zLoc) {
        this.zLoc = zLoc;
    }
}
