/*
 * This file is part of ProDisFuzz, modified on 01.02.14 18:25.
 * Copyright (c) 2013-2014 Volker Nebelung <vnebelung@prodisfuzz.net>
 * This work is free. You can redistribute it and/or modify it under the
 * terms of the Do What The Fuck You Want To Public License, Version 2,
 * as published by Sam Hocevar. See the COPYING file for more details.
 */

package view.controls;


import javafx.geometry.Pos;
import javafx.scene.control.TableCell;

public class NumericTableCell<TableRow, String> extends TableCell<TableRow, String> {
    @Override
    protected void updateItem(String item, boolean empty) {
        if (item == null) {
            return;
        }
        super.updateItem(item, empty);
        setText(item.toString());
        setAlignment(Pos.CENTER_RIGHT);
    }
}
