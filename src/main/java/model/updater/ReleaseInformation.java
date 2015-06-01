/*
 * This file is part of ProDisFuzz, modified on 30.03.14 17:49.
 * Copyright (c) 2013-2014 Volker Nebelung <vnebelung@prodisfuzz.net>
 * This work is free. You can redistribute it and/or modify it under the
 * terms of the Do What The Fuck You Want To Public License, Version 2,
 * as published by Sam Hocevar. See the COPYING file for more details.
 */

package model.updater;

import java.util.Arrays;

public class ReleaseInformation {

    private final int number;
    private final String name;
    private final String date;
    private final String requirements;
    private final String[] information;

    /**
     * Initializes a new release information that is responsible for encapsulating all information about a release.
     *
     * @param name         the release name
     * @param date         the release date
     * @param information  the release notes
     * @param number       the version number
     * @param requirements the requirements
     */
    public ReleaseInformation(int number, String name, String date, String requirements, String... information) {
        this.number = number;
        this.name = name;
        this.date = date;
        this.requirements = requirements;
        this.information = Arrays.copyOf(information, information.length);
    }

    /**
     * Returns the number of this release.
     *
     * @return the release number
     */
    public int getNumber() {
        return number;
    }

    /**
     * Returns the name of this release.
     *
     * @return the release name
     */
    public String getName() {
        return name;
    }

    /**
     * Returns the creation date of this release.
     *
     * @return the date of the release
     */
    public String getDate() {
        return date;
    }

    /**
     * Returns the requirements of this release.
     *
     * @return the release requirements
     */
    public String getRequirements() {
        return requirements;
    }

    /**
     * Returns the information containing notes about changes, bug fixes, enhancements etc. of this release
     *
     * @return the release information
     */
    public String[] getInformation() {
        return information.clone();
    }

}
