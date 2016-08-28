/*
 * This file is part of ProDisFuzz, modified on 28.08.16 19:39.
 * Copyright (c) 2013-2016 Volker Nebelung <vnebelung@prodisfuzz.net>
 * This work is free. You can redistribute it and/or modify it under the
 * terms of the Do What The Fuck You Want To Public License, Version 2,
 * as published by Sam Hocevar. See the COPYING file for more details.
 */

package view.controls;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import model.Model;

import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Locale;

/**
 * This class is a JavaFX based table row that represents the table row model for the collect view.
 */
@SuppressWarnings("unused")
public class TableRow {
    private final String name;
    private final String size;
    private final String lastModified;
    private final String sha256;
    private final BooleanProperty selected;

    /**
     * Constructs a new table row.
     *
     * @param checkbox  the checkbox flag
     * @param fileName the file name
     * @param fileSize the file size
     * @param modified the date the file was last modified
     * @param sha256 the SHA-256 of the file
     */
    public TableRow(boolean checkbox, String fileName, long fileSize, long modified, String sha256) {
        selected = new SimpleBooleanProperty(checkbox);
        selected.addListener(new ChangeListener<Boolean>() {
            @Override
            public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
                Model.INSTANCE.getCollectProcess().toggleSelection(name, selected.get());
            }
        });
        name = fileName;
        DecimalFormat decimalFormat = new DecimalFormat("#0.00");
        size = decimalFormat.format(fileSize / 1024.0);
        DateFormat dateFormat = new SimpleDateFormat("yy-MM-dd HH:mm", Locale.ENGLISH);
        lastModified = dateFormat.format(modified);
        this.sha256 = sha256;
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
     * Sets the select status of the table row indicating whether the user has selected the row for further processing.
     *
     * @param selected true, if the table row is selected
     */
    public void setSelected(boolean selected) {
        this.selected.set(selected);
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
