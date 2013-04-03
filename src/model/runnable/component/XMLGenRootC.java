/**
 * This file is part of the ProDisFuzz program.
 * (c) by Volker Nebelung, 2012
 */
package model.runnable.component;

import model.RunnableThread.RunnableState;
import model.runnable.AbstractR;
import model.runnable.XMLGenR;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * The Class XMLGenRootC implements the functionality to create the root
 * element.
 *
 * @author Volker Nebelung
 * @version 1.0
 */
public class XMLGenRootC extends AbstractC {

    /**
     * Instantiates a new XML generation root component.
     *
     * @param runnable the runnable
     */
    public XMLGenRootC(final AbstractR runnable) {
        super(runnable);
    }

    /**
     * Creates a root element.
     *
     * @param document the DOM document
     */
    public void create(final Document document) {
        runnable.setStateMessage("i:Creating root element ...",
                RunnableState.RUNNING);
        // Create the root element
        final Element root = document.createElement(XMLGenR.XML_ROOT);
        final SimpleDateFormat dateFormat = new SimpleDateFormat(
                "yyyy-MM-dd'T'HH:mm:ssZ", Locale.getDefault());
        String date = dateFormat.format(new Date());
        // The time zone is separated with a colon (standardized)
        date = date.substring(0, date.length() - 2) + ":"
                + date.substring(date.length() - 2);
        root.setAttribute("datetime", date);
        // Append the root element to the document
        document.appendChild(root);
        runnable.increaseProgress("s:done.", RunnableState.RUNNING);
    }

    /*
     * (non-Javadoc)
     *
     * @see model.runnable.component.AbstractComponent#getTotalProgress()
     */
    @Override
    public int getTotalProgress() {
        return 1;
    }

}
