/*
 * This file is part of ProDisFuzz, modified on 01.03.14 14:46.
 * Copyright (c) 2013-2014 Volker Nebelung <vnebelung@prodisfuzz.net>
 * This work is free. You can redistribute it and/or modify it under the
 * terms of the Do What The Fuck You Want To Public License, Version 2,
 * as published by Sam Hocevar. See the COPYING file for more details.
 */

package view.page.collect;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import model.Model;

import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;

public class TableRow {
    private final String name;
    private final String size;
    private final String lastModified;
    private final String sha256;
    private BooleanProperty selected;

    /**
     * Instantiates a new table row that represents the table model for the collect view.
     *
     * @param b  the checkbox flag
     * @param s1 the file name
     * @param l1 the file size
     * @param l2 the date the file was last modified
     * @param s2 the SHA-256 of the file
     */
    public TableRow(boolean b, String s1, long l1, long l2, String s2) {
        selected = new SimpleBooleanProperty(b);
        selected.addListener(new ChangeListener<Boolean>() {
            @Override
            public void changed(ObservableValue<? extends Boolean> o, Boolean b1, Boolean b2) {
                Model.INSTANCE.getCollectProcess().setSelected(name, selected.get());
            }
        });
        name = s1;
        DecimalFormat decimalFormat = new DecimalFormat("#0.00");
        size = decimalFormat.format(l1 / 1024.0);
        DateFormat dateFormat = new SimpleDateFormat("yy-MM-dd HH:mm");
        lastModified = dateFormat.format(l2);
        sha256 = s2;
    }

    /**
     * Returns the checkbox flag.
     *
     * @return true, if the checkbox is checked
     */
    public boolean isSelected() {
        return selected.get();
    }

    /**
     * Sets the select status of the table row indicating whether the user has selected the row for further
     * processing.
     *
     * @param b true, if the table row is selected
     */
    public void setSelected(boolean b) {
        selected.set(b);
    }

    /**
     * Returns the file name.
     *
     * @return the file name including the file extension
     */
    public String getName() {
        return name;
    }

    /**
     * Returns the file size.
     *
     * @return the file size in KB
     */
    public String getSize() {
        return size;
    }

    /**
     * Returns the date the file was last modified in format YY-MM-DD HH:MM
     *
     * @return the date the file was last modified
     */
    public String getLastModified() {
        return lastModified;
    }

    /**
     * Returns the SHA-256 of the file.
     *
     * @return the SHA-256 of the file
     */
    public String getSha256() {
        return sha256;
    }

    /**
     * Returns the boolean property for the select status.
     *
     * @return the select status property
     */
    public BooleanProperty selectedProperty() {
        return selected;
    }
}
