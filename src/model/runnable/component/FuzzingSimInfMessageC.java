/**
 * This file is part of the ProDisFuzz program.
 * (c) by Volker Nebelung, 2012
 */
package model.runnable.component;

import model.FuzzedMessage;
import model.ProtocolPart;
import model.RandomBytes;
import model.RunnableThread.RunnableState;
import model.runnable.AbstractR;

import java.util.Iterator;
import java.util.List;

/**
 * The Class FuzzingSimInfMessageC implements the functionality to generate a
 * fuzzed message with equal values for every variable protocol part.
 *
 * @author Volker Nebelung
 * @version 1.0
 */
public class FuzzingSimInfMessageC extends AbstractC {

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
    public FuzzingSimInfMessageC(final AbstractR runnable,
                                 final List<ProtocolPart> parts) {
        super(runnable);
        this.parts = parts;
    }

    /**
     * Creates the fuzzed message with equal variable parts.
     *
     * @return fuzzedMessage the fuzzed message
     */
    public FuzzedMessage create() {
        byte[] bytes;
        final FuzzedMessage fuzzedMessage = new FuzzedMessage();
        Iterator<List<Byte>> iterator;
        runnable.setStateMessage("i:Preparing fuzzing message.",
                RunnableState.RUNNING);
        // Generate the random bytes
        bytes = RandomBytes.getInstance().generateRandomBytes(parts);
        // Apply the bytes for each VAR part
        for (ProtocolPart part : parts) {
            switch (part.getType()) {
                case FIXED:
                    iterator = part.getContent().iterator();
                    fuzzedMessage.addBytes(iterator.next());
                    break;
                case VAR:
                    fuzzedMessage.addBytes(bytes);
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