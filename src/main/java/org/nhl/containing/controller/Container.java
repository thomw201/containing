/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.nhl.containing.controller;

/**
 * Data model for a container.
 * 
 * @author Ruben Bakker
 */
public class Container {
    private int arrivalDate; // Probably change to DateTime format.
    private String arrivalTransportType; // Method of transport through which the container was delivered.
    private String arrivalCompany;
    private String owner;
    private int departureDate; // Probably change to DateTime format.
    private String departureTransportType; // Method of transport through which the container must be dispatched.
    private String departureCompany;
    private float weight; // Weight in kilogrammes.
    private String contents; // What's in the container?
    
    public Container(int arrivalDate, String arrivalTransportType, String arrivalCompany, String owner,
            int departureDate, String departureTransportType, String departureCompany, float weight, String contents) {
        this.arrivalDate = arrivalDate;
        this.arrivalTransportType = arrivalTransportType;
        this.arrivalCompany = arrivalCompany;
        this.owner = owner;
        this.departureDate = departureDate;
        this.departureTransportType = departureTransportType;
        this.departureCompany = departureCompany;
        this.weight = weight;
        this.contents = contents;
    }
}
