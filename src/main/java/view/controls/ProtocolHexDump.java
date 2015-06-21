/*
 * This file is part of ProDisFuzz, modified on 03.04.14 20:25.
 * Copyright (c) 2013-2014 Volker Nebelung <vnebelung@prodisfuzz.net>
 * This work is free. You can redistribute it and/or modify it under the
 * terms of the Do What The Fuck You Want To Public License, Version 2,
 * as published by Sam Hocevar. See the COPYING file for more details.
 */

package view.controls;

import javafx.fxml.FXML;
import javafx.scene.layout.GridPane;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import model.protocol.ProtocolStructure;
import model.utilities.Hex;
import view.window.FxmlConnection;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;

public class ProtocolHexDump extends GridPane {

    private static final char VAR_CHAR = '*';
    private static final char NON_READABLE_CHAR = '.';
    private int lastHashCode;
    @FXML
    private TextFlow textFlow;

    /**
     * Instantiates a new protocol content area responsible for visualizing the process of learning the protocol
     * sequence.
     */
    public ProtocolHexDump() {
        super();
        //noinspection HardCodedStringLiteral
        FxmlConnection.connect(getClass().getResource("/fxml/protocolHexDump.fxml"), this);
        textFlow.getChildren().add(createHeader());
        textFlow.getChildren().add(createPlaceholder());
    }

    /**
     * Updates the protocol text depending on the given protocol structure. If the given protocol structure is identical
     * to the current one, no update will be done.
     *
     * @param protocolStructure the protocol structure
     */
    public void addProtocolText(ProtocolStructure protocolStructure) {
        if (protocolStructure.hashCode() == lastHashCode) {
            return;
        }
        lastHashCode = protocolStructure.hashCode();
        Collection<Text> newTexts = new ArrayList<>();
        newTexts.add(createHeader());
        if (protocolStructure.getSize() == 0) {
            newTexts.add(createPlaceholder());
        } else {
            newTexts.addAll(createText(protocolStructure));
        }
        textFlow.getChildren().clear();
        textFlow.getChildren().addAll(newTexts);
    }

    /**
     * Returns the headline with the byte positions for the hex view.
     *
     * @return the text containing the header
     */
    private static Text createHeader() {
        StringBuilder stringBuilder = new StringBuilder();
        //noinspection HardCodedStringLiteral
        stringBuilder.append("Offset   ");
        for (int i = 0; i < 16; i++) {
            stringBuilder.append(" 0");
            stringBuilder.append(Integer.toHexString(i));
        }
        stringBuilder.append(System.lineSeparator()).append(System.lineSeparator());
        Text result = new Text(stringBuilder.toString());
        //noinspection HardCodedStringLiteral
        result.getStyleClass().add("normal");
        return result;
    }

    /**
     * Returns a placeholder text for the case that no protocol information is available (yet).
     *
     * @return the text containing the placeholder
     */
    private static Text createPlaceholder() {
        Text result = new Text("No protocol data available.");
        //noinspection HardCodedStringLiteral
        result.getStyleClass().add("normal");
        return result;
    }

    /**
     * Returns the text for the hex view. The data of the text depends on the given protocol structure. Each line
     * contains the incrementing offset, 16 hex values, and the ASCII representation of this 16 bytes.
     *
     * @param protocolStructure the protocol structure
     * @return the text containing the offset, the hex representation and the ASCII representation
     */
    private static Collection<Text> createText(ProtocolStructure protocolStructure) {
        Byte[] bytes = protocolStructure.getBytes();
        Collection<Text> result = new ArrayList<>();
        //noinspection NumericCastThatLosesPrecision
        int totalLines = (int) Math.ceil(bytes.length / 16.0);
        for (int i = 0; i < totalLines; i++) {
            result.add(createOffset(i));
            int toIndex = Math.min((i + 1) * 16, bytes.length);
            Byte[] line = Arrays.copyOfRange(bytes, i * 16, toIndex);
            result.addAll(createHex(line));
            result.addAll(createAscii(line));
        }
        return result;
    }

    /**
     * Returns the ASCII representation of the given bytes.
     *
     * @param bytes the input bytes
     * @return the text containing the ASCII representation plus a linebreak at the end
     */
    private static Collection<Text> createAscii(Byte... bytes) {
        Collection<Text> result = new ArrayList<>();
        StringBuilder stringBuilder = new StringBuilder();
        boolean isPreByteNull = bytes[0] == null;
        for (Byte each : bytes) {
            if (isPreByteNull != (each == null)) {
                Text text = new Text(stringBuilder.toString());
                //noinspection HardCodedStringLiteral
                text.getStyleClass().add(isPreByteNull ? "var" : "fix");
                result.add(text);
                stringBuilder = new StringBuilder();
                isPreByteNull = each == null;
            }
            if (each == null) {
                stringBuilder.append(VAR_CHAR);
            } else {
                int decimal = Integer.parseInt(Hex.byte2Hex(each), 16);
                //noinspection NumericCastThatLosesPrecision
                stringBuilder.append(((decimal >= 32) && (decimal <= 126)) ? (char) decimal : NON_READABLE_CHAR);
            }
        }
        stringBuilder.append(System.lineSeparator());
        Text text = new Text(stringBuilder.toString());
        //noinspection HardCodedStringLiteral
        text.getStyleClass().add(isPreByteNull ? "var" : "fix");
        result.add(text);
        return result;
    }

    /**
     * Returns the offset of the given line.
     *
     * @param line the current line number
     * @return the text containing the offset plus an additional space at the end
     */
    private static Text createOffset(int line) {
        String offset = Integer.toHexString(line * 16);
        StringBuilder stringBuilder = new StringBuilder();
        for (int j = 0; j < (8 - offset.length()); j++) {
            stringBuilder.append(0);
        }
        stringBuilder.append(offset).append(' ');
        Text result = new Text(stringBuilder.toString());
        //noinspection HardCodedStringLiteral
        result.getStyleClass().add("normal");
        return result;
    }

    /**
     * Returns the hex representation of the given bytes.
     *
     * @param bytes the input bytes
     * @return the text containing the hex representation plus two additional spaces at the end
     */
    private static Collection<Text> createHex(Byte... bytes) {
        Collection<Text> result = new ArrayList<>();
        StringBuilder stringBuilder = new StringBuilder();
        boolean isPreByteNull = bytes[0] == null;
        for (Byte each : bytes) {
            if (isPreByteNull != (each == null)) {
                Text text = new Text(stringBuilder.toString());
                //noinspection HardCodedStringLiteral
                text.getStyleClass().add(isPreByteNull ? "var" : "fix");
                result.add(text);
                stringBuilder = new StringBuilder();
                isPreByteNull = each == null;
            }
            stringBuilder.append(' ');
            if (each == null) {
                stringBuilder.append(VAR_CHAR).append(VAR_CHAR);
            } else {
                stringBuilder.append(Hex.byte2Hex(each));
            }
        }
        for (int i = 0; i < (16 - bytes.length); i++) {
            stringBuilder.append("   ");
        }
        stringBuilder.append("  ");
        Text text = new Text(stringBuilder.toString());
        //noinspection HardCodedStringLiteral
        text.getStyleClass().add(isPreByteNull ? "var" : "fix");
        result.add(text);
        return result;
    }
}
