/**
 * This file is part of the ProDisFuzz program.
 * (c) by Volker Nebelung, 2012
 */
package view;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyleRange;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * The Class MessageLog is a StyledText widget which displays a log file in
 * different colors.
 *
 * @author Volker Nebelung
 * @version 1.0
 */
public class MessageLog extends StyledText { // NOPMD

    /**
     * The color for error messages.
     */
    public final static Color COLOR_ERROR = new Color(Display.getDefault(),
            192, 0, 0);

    /**
     * The color for success messages.
     */
    public final static Color COLOR_SUCCESS = new Color(Display.getDefault(),
            0, 192, 0);

    /**
     * The color for warning messages.
     */
    public final static Color COLOR_WARNING = new Color(Display.getDefault(),
            192, 192, 0);

    /**
     * The color for information messages.
     */
    public final static Color COLOR_INFORMATION = new Color(
            Display.getDefault(), 0, 0, 0);

    /**
     * Instantiates a new log.
     *
     * @param parent the parent widget
     * @param style  the style
     */
    public MessageLog(final Composite parent, final int style) {
        super(parent, style);

        final Font font = new Font(getDisplay(), "Courier New", 12, SWT.NORMAL);
        setFont(font);
        setEditable(false);
    }

    /**
     * Appends a message at the end of the log. A Message can be one of the
     * following types: error, success, warning, information. The type of the
     * message is determined by its first two chars: - error: "e:" - success:
     * "s:" - warning: "w:" - information: "i:"
     *
     * @param message the message that shall be appended
     */
    public void addText(String message) { // NOPMD
        if (message == null) {
            setText("");
        } else {
            if (!message.substring(0, 2).equals("e:") // NOPMD
                    && !message.substring(0, 2).equals("s:")
                    && !message.substring(0, 2).equals("i:")
                    && !message.substring(0, 2).equals("w:")) {
                message = "e:INVALID LOG FORMAT!";
            }
            // Delete the first line if there are more than 250 lines
            if (getLineCount() > 249) {
                replaceTextRange(0, getLine(0).length() + 2, "");
            }
            // Determine the color depending on the first 2 chars
            if (!getText().endsWith(message.substring(2))) {
                Color color = COLOR_INFORMATION;
                // The log is the message without the first 2 chars, e.g. s: or
                // i:
                String log = "";
                switch (message.substring(0, 2)) {
                    case "e:":
                        color = COLOR_ERROR;
                        log = "ERROR: " + message.substring(2);
                        break;
                    case "s:":
                        color = COLOR_SUCCESS;
                        log = " " + message.substring(2);
                        break;
                    case "w:":
                        color = COLOR_WARNING;
                        log = message.substring(2);
                        break;
                    case "i:":
                        log = message.substring(2);
                        color = COLOR_INFORMATION;
                        break;
                    default:
                        break;
                }
                // If the message is not a success message following a message
                // which end on "..." a line break is inserted
                if ((!getText().endsWith("...") || !message.startsWith("s:"))
                        && !getText().isEmpty()) {
                    append("\n");
                }
                // If the message is written to a new line a time is added
                if (getText().endsWith("\n") || getText().isEmpty()) {
                    final DateFormat dateFormat = new SimpleDateFormat(
                            "HH:mm:ss", Locale.getDefault());
                    append(dateFormat.format(new Date()) + ":  ");
                }
                // Now the log is appended
                append(log);
                // Color the log chars
                final StyleRange styleRange = new StyleRange(getText().length()
                        - log.length(), log.length(), color, null);
                setStyleRange(styleRange);
                // Set the index to the last line for automatical scrolling
                setTopIndex(getLineCount() - 1);
            }
        }
    }
}
