/*
 * This file is part of ProDisFuzz, modified on 03.10.13 22:25.
 * Copyright (c) 2013 Volker Nebelung <vnebelung@prodisfuzz.net>
 * This work is free. You can redistribute it and/or modify it under the
 * terms of the Do What The Fuck You Want To Public License, Version 2,
 * as published by Sam Hocevar. See the COPYING file for more details.
 */

package view.component;

import model.Model;
import model.ProtocolPart;

import javax.swing.*;
import javax.swing.text.*;
import java.awt.*;
import java.util.List;

public class ProtocolPane extends JTextPane {

    public final static Color COLOR_FIX = new Color(224, 192, 255);
    public final static Color COLOR_VAR = new Color(192, 224, 255);
    private final static char[] HEX_CHARS = "0123456789abcdef".toCharArray();
    private final static String SPACE = "      ";
    private final static char VAR_CHAR = '.';
    private final int bytesPerLine;
    private final SimpleAttributeSet fixStyle;
    private final SimpleAttributeSet varStyle;
    private int lastHashCode;
    private int numOfBytes;

    /**
     * Instantiates a new protocol pane that is responsible for visualizing the protocol structure.
     *
     * @param bytesPerLine the number of bytes that fit into one line of the document's parent1
     */
    public ProtocolPane(final int bytesPerLine) {
        super();
        setFont(new Font("Monospaced", Font.PLAIN, 12));
        this.bytesPerLine = bytesPerLine;

        fixStyle = new SimpleAttributeSet();
        StyleConstants.setBackground(fixStyle, COLOR_FIX);

        varStyle = new SimpleAttributeSet();
        StyleConstants.setBackground(varStyle, COLOR_VAR);
    }

    /**
     * Updates the protocol text.
     *
     * @param protocolParts the protocol parts
     */
    public void addProtocolText(final List<ProtocolPart> protocolParts) {
        if (protocolParts.hashCode() == lastHashCode) {
            return;
        }
        lastHashCode = protocolParts.hashCode();
        String text = hexText(protocolParts);
        text = separatorText(text);
        text = asciiText(text);
        final StyledDocument styledDoc = colorStyledDocument(text);
        setStyledDocument(styledDoc);
    }

    /**
     * Colors the hex-encoded and ASCII-encoded text blocks depending on their protocol part types.
     *
     * @param text the input text
     * @return the styled document containing the colored characters
     */
    private StyledDocument colorStyledDocument(final String text) {
        final StyledDocument styledDoc = new DefaultStyledDocument();
        try {
            styledDoc.insertString(0, text, null);
        } catch (BadLocationException e) {
            Model.INSTANCE.getLogger().error(e);
        }
        int index = text.indexOf(System.lineSeparator());
        while (index > 0) {
            for (int i = 0; i < bytesPerLine; i++) {
                final int positionHex = index - 4 * bytesPerLine - SPACE.length() + i * 3;
                final int positionAscii = index - bytesPerLine + i;
                if (text.charAt(positionHex) == VAR_CHAR) {
                    styledDoc.setCharacterAttributes(positionHex, text.charAt(positionHex + 3) == VAR_CHAR ? 3 : 2,
                            varStyle, true);
                    styledDoc.setCharacterAttributes(positionAscii, 1, varStyle, true);
                } else if (isHex(text.charAt(positionHex))) {
                    styledDoc.setCharacterAttributes(positionHex, isHex(text.charAt(positionHex + 3)) ? 3 : 2,
                            fixStyle, true);
                    styledDoc.setCharacterAttributes(positionAscii, 1, fixStyle, true);
                }
            }
            index = text.indexOf(System.lineSeparator(), index + 1);
        }
        return styledDoc;
    }

    /**
     * Checks whether the given character is part of an hex-encoded string, that means it is equal to 0-9a-f.
     *
     * @param c the input character
     * @return true if the character is in the range of 0-9a-f
     */
    private boolean isHex(final char c) {
        for (final char hex : HEX_CHARS) {
            if (hex == c) {
                return true;
            }
        }
        return false;
    }

    /**
     * Generates the ASCII block after the space block.
     *
     * @param text the hex-encoded text with spaces
     * @return the text with hex and ascii chars
     */
    private String asciiText(final String text) {
        final StringBuilder hexText = new StringBuilder(text);
        int index = hexText.indexOf(SPACE + System.getProperty("line.separator"));
        while (index >= 0) {
            final StringBuilder ascii = new StringBuilder(bytesPerLine);
            for (int i = index - 3 * bytesPerLine; i < index; i += 3) {
                ascii.append(asciiValue(String.valueOf(hexText.charAt(i)) + String.valueOf(hexText.charAt(i + 1))));
            }
            hexText.insert(index + SPACE.length(), ascii.toString());
            index += ascii.length();
            index = hexText.indexOf(SPACE + System.getProperty("line.separator"), index + 1);
        }
        return hexText.toString();
    }

    /**
     * Returns an ASCII char for an string-encoded hex value.
     *
     * @param hex the hex value
     * @return the ASCII char
     */
    private char asciiValue(final String hex) {
        try {
            final int decimal = Integer.parseInt(hex, 16);
            return decimal >= 32 && decimal <= 126 ? (char) decimal : '.';
        } catch (NumberFormatException e) {
            return " ".equals(hex) ? ' ' : '.';
        }
    }

    /**
     * Adds spaces and line breaks into a given text.
     *
     * @param text the text
     * @return the text including spaces and line breaks
     */
    private String separatorText(final String text) {
        final StringBuilder spaceText = new StringBuilder(text);
        int position = 3 * bytesPerLine;
        while (position < spaceText.length()) {
            spaceText.insert(position, SPACE);
            position += SPACE.length();
            spaceText.insert(position, System.lineSeparator());
            position += System.lineSeparator().length();
            position += 3 * bytesPerLine;
        }
        int spaceFill = numOfBytes % bytesPerLine;
        if (spaceFill > 0) {
            spaceFill = bytesPerLine - spaceFill;
            for (int i = 0; i < spaceFill; i++) {
                spaceText.append("   ");
            }
            spaceText.append(SPACE);
            spaceText.append(System.lineSeparator());
        }
        return spaceText.toString();
    }

    /**
     * Reads in all given protocol parts and returns a string with hex-encoded byte values.
     *
     * @param protocolParts the protocol parts containing the byte values
     * @return the text with hex-encoded byte values
     */
    private String hexText(final List<ProtocolPart> protocolParts) {
        final StringBuilder text = new StringBuilder();
        numOfBytes = 0;
        for (final ProtocolPart protocolPart : protocolParts) {
            for (final Byte currentByte : protocolPart.getBytes()) {
                text.append(currentByte == null ? String.valueOf(VAR_CHAR) + String.valueOf(VAR_CHAR) : hexValue
                        (currentByte));
                text.append(' ');
                numOfBytes++;
            }
        }
        return text.toString();
    }

    /**
     * Returns the hex value for a given byte.
     *
     * @param b the byte
     * @return the hex value as a sting
     */
    private String hexValue(final byte b) {
        return String.valueOf(HEX_CHARS[(b & 0xF0) >>> 4]) + String.valueOf(HEX_CHARS[b & 0x0F]);
    }
}
