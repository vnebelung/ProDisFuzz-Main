/**
 * This file is part of the ProDisFuzz program.
 * (c) by Volker Nebelung, 2012
 */
package view;

import org.eclipse.swt.widgets.Display;

/**
 * The Class View.
 *
 * @author Volker Nebelung
 * @version 1.0
 *          <p/>
 *          Implements the view of the MVC pattern.
 */
public class View {

    /**
     * The window.
     */
    private final Window window;

    /**
     * Instantiates a new view.
     */
    public View() {
        final Display display = new Display();
        Display.setAppName("ProDisFuzz");
        window = new Window(display);
    }

    /**
     * Makes the default window visible.
     */
    public void show() {
        window.show();
    }

    /**
     * Gets the window.
     *
     * @return the window
     */
    public Window getWindow() {
        return window;
    }

}
