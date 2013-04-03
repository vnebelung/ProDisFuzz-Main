/**
 * This file is part of the ProDisFuzz program.
 * (c) by Volker Nebelung, 2012
 */
package model.runnable.component;

import model.FuzzedMessage;
import model.ProtocolPart;
import model.RunnableThread.RunnableState;
import model.runnable.AbstractR;

import java.util.Iterator;
import java.util.List;

/**
 * The Class FuzzingSepFinMessageC implements the functionality to generate a
 * fuzzed message with separate values for every variable protocol part.
 *
 * @author Volker Nebelung
 * @version 1.0
 */
public class FuzzingSepFinMessageC extends AbstractC {

    /**
     * The protocol parts.
     */
    private final List<ProtocolPart> parts;

    /**
     * Instantiates a new fuzzing separate finite component.
     *
     * @param runnable the parent runnable
     * @param parts    the protocol parts
     */
    public FuzzingSepFinMessageC(final AbstractR runnable,
                                 final List<ProtocolPart> parts) {
        super(runnable);
        this.parts = parts;
    }

    /**
     * Creates the fuzzed message with individual variable parts.
     *
     * @param index the index of the initial protocol part
     * @param line  the current line
     * @return fuzzedMessage the fuzzed message
     */
    public FuzzedMessage create(final int index, final String line,
                                final List<ProtocolPart> varParts) {
        final FuzzedMessage fuzzedMessage = new FuzzedMessage();
        Iterator<List<Byte>> iterator;
        runnable.setStateMessage("i:Preparing fuzzed message.",
                RunnableState.RUNNING);
        // For every protocol part other than the current read a random line of
        // its library file
        for (ProtocolPart part : parts) {
            switch (part.getType()) {
                case FIXED:
                    iterator = part.getContent().iterator();
                    fuzzedMessage.addBytes(iterator.next());
                    break;
                case VAR:
                    if (part.equals(varParts.get(index))) {
                        fuzzedMessage.addBytes(line.getBytes());
                    } else {
                        fuzzedMessage.addBytes(part.getRandomLibraryLine()
                                .getBytes());
                    }
                    break;
                default:
                    break;
            }
        }
        return fuzzedMessage;
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