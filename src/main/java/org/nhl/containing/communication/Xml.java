package org.nhl.containing.communication;

import org.w3c.dom.CharacterData;
import org.xml.sax.InputSource;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.StringReader;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import java.util.ArrayList;
import javax.xml.parsers.ParserConfigurationException;



/**
 * Parses provided XML files and returns Containers.
 */
public class Xml {

    public static Message parseXmlMessage(String xmlMessage) throws Exception {
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        DocumentBuilder db = dbf.newDocumentBuilder();
        InputSource source = new InputSource();
        source.setCharacterStream(new StringReader(xmlMessage));

        Document doc = db.parse(source);
        
        // Optional, but recommended.
        // Read this - http://stackoverflow.com/questions/13786607/normalization-in-dom-parsing-with-java-how-does-it-work
        doc.getDocumentElement().normalize();

        NodeList nodes = doc.getElementsByTagName("id");
        if (nodes.getLength() != 1) {
            throw new Exception(xmlMessage + " is not a valid message");
        }

        Element line = (Element) nodes.item(0);
        int id = Integer.parseInt(Xml.getCharacterDataFromElement(line));
        
        NodeList messageTypeNodes = doc.getElementsByTagName("Create");
        if (messageTypeNodes.getLength() > 0) {
            return parseCreateMessage(messageTypeNodes, id);
        }
        
        messageTypeNodes = doc.getElementsByTagName("Arrive");
        if (messageTypeNodes.getLength() > 0) {
            return parseArriveMessage(messageTypeNodes, id);
        }
        
        // etc etc etc.
        
        throw new Exception("Could not find valid tag in " + xmlMessage);
    }
    
    private static CreateMessage parseCreateMessage(NodeList nodes, int id) {
        return null;
    }
    
    private static ArriveMessage parseArriveMessage(NodeList nodes, int id) {
        return null;
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
        return "";
    }
}