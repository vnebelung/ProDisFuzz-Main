/**
 * This file is part of the ProDisFuzz program.
 * (c) by Volker Nebelung, 2012
 */
package model.runnable.component;

import model.ProtocolPart;
import model.RunnableThread.RunnableState;
import model.runnable.AbstractR;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import java.util.List;

/**
 * The Class ReportGenProtocolC implements the functionality to generate the
 * protocol structure elements.
 *
 * @author Volker Nebelung
 * @version 1.0
 */
public class ReportGenProtocolC extends AbstractC { // NOPMD

    /**
     * The protocol parts.
     */
    private final List<ProtocolPart> parts;

    /**
     * Instantiates a new report generation protocol component.
     *
     * @param runnable the runnable
     * @param parts    the protocol parts
     */
    public ReportGenProtocolC(final AbstractR runnable,
                              final List<ProtocolPart> parts) {
        super(runnable);
        this.parts = parts;
    }

    /**
     * Creates the protocol structure table.
     *
     * @param document the DOM document
     */
    public void create(final Document document) { // NOPMD
        runnable.setStateMessage("i:Creating protocol structure table ...",
                RunnableState.RUNNING);
        final Element heading = document.createElement("h2");
        heading.appendChild(document.createTextNode("Protocol Structure"));
        document.getElementsByTagName("body").item(0).appendChild(heading);
        final Element table = document.createElement("table");
        final Element[] trs = new Element[parts.size() + 1];
        // Create all tr elements
        for (int i = 0; i < trs.length; i++) {
            trs[i] = document.createElement("tr");
        }
        final Element[] ths = new Element[4];
        // Create all th elements
        for (int i = 0; i < ths.length; i++) {
            ths[i] = document.createElement("th");
        }
        ths[0].appendChild(document.createTextNode("#"));
        ths[1].appendChild(document.createTextNode("Type"));
        ths[2].appendChild(document.createTextNode("Minimum Length"));
        ths[3].appendChild(document.createTextNode("Maximum Length"));
        // Append the th elements to the first tr element
        for (int i = 0; i < ths.length; i++) {
            trs[0].appendChild(ths[i]);
        }
        final Element[][] tds = new Element[parts.size()][ths.length];
        // Create all td elements
        for (int i = 0; i < tds.length; i++) {
            for (int j = 0; j < tds[i].length; j++) {
                tds[i][j] = document.createElement("td");
            }
        }
        // For each part fill the td elements with values
        for (int i = 0; i < parts.size(); i++) {
            tds[i][0].appendChild(document.createTextNode(String.valueOf(i + 1)));
            tds[i][1].appendChild(document.createTextNode(parts.get(i)
                    .getType().toString()));
            tds[i][2].appendChild(document.createTextNode(String.valueOf(parts
                    .get(i).getMinLength())));
            tds[i][3].appendChild(document.createTextNode(String.valueOf(parts
                    .get(i).getMaxLength())));
        }
        // Append all td elements to the particular tr element
        for (int i = 0; i < tds.length; i++) {
            for (int j = 0; j < tds[i].length; j++) {
                trs[i + 1].appendChild(tds[i][j]);
            }
        }
        // Append all tr elements to the table element
        for (int i = 0; i < trs.length; i++) {
            table.appendChild(trs[i]);
        }
        // Append the table element to the body element
        document.getElementsByTagName("body").item(0).appendChild(table);
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
