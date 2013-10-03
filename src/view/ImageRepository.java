/*
 * This file is part of ProDisFuzz, modified on 03.10.13 21:34.
 * Copyright (c) 2013 Volker Nebelung <vnebelung@prodisfuzz.net>
 * This work is free. You can redistribute it and/or modify it under the
 * terms of the Do What The Fuck You Want To Public License, Version 2,
 * as published by Sam Hocevar. See the COPYING file for more details.
 */
package view;

import javax.swing.*;

public enum ImageRepository {

    INSTANCE;
    private final Icon errorIcon;
    private final Icon workingIcon;
    private final Icon okIcon;
    private final Icon logo;

    /**
     * Singelton constructor.
     */
    private ImageRepository() {
        errorIcon = new ImageIcon(getClass().getResource("/icons/error.png"));
        workingIcon = new ImageIcon(getClass().getResource("/icons/working.png"));
        okIcon = new ImageIcon(getClass().getResource("/icons/ok.png"));
        logo = new ImageIcon(getClass().getResource("/icons/logo.png"));
    }

    /**
     * Gets the warning icon.
     *
     * @return the warning icon
     */
    public Icon getWorkingIcon() {
        return workingIcon;
    }

    /**
     * Gets the error icon.
     *
     * @return the error icon
     */
    public Icon getErrorIcon() {
        return errorIcon;
    }

    /**
     * Gets the ok icon.
     *
     * @return the ok icon
     */
    public Icon getOkIcon() {
        return okIcon;
    }

    /**
     * Gets the logo.
     *
     * @return the logo
     */
    public Icon getLogo() {
        return logo;
    }

}
