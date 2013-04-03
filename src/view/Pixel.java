/**
 * This file is part of the ProDisFuzz program.
 * (c) by Volker Nebelung, 2012
 */
package view;

import model.ProtocolPart;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.widgets.Display;

import java.util.List;

/**
 * The Class Pixel implements a single pixel of the protocol image.
 *
 * @author Volker Nebelung
 * @version 1.0
 */
public class Pixel {

    /**
     * The Constant COLOR_INV.
     */
    public final static Color COLOR_INV = new Color(Display.getCurrent(), 96,
            192, 255);

    /**
     * The Constant COLOR_VAR.
     */
    public final static Color COLOR_VAR = new Color(Display.getCurrent(), 192,
            96, 255);

    /**
     * The Constant COLOR_UNDEF.
     */
    public final static Color COLOR_UNDEF = new Color(Display.getCurrent(), 96,
            96, 96);

    /**
     * The boolean flag indicating the pixel is highlighted.
     */
    private boolean isHighlited; // NOPMD

    /**
     * The protocol part the pixel represents.
     */
    private final ProtocolPart part;

    /**
     * Instantiates a new pixel.
     *
     * @param part the protocol part the pixel represents
     */
    public Pixel(final ProtocolPart part) {
        this.part = part;
        this.isHighlited = false;
    }

    /**
     * Gets the color.
     *
     * @return the color
     */
    public Color getColor() {
        Color color;
        switch (part.getType()) {
            case VAR:
                color = COLOR_VAR;
                break;
            case FIXED:
                color = COLOR_INV;
                break;
            default:
                color = COLOR_UNDEF;
                break;
        }
        return color;
    }

    /**
     * Gets the content string.
     *
     * @return the content
     */
    public String getContent() {
        final StringBuffer buffer = new StringBuffer();
        switch (part.getType()) {
            case FIXED:
                // If the part is UNVAR there is only one byte list, so all bytes
                // are appended
                for (List<Byte> bytes : part.getContent()) {
                    for (Byte charByte : bytes) {
                        buffer.append((char) charByte.byteValue());
                    }
                }
                break;
            case VAR:
                // If the part is VAR, for the number of the maximum length of the
                // part a special chars is appended
                for (int i = 0; i < part.getMaxLength(); i++) {
                    buffer.append('�');
                }
                break;
            default:
                break;
        }
        return buffer.toString();
    }

    /**
     * Returns true or false whether the pixel should be displayed as highlighted or not.
     *
     * @return true if pixel is highlighted, false otherwise
     */
    public boolean isHighlited() {
        return isHighlited;
    }

    /**
     * Sets the highlight-flag of the pixel to the given value.
     *
     * @param isHighlited the flag whether the pixel shall be highlighted or not
     */
    public void setHighlited(final boolean isHighlited) {
        this.isHighlited = isHighlited;
    }

    /**
     * Gets the protocol part represented by this pxiel.
     *
     * @return the protocol part
     */
    public ProtocolPart getPart() {
        return part;
    }
}
