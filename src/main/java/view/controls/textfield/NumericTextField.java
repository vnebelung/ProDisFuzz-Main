/*
 * This file is part of ProDisFuzz, modified on 01.03.14 14:49.
 * Copyright (c) 2013-2014 Volker Nebelung <vnebelung@prodisfuzz.net>
 * This work is free. You can redistribute it and/or modify it under the
 * terms of the Do What The Fuck You Want To Public License, Version 2,
 * as published by Sam Hocevar. See the COPYING file for more details.
 */

package view.controls.textfield;

import javafx.scene.control.TextField;

class NumericTextField extends TextField {

    @Override
    public void replaceText(int start, int end, String text) {
        // If the replaced text would end up being invalid, then simply ignore this call!
        if (!text.matches("\\D")) {
            super.replaceText(start, end, text);
        }
    }

    @Override
    public void replaceSelection(String text) {
        if (!text.matches("\\D")) {
            super.replaceSelection(text);
        }
    }
}
