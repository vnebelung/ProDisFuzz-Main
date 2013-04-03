/**
 * This file is part of the ProDisFuzz program.
 * (c) by Volker Nebelung, 2012
 */
package view;

import model.ProtocolPart;
import model.ProtocolPart.Type;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Display;

import java.util.ArrayList;
import java.util.List;

/**
 * The Class ProtocolCanvas implements the image which represents the structure
 * of the protocol by drawing it pixel by pixel.
 *
 * @author Volker Nebelung
 * @version 1.0
 */
public class ProtocolCanvas extends Canvas { // NOPMD

    /**
     * The Constant REC_SIZE.
     */
    private final static int REC_SIZE = 8;

    /**
     * The pixels which contains information about the protocol structure.
     */
    private final List<Pixel> pixels;

    /**
     * The number of pixel columns.
     */
    private int numOfCols;

    /**
     * The default border color for pixels.
     */
    private final static Color DEFAULT_BORDER_COLOR = new Color( // NOPMD
            Display.getCurrent(), 255, 255, 255);

    /**
     * The highlighted border color for pixels.
     */
    private final static Color HIGHLIGHT_BORDER_COLOR = new Color( // NOPMD
            Display.getCurrent(), 0, 0, 0);

    /**
     * The number of pixels for the default image.
     */
    private static final int NUM_OF_DEFAULT_PIXELS = 250; // NOPMD

    /**
     * Instantiates a new protocol canvas.
     *
     * @param parent the parent
     * @param style  the style
     */
    public ProtocolCanvas(final ScrolledComposite parent, final int style) {
        super(parent, style);
        pixels = new ArrayList<Pixel>();
        // Adds the paint listener that is necessary to draw the image
        this.addPaintListener(new PaintListener() {
            @Override
            public void paintControl(final PaintEvent event) {
                drawImage(event);
            }
        });
    }

    /**
     * Draws the protocol image pixel by pixel.
     *
     * @param event the paint event
     */
    private void drawImage(final PaintEvent event) {
        // Compute the number of pixels in a row depending on the size of the
        // parent
        numOfCols = (getParentWidth() - 1) / REC_SIZE;
        if (numOfCols > 0) {
            int numOfRows;
            event.gc.setForeground(DEFAULT_BORDER_COLOR);
            // If there are no pixels yet, a specific number of default pixels
            // is drawn, otherwise the pixels of the list are drawn
            if (pixels.isEmpty()) {
                numOfRows = (int) Math.ceil((double) NUM_OF_DEFAULT_PIXELS
                        / numOfCols);
                setSize(numOfCols * REC_SIZE + 1, numOfRows * REC_SIZE + 1);
                drawDefaultPixels(event);
            } else {
                numOfRows = (int) Math.ceil((double) pixels.size() / numOfCols);
                setSize(numOfCols * REC_SIZE + 1, numOfRows * REC_SIZE + 1);
                drawPixels(event);
            }
        }
    }

    /**
     * Draws all pixels depending on the protocol parts which have been learned
     * so far.
     *
     * @param event the paint event
     */
    private void drawPixels(final PaintEvent event) {
        int currentColumn = 0;
        int currentRow = 0;
        int currentXPos;
        int currentYPos;
        for (int i = 0; i < pixels.size(); i++) {
            // Determine the position in the matrix for drawing the pixel
            currentXPos = REC_SIZE * currentColumn;
            currentYPos = REC_SIZE * currentRow;
            // Draw the rectangle with its colors
            event.gc.drawRectangle(currentXPos, currentYPos, REC_SIZE, REC_SIZE);
            event.gc.setBackground(pixels.get(i).getColor());
            event.gc.fillRectangle(currentXPos + 1, currentYPos + 1,
                    REC_SIZE - 1, REC_SIZE - 1);
            // Determine the next column and row
            currentColumn = (currentColumn + 1) % numOfCols;
            if (currentColumn == 0) {
                currentRow++;
            }
        }
        currentColumn = 0;
        currentRow = 0;
        event.gc.setForeground(HIGHLIGHT_BORDER_COLOR);
        for (int i = 0; i < pixels.size(); i++) {
            // Determine the current position in the matrix
            currentXPos = REC_SIZE * currentColumn;
            currentYPos = REC_SIZE * currentRow;
            // If the pixel is highlighted the border around the pixel is paint
            // over
            if (pixels.get(i).isHighlited()) {
                event.gc.drawRectangle(currentXPos, currentYPos, REC_SIZE,
                        REC_SIZE);
            }
            // Determine the next column and row
            currentColumn = (currentColumn + 1) % numOfCols;
            if (currentColumn == 0) {
                currentRow++;
            }
        }
    }

    /**
     * Draws 250 pixels in the default color 'undefined'. This method should be
     * invoked only if the learning process hasn't started yet.
     *
     * @param event the paint event
     */
    private void drawDefaultPixels(final PaintEvent event) {
        int currentColumn = 0;
        int currentRow = 0;
        int currentXPos;
        int currentYPos;
        event.gc.setBackground(Pixel.COLOR_UNDEF);
        for (int i = 0; i < NUM_OF_DEFAULT_PIXELS; i++) {
            // Determine the current position in the matrix
            currentXPos = REC_SIZE * currentColumn;
            currentYPos = REC_SIZE * currentRow;
            // Draw the rectangle with its color
            event.gc.drawRectangle(currentXPos, currentYPos, REC_SIZE, REC_SIZE);
            event.gc.fillRectangle(currentXPos + 1, currentYPos + 1,
                    REC_SIZE - 1, REC_SIZE - 1);
            // Determine the next column and row
            currentColumn = (currentColumn + 1) % numOfCols;
            if (currentColumn == 0) {
                currentRow++;
            }
        }
    }

