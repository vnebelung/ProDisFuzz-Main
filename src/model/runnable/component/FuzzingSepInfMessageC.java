/**
 * This file is part of the ProDisFuzz program.
 * (c) by Volker Nebelung, 2012
 */
package model.runnable.component;

import model.FuzzedMessage;
import model.ProtocolPart;
import model.ProtocolPart.DataMode;
import model.RandomBytes;
import model.RunnableThread.RunnableState;
import model.runnable.AbstractR;

import java.util.Iterator;
import java.util.List;

/**
 * The Class FuzzingSepInfMessageC implements the functionality to generate a
 * fuzzed message with separate values for every variable protocol part.
 *
 * @author Volker Nebelung
 * @version 1.0
 */
public class FuzzingSepInfMessageC extends AbstractC {

    /**
     * The protocol parts.
     */
    private final List<ProtocolPart> parts;

    /**
     * Instantiates a new fuzzing separate infinite component.
     *
     * @param runnable the parent runnable
     * @param parts    the protocol parts
     */
    public FuzzingSepInfMessageC(final AbstractR runnable,
                                 final List<ProtocolPart> parts) {
        super(runnable);
        this.parts = parts;
    }

    /**
     * Creates the fuzzed message with individual variable parts.
     *
     * @return fuzzedMessage the fuzzed message
     */
    public FuzzedMessage create() {
        final FuzzedMessage fuzzedMessage = new FuzzedMessage();
        Iterator<List<Byte>> iterator;
        runnable.setStateMessage("i:Preparing fuzzed message.",
                RunnableState.RUNNING);
        // Generates the fuzzed string separate for every single protocol part
        for (ProtocolPart part : parts) {
            switch (part.getType()) {
                case FIXED:
                    iterator = part.getContent().iterator();
                    fuzzedMessage.addBytes(iterator.next());
                    break;
                case VAR:
                    if (part.getDataMode() == DataMode.LIBRARY) {
                        fuzzedMessage.addBytes(part.getRandomLibraryLine()
                                .getBytes());
                    } else if (part.getDataMode() == DataMode.RANDOM) {
                        fuzzedMessage.addBytes(RandomBytes.getInstance()
                                .generateRandomBytes(part.getMaxLength()));
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