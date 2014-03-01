/*
 * This file is part of ProDisFuzz, modified on 01.03.14 01:00.
 * Copyright (c) 2013-2014 Volker Nebelung <vnebelung@prodisfuzz.net>
 * This work is free. You can redistribute it and/or modify it under the
 * terms of the Do What The Fuck You Want To Public License, Version 2,
 * as published by Sam Hocevar. See the COPYING file for more details.
 */

package model.updater;

import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ReleaseInformation implements Comparable<ReleaseInformation> {

    private int number;
    private String name;
    private String date;
    private String requirements;
    private List<String> information;

    /**
     * Initializes a new release information that is responsible for encapsulating all information about a release.
     *
     * @param node the DOM node that contains the release information. Has to be conform to the XML update schema.
     */
    public ReleaseInformation(Node node) {
        NodeList nodeList = node.getChildNodes();
        for (int i = 0; i < nodeList.getLength(); i++) {
            switch (nodeList.item(i).getNodeName()) {
                case "number":
                    number = Integer.parseInt(nodeList.item(i).getFirstChild().getNodeValue());
                    break;
                case "name":
                    name = nodeList.item(i).getFirstChild().getNodeValue();
                    break;
                case "date":
                    date = nodeList.item(i).getFirstChild().getNodeValue();
                    break;
                case "requirements":
                    requirements = nodeList.item(i).getFirstChild().getNodeValue();
                    break;
                case "information":
                    NodeList nodeList1 = nodeList.item(i).getChildNodes();
                    information = new ArrayList<>(nodeList1.getLength());
                    for (int j = 0; j < nodeList1.getLength(); j++) {
                        if (nodeList1.item(j).getNodeName().equals("item")) {
                            information.add(nodeList1.item(j).getFirstChild().getNodeValue());
                        }
                    }
                    break;
                default:
                    break;
            }
        }
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
    public List<String> getInformation() {
        return Collections.unmodifiableList(information);
    }

    @Override
    public int compareTo(ReleaseInformation o) {
        return number - o.getNumber();
    }
}
