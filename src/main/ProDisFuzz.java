/**
 * This file is part of the ProDisFuzz program.
 * (c) by Volker Nebelung, 2012
 */
package main;

import controller.Controller;
import model.Model;
import view.View;

/**
 * @author Volker Nebelung
 * @version 1.0
 *          <p/>
 *          Implements the MVC pattern of ProDisFuzz.
 */
public final class ProDisFuzz {

    /**
     * The model component of the MVC pattern.
     */
    private static Model model;

    /**
     * The view component of the MVC pattern.
     */
    private static View view;

    /**
     * The controller component of the MVC pattern.
     */
    private static Controller controller;

    /**
     * Main method of ProDisFuzz.
     *
     * @param args the arguments
     */
    public static void main(final String[] args) {
        view = new View();
        model = new Model();
        controller = new Controller(model, view);
        // Adds the observers in the observer pattern
        controller.assignObservers();
        // Adds the listeners to all view elements
        controller.addListeners();
        // Sends an initial update signal to update the view
        model.spreadUpdate();
        // Displays the view
        view.show();
    }

    /**
     * Instantiates a new ProDisFuzz.
     */
    private ProDisFuzz() {
    }

}