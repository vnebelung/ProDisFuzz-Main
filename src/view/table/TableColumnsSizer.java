/*
 * This file is part of ProDisFuzz, modified on 11.10.13 22:32.
 * Copyright (c) 2013 Volker Nebelung <vnebelung@prodisfuzz.net>
 * This work is free. You can redistribute it and/or modify it under the
 * terms of the Do What The Fuck You Want To Public License, Version 2,
 * as published by Sam Hocevar. See the COPYING file for more details.
 */

package view.table;

import javax.swing.*;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
import java.awt.*;

public abstract class TableColumnsSizer {
    private final static int MARGIN = 5;

    /**
     * Determines and sets the ideal widths for all columns of the JTable.
     *
     * @param t the table
     */
    public static void optimizeWidths(final JTable t) {
        for (int i = 0; i < t.getColumnModel().getColumnCount(); i++) {
            adjustColumn(t, i);
        }
    }

    /**
     * Adjust the width of the specified column in the table.
     *
     * @param t      the table
     * @param column the column index
     */
    private static void adjustColumn(final JTable t, final int column) {
        final TableColumn tableColumn = t.getColumnModel().getColumn(column);
        if (!tableColumn.getResizable()) {
            return;
        }
        final int columnHeaderWidth = getColumnHeaderWidth(t, column);
        final int columnDataWidth = getColumnDataWidth(t, column);
        final int preferredWidth = Math.max(columnHeaderWidth, columnDataWidth);
        updateTableColumn(t, column, preferredWidth);
    }

    /**
     * Gets the width based on the column name.
     *
     * @param t      the table
     * @param column the column index
     */
    private static int getColumnHeaderWidth(final JTable t, final int column) {
        final TableColumn tableColumn = t.getColumnModel().getColumn(column);
        final Object value = tableColumn.getHeaderValue();
        TableCellRenderer renderer = tableColumn.getHeaderRenderer();
        if (renderer == null) {
            renderer = t.getTableHeader().getDefaultRenderer();
        }
        final Component c = renderer.getTableCellRendererComponent(t, value, false, false, -1, column);
        return c.getPreferredSize().width;
    }

    /**
     * Gets the width based on the widest cell renderer for the given column.
     *
     * @param t      the table
     * @param column the column index
     */
    private static int getColumnDataWidth(final JTable t, final int column) {
        int preferredWidth = 0;
        final int maxWidth = t.getColumnModel().getColumn(column).getMaxWidth();
        for (int row = 0; row < t.getRowCount(); row++) {
            preferredWidth = Math.max(preferredWidth, getCellDataWidth(t, row, column));
            if (preferredWidth >= maxWidth) {
                break;
            }
        }
        return preferredWidth;
    }

    /**
     * Gets the preferred width for the specified cell.
     *
     * @param t      the table
     * @param row    the row index
     * @param column the column index
     */
    private static int getCellDataWidth(final JTable t, final int row, final int column) {
        final TableCellRenderer cellRenderer = t.getCellRenderer(row, column);
        final Component c = t.prepareRenderer(cellRenderer, row, column);
        return c.getPreferredSize().width + t.getIntercellSpacing().width;
    }

    /**
     * Update the TableColumn with the newly calculated width.
     *
     * @param t              the table
     * @param column         the column index
     * @param preferredWidth the preferred column width
     */
    private static void updateTableColumn(final JTable t, final int column, final int preferredWidth) {
        final TableColumn tableColumn = t.getColumnModel().getColumn(column);
        if (!tableColumn.getResizable()) {
            return;
        }
        final int width = Math.max(preferredWidth + MARGIN, tableColumn.getPreferredWidth());
        t.getTableHeader().setResizingColumn(tableColumn);
        tableColumn.setWidth(width);
    }
}
