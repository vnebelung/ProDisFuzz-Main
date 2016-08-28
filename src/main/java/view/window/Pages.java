/*
 * This file is part of ProDisFuzz, modified on 28.08.16 19:39.
 * Copyright (c) 2013-2016 Volker Nebelung <vnebelung@prodisfuzz.net>
 * This work is free. You can redistribute it and/or modify it under the
 * terms of the Do What The Fuck You Want To Public License, Version 2,
 * as published by Sam Hocevar. See the COPYING file for more details.
 */

package view.window;

import javafx.scene.Node;

import java.util.ArrayList;
import java.util.List;

/**
 * This class represents the JavaFX based pages. Each page has a successor and a predecessor page (except for the first
 * and last page), so that it is possible to navigate through pages.
 */
public class Pages {

    private final List<Node> pages;
    private int currentIndex;

    /**
     * Constructs a new pages list. The current page is set to the first page.
     */
    public Pages() {
        super();
        pages = new ArrayList<>();
        currentIndex = 0;
    }

    /**
     * Adds a page to the end of the list. The current page pointer is not changed by this call.
     *
     * @param node the page as a node
     */
    public void add(Node node) {
        pages.add(node);
    }

    /**
     * Returns the current page referenced by the page pointer.
     *
     * @return the current page
     */
    public Node getCurrent() {
        return pages.get(currentIndex);
    }

    /**
     * Sets the current page pointer to the first page and returns it. If there are no pages the return value will be
     * null.
     *
     * @return the first page
     */
    public Node getFirst() {
        currentIndex = 0;
        return pages.get(currentIndex);
    }

    /**
     * Increases the current page pointer and returns that page. If there are no successors of the current page the
     * return value will be null.
     *
     * @return the next page
     */
    public Node getNext() {
        if (currentIndex < (pages.size() - 1)) {
            currentIndex++;
            return pages.get(currentIndex);
        } else {
            //noinspection ReturnOfNull
            return null;
        }
    }

    /**
     * Decreases the current page pointer and returns that page. If there are no predecessors of the current page the
     * return value will be null.
     *
     * @return the previous page
     */
    public Node getPrevious() {
        if (currentIndex > 0) {
            currentIndex--;
            return pages.get(currentIndex);
        } else {
            //noinspection ReturnOfNull
            return null;
        }
    }

    /**
     * Returns whether the current page has a succeeding page.
     *
     * @return true if the current page has a succeeding page
     */
    public boolean hasNext() {
        return currentIndex < (pages.size() - 1);
    }

    /**
     * Returns whether the current page has a preceding page.
     *
     * @return true if the current page has a preceding page
     */
    public boolean hasPrevious() {
        return currentIndex > 0;
    }


}
