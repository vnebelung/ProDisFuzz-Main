/**
 * This file is part of the ProDisFuzz program.
 * (c) by Volker Nebelung, 2012
 */
package model.runnable.component;

import model.ProtocolPart;
import model.ProtocolPart.Type;
import model.RunnableThread.RunnableState;
import model.runnable.AbstractR;
import model.runnable.XMLGenR;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import java.util.List;

/**
 * The Class XMLGenContentC implements the functionality to create all content
 * elements.
 *
 * @author Volker Nebelung
 * @version 1.0
 */
public class XMLGenContentC extends AbstractC {

    /**
     * The protocol parts.
     */
    private final List<ProtocolPart> parts;

    /**
     * Instantiates a new XML generation content component.
     *
     * @param runnable the runnable
     * @param parts    the protocol parts
     */
    public XMLGenContentC(final AbstractR runnable,
                          final List<ProtocolPart> parts) {
        super(runnable);
        this.parts = parts;
    }

    /**
     * Creates all content elements
     *
     * @param document the DOM document
     */
    public void create(final Document document) {
        int partFixIndex = 0;
        int partSemivarIndex = 0;
        Element content;
        Element byteElement;
        for (int i = 0; i < parts.size() && !runnable.isInterrupted(); i++) {
            if (parts.get(i).getType() == Type.FIXED) {
                for (List<Byte> bytesList : parts.get(i).getContent()) {
                    runnable.setStateMessage("i:Creating content element ...",
                            RunnableState.RUNNING);
                    // Create the content element
                    content = document.createElement("content");
                    // Append all bytes to the content element
                    for (Byte currentByte : bytesList) {
                        byteElement = document.createElement("byte");
                        byteElement.setTextContent(currentByte.toString());
                        // Append the byte element to the content element
                        content.appendChild(byteElement);
                    }
                    // Append the content element to the particular part
                    // element
                    if (parts.get(i).getType() == Type.FIXED) {
                        document.getElementsByTagName(XMLGenR.XML_PART_FIX)
                                .item(partFixIndex).appendChild(content);
                    } else {
                        document.getElementsByTagName(XMLGenR.XML_PART_SEMIVAR)
                                .item(partSemivarIndex).appendChild(content);
                    }
                    runnable.increaseProgress("s:done.", RunnableState.RUNNING);
                    runnable.sleep(AbstractR.SLEEPING_TIME);
                }
                if (parts.get(i).getType() == Type.FIXED) {
                    partFixIndex++;
                } else {
                    partSemivarIndex++;
                }
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
        int steps = 0;
        for (ProtocolPart part : parts) {
            if (part.getType() == Type.FIXED) {
                steps += part.getContent().size();
            }
        }
        return steps;
    }

}
