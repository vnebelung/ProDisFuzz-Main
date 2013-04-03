/**
 * This file is part of the ProDisFuzz program.
 * (c) by Volker Nebelung, 2012
 */
package model.runnable.component;

import model.ProtocolPart;
import model.RunnableThread.RunnableState;
import model.runnable.AbstractR;
import model.runnable.XMLGenR;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import java.util.List;

/**
 * The Class XMLGenPartC implements the functionality to create all part
 * elements.
 *
 * @author Volker Nebelung
 * @version 1.0
 */
public class XMLGenPartC extends AbstractC {

    /**
     * The protocol parts.
     */
    private final List<ProtocolPart> parts;

    /**
     * Instantiates a new XML generation part component.
     *
     * @param runnable the runnable
     * @param parts    the protocol parts
     */
    public XMLGenPartC(final AbstractR runnable, final List<ProtocolPart> parts) {
        super(runnable);
        this.parts = parts;
    }

    /**
     * Creates all part elements
     *
     * @param document the DOM document
     */
    public void create(final Document document) {
        for (int i = 0; i < parts.size() && !runnable.isInterrupted(); i++) {
            runnable.setStateMessage("i:Creating part element #" + (i + 1) + " ...",
                    RunnableState.RUNNING);
            // Create the part element depending on the part's type
            Element part;
            switch (parts.get(i).getType()) {
                case VAR:
                    part = document.createElement(XMLGenR.XML_PART_VAR);
                    break;
                case FIXED:
                    part = document.createElement(XMLGenR.XML_PART_FIX);
                    break;
                default:
                    return;
            }
            part.setAttribute(XMLGenR.XML_MINLENGTH,
                    String.valueOf(parts.get(i).getMinLength()));
            part.setAttribute(XMLGenR.XML_MAXLENGTH,
                    String.valueOf(parts.get(i).getMaxLength()));
            // Append the part element to the parts element
            document.getElementsByTagName(XMLGenR.XML_PARTS).item(0)
                    .appendChild(part);
            runnable.increaseProgress("s:done.", RunnableState.RUNNING);
            if (i < parts.size() - 1) {
                runnable.sleep(AbstractR.SLEEPING_TIME);
            }
        }
    }

    /*
     * (non-Javadoc)
     *
     * @see model.runnable.component.AbstractComponent#getTotalProgress()
     */
    @Override
    public int getTotalProgress() {
        return parts.size();
    }

}
