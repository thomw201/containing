package org.nhl.containing.communication;



/**
 *  A data class used by the xml decoder
 *
 */
public class Message {

    public enum Command {

    Create, Move, Dispose, LastMessage
}
    private int maxValueContainers;
    private String containerIso;
    private String containerOwner;
    private String transportType;
    private String objectName;
    private String destinationName;
    private String speed;
    private Command command;
    private int xLoc;
    private int yLoc;
    private int zLoc;

    public void setCommand(Command command) {
        this.command = command;
    }

    public void setContainerIso(String containerIso) {
        this.containerIso = containerIso;
    }

    public void setContainerOwner(String containerOwner) {
        this.containerOwner = containerOwner;
    }

    public void setDestinationName(String destinationName) {
        this.destinationName = destinationName;
    }

    public void setMaxValueContainers(int maxValueContainers) {
        this.maxValueContainers = maxValueContainers;
    }

    public void setObjectName(String objectName) {
        this.objectName = objectName;
    }

    public void setSpeed(String speed) {
        this.speed = speed;
    }

    public void setTransportType(String transportType) {
        this.transportType = transportType;
    }

    public void setxLoc(int xLoc) {
        this.xLoc = xLoc;
    }

    public void setyLoc(int yLoc) {
        this.yLoc = yLoc;
    }

    public void setzLoc(int zLoc) {
        this.zLoc = zLoc;
    }

    public Command getCommand() {
        return command;
    }

    public String getContainerIso() {
        return containerIso;
    }

    public String getContainerOwner() {
        return containerOwner;
    }

    public String getDestinationName() {
        return destinationName;
    }

    public int getMaxValueContainers() {
        return maxValueContainers;
    }

    public String getObjectName() {
        return objectName;
    }

    public String getSpeed() {
        return speed;
    }

    public String getTransportType() {
        return transportType;
    }

    public int getxLoc() {
        return xLoc;
    }

    public int getyLoc() {
        return yLoc;
    }

    public int getzLoc() {
        return zLoc;
    }
}
