/*
 * This file is part of ProDisFuzz, modified on 01.10.13 23:25.
 * Copyright (c) 2013 Volker Nebelung <vnebelung@prodisfuzz.net>
 * This work is free. You can redistribute it and/or modify it under the
 * terms of the Do What The Fuck You Want To Public License, Version 2,
 * as published by Sam Hocevar. See the COPYING file for more details.
 */

package view.table;

import model.process.CollectProcess;

import javax.swing.table.AbstractTableModel;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class CheckTableModel extends AbstractTableModel {
    private final CollectProcess process;
    private final String[] columnNames = {"Use", "File", "Modified", "Size (KB)", "SHA-256"};

    /**
     * Instantiates a new model.
     *
     * @param process the corresponding process
     */
    public CheckTableModel(final CollectProcess process) {
        super();
        this.process = process;
    }

    @Override
    public void setValueAt(final Object aValue, final int rowIndex, final int columnIndex) {
        // setValueAt is called twice when pressing a table checkbox. To avoid conflicts with a wrong checkbox status
        // and to update the model only once the variable fire is toggled
        if (columnIndex == 0 && rowIndex < process.getFiles().size()) {
            if (process.isSelected(rowIndex)) {
                process.setUnselected(rowIndex);
            } else {
                process.setSelected(rowIndex);
            }
        }

    }

    @Override
    public boolean isCellEditable(final int rowIndex, final int columnIndex) {
        return columnIndex == 0;
    }

    @Override
    public Class<?> getColumnClass(final int columnIndex) {
        switch (columnIndex) {
            case 0:
                return Boolean.class;
            case 3:
                return Number.class;
            default:
                return String.class;
        }
    }

    @Override
    public String getColumnName(final int col) {
        return columnNames[col];
    }

    @Override
    public int getRowCount() {
        return process.getFiles().size() == 0 ? 5 : process.getFiles().size();
    }

    @Override
    public int getColumnCount() {
        return 5;
    }

    @Override
    public Object getValueAt(final int rowIndex, final int columnIndex) {
        if (rowIndex >= process.getFiles().size()) {
            return "";
        }
        final DateFormat dateFormat = new SimpleDateFormat("yy-MM-dd HH:mm", Locale.getDefault());
        switch (columnIndex) {
            case 0:
                return process.isSelected(rowIndex);
            case 1:
                return process.getFiles().get(rowIndex).getName();
            case 2:
                return dateFormat.format(new Date(process.getFiles().get(rowIndex).getLastModified()));
            case 3:
                return process.getFiles().get(rowIndex).getSize() / 1024.0;
            case 4:
                return process.getFiles().get(rowIndex).getSHA256();
            default:
                return "";
        }
    }

}
