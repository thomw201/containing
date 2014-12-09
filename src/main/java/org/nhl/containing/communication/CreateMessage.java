package org.nhl.containing.communication;

import java.util.List;

/**
 * Data class that holds information pertaining the creation of a transporter.
 *
 */
public class CreateMessage extends Message {
    private String transporterType;
    private int transporterIdentifier;
    private List<ContainerBean> containerBeans;
    
    public CreateMessage(int id, String transporterType, int transporterIdentifier,
            List<ContainerBean> containerBeans) {
        super(id);
        this.transporterType = transporterType;
        this.transporterIdentifier = transporterIdentifier;
        this.containerBeans = containerBeans;
    }

    public String getTransporterType() {
        return transporterType;
    }

    public int getTransporterIdentifier() {
        return transporterIdentifier;
    }

    public List<ContainerBean> getContainerBeans() {
        return containerBeans;
    }
}
