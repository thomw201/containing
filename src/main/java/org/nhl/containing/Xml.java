package org.nhl.containing;

import org.w3c.dom.*;
import org.xml.sax.InputSource;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.StringReader;

enum Command {
    Create, Move, Dispose, LastMessage
}

/**
 *
 *
 */
public class Xml {

    public static String okObject;
    public static int okId;
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
            NodeList nodes = doc.getElementsByTagName("OK");
            if (nodes.getLength() > 0) {
                for (int i = 0; i < nodes.getLength(); i++) {
                    Element element = (Element) nodes.item(i);
                    NodeList numberOfContainers = element.getElementsByTagName("OBJECT");
                    Element line = (Element) numberOfContainers.item(0);
                    okObject = getCharacterDataFromElement(line);
                    System.out.println("OBJECT: " + okObject);
                    numberOfContainers = element.getElementsByTagName("OBJECTID");
                    line = (Element) numberOfContainers.item(0);
                    okId = Integer.parseInt(getCharacterDataFromElement(line));
                    System.out.println("OBJECTID: " + okId);
                }
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
