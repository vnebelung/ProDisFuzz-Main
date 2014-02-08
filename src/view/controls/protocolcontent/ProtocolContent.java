/*
 * This file is part of ProDisFuzz, modified on 08.02.14 22:54.
 * Copyright (c) 2013-2014 Volker Nebelung <vnebelung@prodisfuzz.net>
 * This work is free. You can redistribute it and/or modify it under the
 * terms of the Do What The Fuck You Want To Public License, Version 2,
 * as published by Sam Hocevar. See the COPYING file for more details.
 */

package view.controls.protocolcontent;

import javafx.fxml.FXML;
import javafx.scene.control.TextArea;
import javafx.scene.layout.GridPane;
import model.ProtocolPart;
import model.helper.Hex;
import view.window.ConnectionHelper;

import java.util.ArrayList;
import java.util.List;

public class ProtocolContent extends GridPane {

    private final static char VAR_CHAR = '.';
    private int lastHashCode;
    @FXML
    private TextArea protocolTextArea;

    /**
     * Instantiates a new protocol content area responsible for visualizing the process of learning the protocol
     * sequence.
     */
    public ProtocolContent() {
        super();
        ConnectionHelper.connect(getClass().getResource("protocolContent.fxml"), this);
    }

    /**
     * Updates the protocol text depending on the given protocol parts. If the given protocol parts are identical to
     * the current ones, no update will be done.
     *
     * @param protocolParts the protocol parts
     */
    public void addProtocolText(List<ProtocolPart> protocolParts) {
        if (protocolParts.hashCode() == lastHashCode) {
            return;
        }
        lastHashCode = protocolParts.hashCode();
        StringBuilder text = new StringBuilder();
        writeHeadline(text);
        List<Byte> bytes = partsToBytes(protocolParts);
        writeText(text, bytes);
        protocolTextArea.setText(text.toString());
    }

    private List<Byte> partsToBytes(List<ProtocolPart> protocolParts) {
        List<Byte> result = new ArrayList<>();
        for (ProtocolPart each : protocolParts) {
            result.addAll(each.getBytes());
        }
        return result;
    }

    /**
     * Writes the headline with the byte positions into the given string builder.
     *
     * @param sb the string builder the text will be written into
     */
    private void writeHeadline(StringBuilder sb) {
        sb.append("Offset   ");
        for (int i = 0; i < 16; i++) {
            sb.append(" 0");
            sb.append(Integer.toHexString(i));
        }
        sb.append(System.lineSeparator()).append(System.lineSeparator());
    }

    /**
     * Writes the text for the hexedit view into the given string builder. The data of the text depends on the given
     * bytes. Each line contains the incrementing offset, 16 hex values, and the ASCII representation of this 16 bytes.
     *
     * @param sb    the string builder the text will be written into
     * @param bytes the input bytes
     */
    private void writeText(StringBuilder sb, List<Byte> bytes) {
        int totalLines = (int) Math.ceil(bytes.size() / 16.0);
        for (int i = 0; i < totalLines; i++) {
            writeOffset(sb, i);
            sb.append(' ');
            int toIndex = Math.min((i + 1) * 16, bytes.size());
            List<Byte> line = bytes.subList(i * 16, toIndex);
            writeHex(sb, line);
            sb.append("  ");
            writeAscii(sb, line);
            sb.append(System.lineSeparator());
        }
    }

    /**
     * Writes the ASCII representation of the given bytes at the end of the given string builder.
     *
     * @param sb    the string builder the text will be written into
     * @param bytes the input bytes
     */
    private void writeAscii(StringBuilder sb, List<Byte> bytes) {
        for (Byte each : bytes) {
            if (each == null) {
                sb.append('.');
            } else {
                int decimal = Integer.parseInt(Hex.byte2Hex(each), 16);
                sb.append(decimal >= 32 && decimal <= 126 ? (char) decimal : '.');
            }
        }
    }

    /**
     * Writes the offset of the given line at the end of the given string builder.
     *
     * @param sb the string builder the text will be written into
     * @param i  the current line number
     */
    private void writeOffset(StringBuilder sb, int i) {
        String offset = Integer.toHexString(i * 16);
        for (int j = 0; j < 8 - offset.length(); j++) {
            sb.append(0);
        }
        sb.append(offset);
    }

    /**
     * Writes the hex representation of the given bytes at the end of the given string builder.
     *
     * @param sb    the string builder the text will be written into
     * @param bytes the input bytes
     */
    private void writeHex(StringBuilder sb, List<Byte> bytes) {
        for (Byte each : bytes) {
            sb.append(' ');
            if (each == null) {
                sb.append(VAR_CHAR).append(VAR_CHAR);
            } else {
                sb.append(Hex.byte2Hex(each));
            }
        }
        for (int i = 0; i < 16 - bytes.size(); i++) {
            sb.append("   ");
        }
    }

}
