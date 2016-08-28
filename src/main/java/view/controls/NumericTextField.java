/*
 * This file is part of ProDisFuzz, modified on 28.08.16 19:39.
 * Copyright (c) 2013-2016 Volker Nebelung <vnebelung@prodisfuzz.net>
 * This work is free. You can redistribute it and/or modify it under the
 * terms of the Do What The Fuck You Want To Public License, Version 2,
 * as published by Sam Hocevar. See the COPYING file for more details.
 */

package view.controls;

import javafx.scene.control.TextField;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * This class is a JavaFX based numeric text field, responsible for displaying and formatting numeric text in a table
 * cell.
 */
@SuppressWarnings("ClassIndependentOfModule")
public class NumericTextField extends TextField {

    private static final Pattern PATTERN = Pattern.compile("\\D");

    @Override
    public void replaceText(int start, int end, String text) {
        Matcher matcher = PATTERN.matcher(text);
        if (!matcher.matches()) {
            super.replaceText(start, end, text);
        }
    }

    @Override
    public void replaceSelection(String replacement) {
        Matcher matcher = PATTERN.matcher(replacement);
        if (!matcher.matches()) {
            super.replaceSelection(replacement);
        }
    }
}
