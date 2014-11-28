package org.nhl.containing;

import org.w3c.dom.CharacterData;
import org.xml.sax.InputSource;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.StringReader;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

enum Command {

    Create, Move, Dispose, LastMessage
}

/**
 * Parses provided XML files and returns Containers.
 */
public class Xml {

    private int maxValueContainers = 4;
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

    /**
     * Tries to decode the incoming XML message and splits it within attributes
     * of this class.
     *
     * @param xmlMessage The xml message you're willing to decode
     */
    public void decodeXMLMessage(String xmlMessage) {
        try {
            DocumentBuilderFactory dbf =
                    DocumentBuilderFactory.newInstance();
            DocumentBuilder db = dbf.newDocumentBuilder();
            InputSource is = new InputSource();
            is.setCharacterStream(new StringReader(xmlMessage));
            Document doc = db.parse(is);
            NodeList nodes = doc.getElementsByTagName("LastMessage");
            if (nodes.getLength() > 0) {
                for (int i = 0; i < nodes.getLength(); i++) {
                    Element element = (Element) nodes.item(i);
                    NodeList numberOfContainers = element.getElementsByTagName("numberOfContainers");
                    Element line = (Element) numberOfContainers.item(0);
                    maxValueContainers = Integer.parseInt(getCharacterDataFromElement(line));
                    System.out.println("numberOfContainers: " + maxValueContainers);
                }
                containerIso = null;
                containerOwner = null;
                transportType = null;
                objectName = null;
                destinationName = null;
                speed = null;
                command = Command.LastMessage;
            }
            nodes = doc.getElementsByTagName("Create");
            if (nodes.getLength() > 0) {
                for (int i = 0; i < nodes.getLength(); i++) {
                    Element element = (Element) nodes.item(i);
                    NodeList nodeList = element.getElementsByTagName("iso");
                    Element line = (Element) nodeList.item(0);
                    containerIso = getCharacterDataFromElement(line);
                    System.out.println("ISO: " + containerIso);
                    nodeList = element.getElementsByTagName("owner");
                    line = (Element) nodeList.item(0);
                    containerOwner = getCharacterDataFromElement(line);
                    System.out.println("Owner: " + containerOwner);
                    nodeList = element.getElementsByTagName("arrivalTransportType");
                    line = (Element) nodeList.item(0);
                    transportType = getCharacterDataFromElement(line);
                    System.out.println("arrivalTransportType: " + transportType);
                    nodeList = element.getElementsByTagName("arrivalTransportType");
                    line = (Element) nodeList.item(0);
                    xLoc = Integer.parseInt(getCharacterDataFromElement(line));
                    System.out.println("xLoc: " + xLoc);
                    nodeList = element.getElementsByTagName("arrivalTransportType");
                    line = (Element) nodeList.item(0);
                    yLoc = Integer.parseInt(getCharacterDataFromElement(line));
                    System.out.println("yLoc: " + yLoc);
                    nodeList = element.getElementsByTagName("arrivalTransportType");
                    line = (Element) nodeList.item(0);
                    zLoc = Integer.parseInt(getCharacterDataFromElement(line));
                    System.out.println("zLoc: " + zLoc);
                }
                objectName = null;
                destinationName = null;
                speed = null;
                command = Command.Create;
            }
            nodes = doc.getElementsByTagName("Move");
            if (nodes.getLength() > 0) {
                for (int i = 0; i < nodes.getLength(); i++) {
                    Element element = (Element) nodes.item(i);
                    NodeList nodeList = element.getElementsByTagName("objectName");
                    Element line = (Element) nodeList.item(0);
                    objectName = getCharacterDataFromElement(line);
                    System.out.println("objectName: " + objectName);
                    nodeList = element.getElementsByTagName("destinationName");
                    line = (Element) nodeList.item(0);
                    destinationName = getCharacterDataFromElement(line);
                    System.out.println("destinationName: " + destinationName);
                    nodeList = element.getElementsByTagName("speed");
                    line = (Element) nodeList.item(0);
                    speed = getCharacterDataFromElement(line);
                    System.out.println("speed: " + speed);
                }
                containerIso = null;
                containerOwner = null;
                transportType = null;
                command = Command.Move;
            }
            nodes = doc.getElementsByTagName("Dispose");
            if (nodes.getLength() > 0) {
                for (int i = 0; i < nodes.getLength(); i++) {
                    Element element = (Element) nodes.item(i);
                    NodeList nodeList = element.getElementsByTagName("objectName");
                    Element line = (Element) nodeList.item(0);
                    objectName = getCharacterDataFromElement(line);
                    System.out.println("objectName: " + objectName);
                }
                containerIso = null;
                containerOwner = null;
                transportType = null;
                objectName = null;
                destinationName = null;
                speed = null;
                command = Command.Dispose;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Gets the characterdata from the specified element
     */
    private static String getCharacterDataFromElement(Element e) {
        Node child = e.getFirstChild();
        if (child instanceof CharacterData) {
            CharacterData cd = (CharacterData) child;
            return cd.getData();
        }
        return "?";
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
