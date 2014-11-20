package org.nhl.containing;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;
/*
 * Commands;
 * 
 * OK + [OBJECT] + [OBJECTID] + /OK
 * 
 */

/**
 * @author TRJMM Used to communicate with the Backend system
 */
public class Communication {

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
    private String commando = "";

    public Communication() {
        status = Status.INITIALIZE;
    }

    public String getCommando() {
        return commando;
    }

    /**
     * Starts the client
     */
    public void startClient() {
        try {

            System.out.println("Connecting to " + serverName
                    + " on port " + PORT);
            client = new Socket(serverName, PORT);
            System.out.println(" Just connected to "
                    + client.getRemoteSocketAddress());
        } catch (IOException e) {
        }
        sleep(1000);
    }

    /**
     * Listens for input from the backend system
     */
    public void listen() {
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
            commando = input.readUTF();
            if (commando.equals("")) {
                input.reset();
                commando = "";
            } else {
                System.out.println("Recieved string " + commando + " from backend system!");
            }
        } catch (IOException e) {
        }
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
    public void sleep(int milliseconds) {
        try {
            operation.currentThread().sleep(milliseconds); //1000 milliseconds is one second.
        } catch (InterruptedException ex) {
            operation.currentThread().interrupt();
        }
    }
}
