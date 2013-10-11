/*
 * This file is part of ProDisFuzz, modified on 11.10.13 22:13.
 * Copyright (c) 2013 Volker Nebelung <vnebelung@prodisfuzz.net>
 * This work is free. You can redistribute it and/or modify it under the
 * terms of the Do What The Fuck You Want To Public License, Version 2,
 * as published by Sam Hocevar. See the COPYING file for more details.
 */
package view.icons;

import javax.swing.*;

public enum ImageRepository {

    INSTANCE;
    private final Icon errorIcon;
    private final Icon workingIcon;
    private final Icon okIcon;
    private final Icon logo;

    /**
     * Instantiates a new singleton image repository for managing all images and icons.
     */
    private ImageRepository() {
        errorIcon = new ImageIcon(getClass().getResource("/view/icons/error.png"));
        workingIcon = new ImageIcon(getClass().getResource("/view/icons/working.png"));
        okIcon = new ImageIcon(getClass().getResource("/view/icons/ok.png"));
        logo = new ImageIcon(getClass().getResource("/view/icons/logo.png"));
    }

    /**
     * Returns the warning icon.
     *
     * @return the warning icon
     */
    public Icon getWorkingIcon() {
        return workingIcon;
    }

    /**
     * Returns the error icon.
     *
     * @return the error icon
     */
    public Icon getErrorIcon() {
        return errorIcon;
    }

    /**
     * Returns the ok icon.
     *
     * @return the ok icon
     */
    public Icon getOkIcon() {
        return okIcon;
    }

    /**
     * Returns the ProDisFuzz logo.
     *
     * @return the logo
     */
    public Icon getLogo() {
        return logo;
    }

}
