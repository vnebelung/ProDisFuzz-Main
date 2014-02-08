/*
 * This file is part of ProDisFuzz, modified on 16.12.13 21:10.
 * Copyright (c) 2013-2014 Volker Nebelung <vnebelung@prodisfuzz.net>
 * This work is free. You can redistribute it and/or modify it under the
 * terms of the Do What The Fuck You Want To Public License, Version 2,
 * as published by Sam Hocevar. See the COPYING file for more details.
 */
package view.icons;

import javafx.scene.image.Image;

public enum ImageRepository {

    INSTANCE;
    private final Image errorIcon;
    private final Image workingIcon;
    private final Image okIcon;

    /**
     * Instantiates a new singleton image repository for managing all images and icons.
     */
    private ImageRepository() {
        errorIcon = new Image("/view/icons/error.png");
        workingIcon = new Image("/view/icons/working.png");
        okIcon = new Image("/view/icons/ok.png");
    }

    /**
     * Returns the warning icon.
     *
     * @return the warning icon
     */
    public Image getWorkingIcon() {
        return workingIcon;
    }

    /**
     * Returns the error icon.
     *
     * @return the error icon
     */
    public Image getErrorIcon() {
        return errorIcon;
    }

    /**
     * Returns the ok icon.
     *
     * @return the ok icon
     */
    public Image getOkIcon() {
        return okIcon;
    }

}
