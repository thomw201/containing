package org.nhl.containing;

/**
 * Entry point
 */
public class Main {
    public static void main(String[] args) {
        Simulation app = new Simulation();
        app.start();
        Communication communication = new Communication();
        communication.Start();
    }
}