    /**
     * Initializes the pixels depending on the protocol structure.
     *
     * @param protocolParts the list of protocol parts
     */
    private void initPixels(final List<ProtocolPart> protocolParts) { // NOPMD
        pixels.clear();
        // Fill the list with pixels
        for (ProtocolPart part : protocolParts) {
            for (int i = 0; i < part.getMaxLength(); i++) {
                pixels.add(new Pixel(part)); // NOPMD
            }
        }
    }

    /**
     * Sets the protocol parts.
     *
     * @param protocolParts the new protocol parts
     */
    public void setProtocolParts(final List<ProtocolPart> protocolParts) {
        initPixels(protocolParts);
        redraw();
        update();
    }

    /**
     * Returns the width of the canvas' parent widget.
     *
     * @return the width of the parent widget
     */
    private int getParentWidth() {
        return getParent().getClientArea().width;
    }

    /**
     * Highlights the current pixel and its neighbours of the same block by
     * drawing a new border color.
     *
     * @param x the x position
     * @param y the y position
     */
    public void highlightPixels(final int x, final int y) { // NOPMD
        // Remove all previous highlights
        unhighlightPixels();
        // Determines the position in the matrix and the corresponding index
        final int column = x / REC_SIZE;
        final int row = y / REC_SIZE;
        final int index = numOfCols * row + column;
        // If the index is a valid one, proceed
        if (index < pixels.size()) {
            final Color color = pixels.get(index).getColor();
            pixels.get(index).setHighlited(true);
            // Highlights all preceding pixels with the same color as they
            // belong to the same protocol part
            highlightPrePixels(index, color);
            // Highlights all succeeding pixels with the same color as they
            // belong to the same protocol part
            highlightSuccPixels(index, color);
            redraw();
        }
    }

    /**
     * Highlights all preceding pixels with the same color like the given pixel.
     *
     * @param index the index of the origin pixel
     * @param color the color of the origin pixel
     */
    private void highlightPrePixels(final int index, final Color color) {
        for (int i = 1; index - i >= 0
                && pixels.get(index - i).getColor() == color; i++) {
            pixels.get(index - i).setHighlited(true);
        }
    }

    /**
     * Highlights all succeeding pixels with the same color like the given
     * pixel.
     *
     * @param index the index of the origin pixel
     * @param color the color of the origin pixel
     */
    private void highlightSuccPixels(final int index, final Color color) {
        for (int i = 1; index + i < pixels.size()
                && pixels.get(index + i).getColor() == color; i++) {
            pixels.get(index + i).setHighlited(true);
        }
    }

    /**
     * Highlights the pixels with the given variable part index by drawing a new
     * border color.
     *
     * @param index the index of the variable part
     */
    public void highlightPixels(final int index) {
        unhighlightPixels();
        int varPart = -1;
        for (int i = 0; i < pixels.size(); i++) {
            if (pixels.get(i).getPart().getType() == Type.VAR) {
                // If the type is not VAR the varPart variable is increased
                if (i == 0 || pixels.get(i - 1).getPart().getType() != Type.VAR) {
                    varPart++;
                }
                // If the varPart equals the given index the pixel index is
                // found
                if (varPart == index) {
                    pixels.get(i).setHighlited(true);
                    // Highlights all succeeding pixels with the same color as
                    // they
                    // belong to the same protocol part
                    highlightSuccPixels(i, pixels.get(i).getColor());
                    redraw();
                    break;
                }
            }
        }
    }

    /**
     * Un-highlights all pixels.
     */
    public void unhighlightPixels() {
        for (int i = 0; i < pixels.size(); i++) {
            pixels.get(i).setHighlited(false);
        }
        redraw();
    }

    /**
     * Gets the text of all parts/pixels concatenated as one string.
     *
     * @return the whole text
     */
    public String getText() {
        final StringBuffer buffer = new StringBuffer();
        Color lastColor = null; // NOPMD
        for (Pixel pixel : pixels) {
            if (lastColor == null || lastColor != pixel.getColor()) {
                buffer.append(pixel.getContent());
                lastColor = pixel.getColor();
            }
        }
        return buffer.toString();
    }

    /**
     * Gets the range of the text for a given position of a whole pixel block.
     *
     * @param x the x position
     * @param y the y position
     * @return the start position and the length of the pixel block as an array
     */
    public int[] getRangeForPos(final int x, final int y) { // NOPMD
        // Initialize the range
        int[] range = {0, 0};
        // Determines the position in the matrix and the corresponding index
        final int column = x / REC_SIZE;
        final int row = y / REC_SIZE;
        int index = numOfCols * row + column;
        // If the index is a valid one, proceed
        if (index < pixels.size()) {
            // Find the first position of preceding pixels with the same color
            while (index - 1 >= 0
                    && pixels.get(index - 1).getColor() == pixels.get(index)
                    .getColor()) {
                index--;
            }
            range[0] = index;
            // Determine the length of the pixels with the same color
            while (index + 1 < pixels.size()
                    && pixels.get(index + 1).getColor() == pixels.get(index)
                    .getColor()) {
                index++;
            }
            range[1] = index + 1 - range[0];
        }
        return range;
    }

    /**
     * Gets the color of the underlying pixel for a given position.
     *
     * @param x the x position
     * @param y the y position
     * @return the color of the underlying pixel
     */
    public Color getColorForPos(final int x, final int y) { // NOPMD
        // Initialize the color
        Color color = null;// NOPMD
        // Determines the position in the matrix and the corresponding index
        final int column = x / REC_SIZE;
        final int row = y / REC_SIZE;
        final int index = numOfCols * row + column;
        // If the index is a valid one, proceed
        if (index < pixels.size()) {
            // Get the color of the pixel
            color = pixels.get(index).getColor();
        }
        return color;
    }
}
