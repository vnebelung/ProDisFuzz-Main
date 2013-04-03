/**
 * This file is part of the ProDisFuzz program.
 * (c) by Volker Nebelung, 2012
 */
package model.runnable.component;

import model.RunnableThread.RunnableState;
import model.runnable.AbstractR;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * The Class ReportGenHeadingC implements the functionality to generate the
 * heading element.
 *
 * @author Volker Nebelung
 * @version 1.0
 */
public class ReportGenHeadingC extends AbstractC {

    /**
     * Instantiates a new report generation heading component.
     *
     * @param runnable the runnable
     */
    public ReportGenHeadingC(final AbstractR runnable) {
        super(runnable);
    }

    /**
     * Creates the headings.
     *
     * @param document the DOM document
     */
    public void create(final Document document) {
        runnable.setStateMessage("i:Creating heading element ...",
                RunnableState.RUNNING);
        final Element heading = document.createElement("h1");
        heading.appendChild(document.createTextNode("Pro"));
        final Element span = document.createElement("span");
        span.appendChild(document.createTextNode("Dis"));
        heading.appendChild(span);
        heading.appendChild(document.createTextNode("Fuzz Results"));
        document.getElementsByTagName("body").item(0).appendChild(heading);
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
