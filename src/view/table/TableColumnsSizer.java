/*
 * This file is part of ProDisFuzz, modified on 01.10.13 23:25.
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
     * @param table the table
     */
    public static void optimizeWidths(final JTable table) {
        for (int i = 0; i < table.getColumnModel().getColumnCount(); i++) {
            adjustColumn(table, i);
        }
    }

    /**
     * Adjust the width of the specified column in the table.
     *
     * @param column the column index
     */
    private static void adjustColumn(final JTable table, final int column) {
        final TableColumn tableColumn = table.getColumnModel().getColumn(column);
        if (!tableColumn.getResizable()) {
            return;
        }
        final int columnHeaderWidth = getColumnHeaderWidth(table, column);
        final int columnDataWidth = getColumnDataWidth(table, column);
        final int preferredWidth = Math.max(columnHeaderWidth, columnDataWidth);
        updateTableColumn(table, column, preferredWidth);
    }

    /**
     * Gets the width based on the column name.
     *
     * @param table  the table
     * @param column the column index
     */
    private static int getColumnHeaderWidth(final JTable table, final int column) {
        final TableColumn tableColumn = table.getColumnModel().getColumn(column);
        final Object value = tableColumn.getHeaderValue();
        TableCellRenderer renderer = tableColumn.getHeaderRenderer();
        if (renderer == null) {
            renderer = table.getTableHeader().getDefaultRenderer();
        }
        final Component c = renderer.getTableCellRendererComponent(table, value, false, false, -1, column);
        return c.getPreferredSize().width;
    }

    /**
     * Gets the width based on the widest cell renderer for the given column.
     *
     * @param table  the table
     * @param column the column index
     */
    private static int getColumnDataWidth(final JTable table, final int column) {
        int preferredWidth = 0;
        final int maxWidth = table.getColumnModel().getColumn(column).getMaxWidth();
        for (int row = 0; row < table.getRowCount(); row++) {
            preferredWidth = Math.max(preferredWidth, getCellDataWidth(table, row, column));
            if (preferredWidth >= maxWidth) {
                break;
            }
        }
        return preferredWidth;
    }

    /**
     * Gets the preferred width for the specified cell.
     *
     * @param table  the table
     * @param row    the row index
     * @param column the column index
     */
    private static int getCellDataWidth(final JTable table, final int row, final int column) {
        final TableCellRenderer cellRenderer = table.getCellRenderer(row, column);
        final Component c = table.prepareRenderer(cellRenderer, row, column);
        return c.getPreferredSize().width + table.getIntercellSpacing().width;
    }

    /**
     * Update the TableColumn with the newly calculated width.
     *
     * @param table          the table
     * @param column         the column index
     * @param preferredWidth the preferred column width
     */
    private static void updateTableColumn(final JTable table, final int column, final int preferredWidth) {
        final TableColumn tableColumn = table.getColumnModel().getColumn(column);
        if (!tableColumn.getResizable()) {
            return;
        }
        final int width = Math.max(preferredWidth + MARGIN, tableColumn.getPreferredWidth());
        table.getTableHeader().setResizingColumn(tableColumn);
        tableColumn.setWidth(width);
    }
}
