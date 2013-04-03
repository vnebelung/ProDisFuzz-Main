/**
 * This file is part of the ProDisFuzz program.
 * (c) by Volker Nebelung, 2012
 */
package model.runnable.component;

import model.RunnableThread.RunnableState;
import model.runnable.AbstractR;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * The Class ReportGenHTMLC implements the functionality to generate the html
 * structure of the report.
 *
 * @author Volker Nebelung
 * @version 1.0
 */
public class ReportGenHTMLC extends AbstractC {

    /**
     * Instantiates a new report generation HTML component.
     *
     * @param runnable the runnable
     */
    public ReportGenHTMLC(final AbstractR runnable) {
        super(runnable);
    }

    /**
     * Creates the html element in the DOM
     *
     * @param document the DOM document
     */
    public void create(final Document document) {
        runnable.setStateMessage("i:Creating HTML structure ...",
                RunnableState.RUNNING);
        final Element html = document.createElement("html");
        html.setAttribute("xmlns", "http://www.w3.org/1999/xhtml");
        html.setAttribute("xml:lang", "en");
        document.appendChild(html);
        createHead(document);
        createCSS(document);
        createBody(document);
        runnable.increaseProgress("s:done.", RunnableState.RUNNING);
    }

    /**
     * Creates the head element in the DOM
     *
     * @param document the DOM document
     */
    private void createHead(final Document document) {
        final Element head = document.createElement("head");
        final Element title = document.createElement("title");
        title.appendChild(document.createTextNode("ProDisFuzz Results"));
        head.appendChild(title);
        // Create the meta element with the date of the generation
        final Element metaDate = document.createElement("meta");
        metaDate.setAttribute("name", "date");
        final SimpleDateFormat dateFormat = new SimpleDateFormat(
                "yyyy-MM-dd'T'HH:mm:ssZ", Locale.getDefault());
        String date = dateFormat.format(new Date());
        // The time zone has to be separated by a colon
        date = date.substring(0, date.length() - 2) + ":"
                + date.substring(date.length() - 2);
        metaDate.setAttribute("content", date);
        head.appendChild(metaDate);
        document.getElementsByTagName("html").item(0).appendChild(head);
    }

    /**
     * Creates the CSS elements.
     *
     * @param document the DOM document
     */
    public void createCSS(final Document document) {
        final Element style = document.createElement("style");
        style.setAttribute("type", "text/css");
        style.appendChild(document
                .createTextNode("body { font-family: sans-serif; background-color: #ffffff; color: #000000; }"));
        style.appendChild(document
                .createTextNode(" th, td { padding: 0.2em 1em; text-align: left; }"));
        style.appendChild(document
                .createTextNode(" h1 span { color: #4526ae; }"));
        style.appendChild(document
                .createTextNode(" h2, h3 { background-color: #4526ae; color: #ffffff; padding: 0.2em; }"));
        style.appendChild(document
                .createTextNode(" .crash { background-color: #ffc0c0; }"));
        style.appendChild(document
                .createTextNode(" .right { text-align: right; }"));
        document.getElementsByTagName("head").item(0).appendChild(style);
    }

    /**
     * Creates the body element.
     *
     * @param document the DOM document
     */
    public void createBody(final Document document) {
        final Element body = document.createElement("body");
        document.getElementsByTagName("html").item(0).appendChild(body);
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
