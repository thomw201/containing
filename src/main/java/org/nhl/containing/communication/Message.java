package org.nhl.containing.communication;


import java.lang.String;

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

    public int getMaxValueContainers() {
        return maxValueContainers;
    }

    public void setMaxValueContainers(int maxValueContainers) {
        this.maxValueContainers = maxValueContainers;
    }

    public String getContainerIso() {
        return containerIso;
    }

    public void setContainerIso(String containerIso) {
        this.containerIso = containerIso;
    }

    public String getContainerOwner() {
        return containerOwner;
    }

    public void setContainerOwner(String containerOwner) {
        this.containerOwner = containerOwner;
    }

    public String getTransportType() {
        return transportType;
    }

    public void setTransportType(String transportType) {
        this.transportType = transportType;
    }

    public String getObjectName() {
        return objectName;
    }

    public void setObjectName(String objectName) {
        this.objectName = objectName;
    }

    public String getDestinationName() {
        return destinationName;
    }

    public void setDestinationName(String destinationName) {
        this.destinationName = destinationName;
    }

    public String getSpeed() {
        return speed;
    }

    public void setSpeed(String speed) {
        this.speed = speed;
    }

    public org.nhl.containing.communication.Message.Command getCommand() {
        return command;
    }

    public void setCommand(org.nhl.containing.communication.Message.Command command) {
        this.command = command;
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
