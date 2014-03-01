/*
 * This file is part of ProDisFuzz, modified on 01.03.14 10:47.
 * Copyright (c) 2013-2014 Volker Nebelung <vnebelung@prodisfuzz.net>
 * This work is free. You can redistribute it and/or modify it under the
 * terms of the Do What The Fuck You Want To Public License, Version 2,
 * as published by Sam Hocevar. See the COPYING file for more details.
 */

package model.logger;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.LinkedList;
import java.util.List;
import java.util.Observable;

public class Logger extends Observable {

    private final List<Message> log;

    /**
     * Instantiates the logging mechanism.
     */
    public Logger() {
        super();
        log = new LinkedList<>();
    }

    /**
     * Adds an info message to the log.
     *
     * @param s the message string to be add
     */
    public void info(String s) {
        log.add(new Message(s, Message.Type.INFO));
        prune();
        spreadUpdate();
    }

    /**
     * Adds a success message to the log.
     *
     * @param s the message string to be add
     */
    public void fine(String s) {
        log.add(new Message(s, Message.Type.FINE));
        prune();
        spreadUpdate();
    }

    /**
     * Adds an error message to the log.
     *
     * @param s the message string to be add
     */
    public void error(String s) {
        log.add(new Message(s, Message.Type.ERROR));
        prune();
        spreadUpdate();
    }

    /**
     * Adds an error message based on an occurred exception to the log.
     *
     * @param t the throwable
     */
    public void error(Throwable t) {
        try (StringWriter sw = new StringWriter()) {
            try (PrintWriter pw = new PrintWriter(sw)) {
                t.printStackTrace(pw);
                error(sw.toString());
            }
            t.printStackTrace();
        } catch (IOException e) {
            // Should not happen
        }
    }

    /**
     * Adds a warning message to the log.
     *
     * @param s the message string to be add
     */
    public void warning(String s) {
        log.add(new Message(s, Message.Type.WARNING));
        prune();
        spreadUpdate();
    }

    /**
     * Notifies all observers about an update.
     */
    private void spreadUpdate() {
        setChanged();
        notifyObservers();
    }

    /**
     * Keeps the number of entries of the log under the defined size of 500.
     */
    private void prune() {
        int oversize = log.size() - 500;
        if (oversize > 0) {
            log.subList(0, oversize).clear();
        }
    }

    /**
     * Gets the last log entry.
     *
     * @return the last log entry or null if there are no entries
     */
    public Message getLastEntry() {
        return log.isEmpty() ? null : log.get(log.size() - 1);
    }

    /**
     * Resets the log and deletes all entries.
     */
    public void reset() {
        log.clear();
        spreadUpdate();
    }
}
