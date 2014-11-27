package org.nhl.containing;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * Client.
 */
public class Client implements Runnable {
    private Socket socket;
    private final int portNumber = 6666;
    private final String serverName = "localhost";

    

    private ListenRunnable listenRunnable;
    private SendRunnable sendRunnable;

    private boolean running;

    public Client() {

    }

    @Override
    public void run() {
        try {
            // Open up the socket.
            socket = new Socket(serverName, portNumber);

            listenRunnable = new ListenRunnable(new BufferedReader(new InputStreamReader(socket.getInputStream())));
            sendRunnable = new SendRunnable(new PrintWriter(socket.getOutputStream(), true));

            Thread listenThread = new Thread(listenRunnable);
            listenThread.setName("ListenThread");
            Thread sendThread = new Thread(sendRunnable);
            sendThread.setName("SendThread");

            listenThread.start();
            sendThread.start();
        } catch (IOException e) {
            e.printStackTrace();
        }

        running = true;

        while (running) {
            try {
                // Do nothing.
                Thread.sleep(1000);
                // In case the client shut down the listener, shut down everything.
                if (!listenRunnable.isRunning()) {
                    this.stop();
                }
                System.out.println("Still alive");
            } catch (Throwable e) {
                e.printStackTrace();
            }
        }
    }

    public void stop() {
        try {
            listenRunnable.stop();
        } catch (Throwable e) {
        }
        try {
            sendRunnable.stop();
        } catch (Throwable e) {
        }
        running = false;
    }

    public String getMessage() {
        return listenRunnable.getMessage();
    }

    public void writeMessage(String message) {
        sendRunnable.writeMessage(message);
    }
}
