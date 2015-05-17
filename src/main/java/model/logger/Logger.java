/*
 * This file is part of ProDisFuzz, modified on 08.03.14 19:41.
 * Copyright (c) 2013-2014 Volker Nebelung <vnebelung@prodisfuzz.net>
 * This work is free. You can redistribute it and/or modify it under the
 * terms of the Do What The Fuck You Want To Public License, Version 2,
 * as published by Sam Hocevar. See the COPYING file for more details.
 */

package model.logger;

import model.logger.Message.Type;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.*;

public class Logger extends Observable {

    private final List<Message> log;
    private final Map<Message, Boolean> returned;

    /**
     * Instantiates the logging mechanism.
     */
    public Logger() {
        super();
        log = new ArrayList<>();
        returned = new HashMap<>();
    }

    /**
     * Adds an info message to the log.
     *
     * @param text the message text to be add
     */
    public void info(String text) {
        addEntry(text, Type.INFO);
    }

    /**
     * Adds a success message to the log.
     *
     * @param text the message text to be add
     */
    public void fine(String text) {
        addEntry(text, Type.FINE);
    }

    /**
     * Adds an error message to the log.
     *
     * @param text the message text to be add
     */
    public void error(String text) {
        addEntry(text, Type.ERROR);
    }

    /**
     * Adds an error message based on an occurred exception to the log.
     *
     * @param throwable the throwable
     */
    public void error(Throwable throwable) {
        try (StringWriter sw = new StringWriter(); PrintWriter pw = new PrintWriter(sw)) {
            throwable.printStackTrace(pw);
            error(sw.toString());
        } catch (IOException ignored) {
            // Should not happen
        }
    }

    /**
     * Adds a warning message to the log.
     *
     * @param text the message text to be add
     */
    public void warning(String text) {
        addEntry(text, Type.WARNING);
    }

    /**
     * Adds a log entry with the given text and the given log type.
     *
     * @param text the text to log
     * @param type the log type
     */
    private void addEntry(String text, Type type) {
        Message message = new Message(text, type);
        log.add(message);
        returned.put(message, false);
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
        while (log.size() > 500) {
            returned.remove(log.get(0));
            log.remove(0);
        }
    }

    /**
     * Gets all not yet read log entries. The returned entries are set to "read" afterwards.
     *
     * @return the unread log entries or am empty list, if there are no unread entries
     */
    public Message[] getUnreadEntries() {
        List<Message> result = new LinkedList<>();
        for (int i = log.size() - 1; i >= 0; i--) {
            if (returned.get(log.get(i))) {
                break;
            } else {
                returned.put(log.get(i), true);
                result.add(0, log.get(i));
            }
        }
        return result.toArray(new Message[result.size()]);
    }

    /**
     * Resets the log and deletes all entries.
     */
    public void reset() {
        log.clear();
        returned.clear();
        spreadUpdate();
    }
}
