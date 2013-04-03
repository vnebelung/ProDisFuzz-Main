/**
 * This file is part of the ProDisFuzz program.
 * (c) by Volker Nebelung, 2012
 */
package view;

import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Display;

/**
 * The Class ImageRepository is responsible for managing all icons which can be
 * displayed in a composite.
 *
 * @author Volker Nebelung
 * @version 1.0
 */
public final class ImageRepository {

    /**
     * Private class attribute. Only 1 instance
     */
    private static final ImageRepository INSTANCE = new ImageRepository();

    /**
     * The error icon.
     */
    private final Image errorIcon = new Image(Display.getCurrent(), getClass()
            .getResourceAsStream("/icons/error.png"));

    /**
     * The warning icon.
     */
    private final Image workingIcon = new Image(Display.getCurrent(),
            getClass().getResourceAsStream("/icons/working.png"));

    /**
     * The ok icon.
     */
    private final Image okIcon = new Image(Display.getCurrent(), getClass()
            .getResourceAsStream("/icons/ok.png"));

    /**
     * The ProDisFuzz logo.
     */
    private final Image logo = new Image(Display.getCurrent(), getClass()
            .getResourceAsStream("/icons/logo.png"));

    /**
     * Singelton Constructor.
     */
    private ImageRepository() {
    }

    /**
     * Gets the only instance of ImagerRepository.
     */
    public static ImageRepository getInstance() {
        return INSTANCE;
    }

    /**
     * Gets the warning icon.
     *
     * @return the warning icon
     */
    public Image getWorkingIcon() {
        return workingIcon;
    }

    /**
     * Gets the error icon.
     *
     * @return the error icon
     */
    public Image getErrorIcon() {
        return errorIcon;
    }

    /**
     * Gets the ok icon.
     *
     * @return the ok icon
     */
    public Image getOkIcon() {
        return okIcon;
    }

    /**
     * Gets the logo.
     *
     * @return the logo
     */
    public Image getLogo() {
        return logo;
    }

}
