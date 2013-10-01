/*
 * This file is part of ProDisFuzz, modified on 01.10.13 23:28.
 * Copyright (c) 2013 Volker Nebelung <vnebelung@prodisfuzz.net>
 * This work is free. You can redistribute it and/or modify it under the
 * terms of the Do What The Fuck You Want To Public License, Version 2,
 * as published by Sam Hocevar. See the COPYING file for more details.
 */

package view.table;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;

public class CheckTableBooleanRenderer extends DefaultTableCellRenderer {

    @Override
    public Component getTableCellRendererComponent(final JTable table, Object value, final boolean isSelected,
                                                   final boolean hasFocus, final int row, final int column) {
        if (value.toString().isEmpty()) {
            final JLabel comp = (JLabel) super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row,
                    column);
            comp.setText(null);
            return comp;
        } else {
            return table.getDefaultRenderer(table.getColumnClass(column)).getTableCellRendererComponent(table, value,
                    isSelected, hasFocus, row, column);
        }
    }
}
