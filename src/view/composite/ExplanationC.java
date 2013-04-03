/**
 * This file is part of the ProDisFuzz program.
 * (c) by Volker Nebelung, 2012
 */
package view.composite;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import view.Pixel;

/**
 * The Class ExplanationCo implements the widgets responsible for displaying
 * information about the protocol canvas, like used colors etc.
 *
 * @author Volker Nebelung
 * @version 1.0
 */
public class ExplanationC extends Composite {

    public ExplanationC(final Composite parent, final int style) {
        super(parent, style);

        setLayout(new GridLayout(2, false));

        final GridData size = new GridData();
        // Set the size for the color blocks
        size.widthHint = 10;
        size.heightHint = 10;

        final Canvas expVarCanvas = new Canvas(this, SWT.NONE);
        expVarCanvas.setLayoutData(size);
        expVarCanvas.addPaintListener(new PaintListener() {
            @Override
            public void paintControl(final PaintEvent event) {
                drawExplanationImg(event, expVarCanvas.getBounds(),
                        Pixel.COLOR_VAR);
            }
        });

        final Label expVarLabel = new Label(this, SWT.NONE);
        expVarLabel.setText("Variable Parts");

        final Canvas expInvCanvas = new Canvas(this, SWT.NONE);
        expInvCanvas.setLayoutData(size);
        expInvCanvas.addPaintListener(new PaintListener() {
            @Override
            public void paintControl(final PaintEvent event) {
                drawExplanationImg(event, expInvCanvas.getBounds(),
                        Pixel.COLOR_INV);
            }
        });

        final Label expFixLabel = new Label(this, SWT.NONE);
        expFixLabel.setText("Fixed Parts");

        final Canvas expUndefCanvas = new Canvas(this, SWT.NONE);
        expUndefCanvas.setLayoutData(size);
        expUndefCanvas.addPaintListener(new PaintListener() {
            @Override
            public void paintControl(final PaintEvent event) {
                drawExplanationImg(event, expUndefCanvas.getBounds(),
                        Pixel.COLOR_UNDEF);
            }
        });

        final Label expUndefLabel = new Label(this, SWT.NONE);
        expUndefLabel.setText("Undefined Parts");

    }

    /**
     * Draws explanation image.
     *
     * @param event the paint event
     * @param rec   the bounds of the canvas
     * @param color the background color
     */
    private void drawExplanationImg(final PaintEvent event,
                                    final Rectangle rec, final Color color) {
        event.gc.setBackground(color);
        event.gc.fillRectangle(0, 0, rec.width - 1, rec.height - 1);
    }

}
