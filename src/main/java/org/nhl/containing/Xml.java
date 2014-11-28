package org.nhl.containing;

import org.w3c.dom.CharacterData;
import org.xml.sax.InputSource;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.StringReader;

enum Command {
    Create, Move, Dispose, LastMessage
}

/**
 * Parses provided XML files and returns Containers.
 */
public class Xml {

    public static int maxValueContainers;
    public static String containerIso;
    public static String containerOwner;
    public static String transportType;
    public static String objectName;
    public static String destinationName;
    public static String speed;
    public static Command command;

    /**
     * Tries to decode the incoming XML message and splits it within attributes
     * of this class.
     *
     * @param xmlMessage The xml message you're willing to decode
     */
    public static void decodeXMLMessage(String xmlMessage) {
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
}
