package org.nhl.containing;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;
import javax.xml.parsers.*;
import org.xml.sax.InputSource;
import org.w3c.dom.*;
import java.io.*;

/**
 * @author TRJMM Used to communicate with the Backend system. See wiki for all
 * the commands
 */
public class Communication {

    private String containerIso;
    private String containerOwner;
    private String transportType;
    private int maxValueContainers = 0;

    private enum Status {

        LISTEN, SENDING, INITIALIZE, DISPOSE
    };
    private Status status;
    private Socket client;
    private InputStream inFromServer;
    private DataOutputStream out;
    private final int PORT = 6666;
    private final String serverName = "localhost";
    private Thread operation;
    private String Output = "";

    public Communication() {
        status = Status.INITIALIZE;
    }

    public String getCommand() {
        return Output;
    }

    /**
     * Starts the client
     */
    private void startClient() {
        try {

            System.out.println("Connecting to " + serverName
                    + " on port " + PORT);
            client = new Socket(serverName, PORT);
            System.out.println(" Just connected to "
                    + client.getRemoteSocketAddress());
        } catch (IOException e) {
            e.printStackTrace();
        }
        sleep(1000);
    }

    /**
     * Listens for input from the backend system
     */
    private void listen() {
        try {
            if (client == null) {
                client = new Socket(serverName, PORT);
                sleep(100);
            }
            System.out.println("Listening to "
                    + client.getRemoteSocketAddress() + "...");
            inFromServer = client.getInputStream();
            DataInputStream input =
                    new DataInputStream(inFromServer);
            Output = input.readUTF();
            if (Output.equals("")) {
                input.reset();
                Output = "";
            } else {
                System.out.println("Received string " + Output + " from backend system. Now decoding...");
                decodeXMLMessage(Output);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Tries to decode the incoming XML message and splits it within attributes
     * of this class.
     *
     * @param xmlMessage The xml message you're willing to decode
     */
    private void decodeXMLMessage(String xmlMessage) {
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
            }

            nodes = doc.getElementsByTagName("Create");
            if (nodes.getLength() > 0) {
                for (int i = 0; i < nodes.getLength(); i++) {
                    Element element = (Element) nodes.item(i);

                    NodeList iso = element.getElementsByTagName("iso");
                    Element line = (Element) iso.item(0);
                    containerIso = getCharacterDataFromElement(line);
                    System.out.println("ISO: " + containerIso);


                    NodeList owner = element.getElementsByTagName("owner");
                    line = (Element) owner.item(0);
                    containerOwner = getCharacterDataFromElement(line);
                    System.out.println("Owner: " + containerOwner);

                    NodeList arrivalType = element.getElementsByTagName("arrivalTransportType");
                    line = (Element) arrivalType.item(0);
                    transportType = getCharacterDataFromElement(line);
                    System.out.println("arrivalTransportType: " + transportType);
                }
            }

            nodes = doc.getElementsByTagName("Move");
            if (nodes.getLength() > 0) {
                for (int i = 0; i < nodes.getLength(); i++) {
                    Element element = (Element) nodes.item(i);
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

    /**
     * Sends a message to the backend system
     *
     * @param message The message
     */
    public void sendMessage(String message) {
        status = Status.SENDING;

        try {
            if (client == null) {
                client = new Socket(serverName, PORT);
                sleep(100);
            }
            System.out.println("Trying to send message " + message + " to the Backend system!");
            out = new DataOutputStream(client.getOutputStream());
            out.writeUTF(message);
            System.out.println("Sent message " + message + " to the Backend system!");

        } catch (Exception e) {
            e.printStackTrace();
        }
        status = Status.LISTEN;
    }

    /**
     * Stops the client and stops this thread
     */
    public void stopClient() {
        try {
            client.close();
            status = Status.DISPOSE;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * This method will be called once and loops until the thread stops.
     */
    public void Start() {
        operation = new Thread(new Runnable() {
            @Override
            public void run() {
                while (true) {
                    try {
                        switch (status) {
                            case INITIALIZE:
                                startClient();
                                status = Status.LISTEN;
                                break;
                            case LISTEN:
                                listen();
                                status = Status.LISTEN;
                                break;
                            case SENDING:
                                sleep(100);
                                status = Status.SENDING;
                                break;
                            case DISPOSE:
                                operation.stop();
                                break;
                        }

                    } catch (Throwable e) {
                        e.printStackTrace();
                    }
                }
            }
        });
        operation.setName("Simulation Communicator");
        operation.start();
    }

    /**
     * Sleep this thread we are working with for x milliseconds
     *
     * @param milliseconds How long are we waiting in milliseconds?
     */
    private void sleep(int milliseconds) {
        try {
            operation.currentThread().sleep(milliseconds); //1000 milliseconds is one second.
        } catch (InterruptedException ex) {
            operation.currentThread().interrupt();
        }
    }
}
