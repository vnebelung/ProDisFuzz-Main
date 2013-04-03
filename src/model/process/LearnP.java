/**
 * This file is part of the ProDisFuzz program.
 * (c) by Volker Nebelung, 2012
 */
package model.process;

import model.ProtocolFile;
import model.ProtocolPart;
import model.ProtocolPart.Type;
import model.runnable.LearnR;

import java.util.ArrayList;
import java.util.List;
import java.util.Observable;

/**
 * The Class LearnP encapsulates the process of learning a protocol by analyzing
 * the structure of each collected file. The structure is then saved for further
 * usage, e.g. the generation of an XML format.
 *
 * @author Volker Nebelung
 * @version 1.0
 */
public class LearnP extends AbstractThreadP {

    /**
     * The file list.
     */
    private List<ProtocolFile> files;

    /**
     * The protocol parts list.
     */
    private final List<ProtocolPart> parts;

    /**
     * The number of temporary sequences during the learning process.
     */
    private int numOfTmpSequences;

    /**
     * Instantiates a new learn process.
     */
    public LearnP() {
        super();
        numOfTmpSequences = -1;
        files = new ArrayList<ProtocolFile>();
        parts = new ArrayList<ProtocolPart>();
    }

    /*
     * (non-Javadoc)
     *
     * @see model.process.AbstractThreadProcess#reset()
     */
    @Override
    public void reset() {
        files.clear();
        parts.clear();
        numOfTmpSequences = -1;
        super.reset();
    }

    /**
     * Clears the file list by removing all files which are marked as not
     * checked by a previous process.
     *
     * @param files the files
     */
    public void cleanFileList(final List<ProtocolFile> files) {
        for (ProtocolFile file : files) {
            if (file.isChecked()) {
                this.files.add(file);
            }
        }
        spreadUpdate(false);
    }

    /**
     * Starts the thread with the collect runnable.
     */
    public void start() {
        super.start(new LearnR(files));
    }

    /**
     * Generates the protocol parts from a given sequence of bytes.
     *
     * @param sequence the byte sequence
     */
    private void generateProtocolParts(final List<Byte> sequence) {
        parts.clear();
        if (!sequence.isEmpty()) {
            ProtocolPart part;
            // Determine the type
            Type type = getType(sequence.get(0));
            List<Byte> content = new ArrayList<Byte>();
            for (Byte currentByte : sequence) {
                // If the type is equal to the preceding type this byte belongs
                // to
                // the same protocol part
                if (type != getType(currentByte)) {
                    // If the types do not match the preceding part is written
                    // into
                    // the protocol part list and a new content list is
                    // initialized
                    part = new ProtocolPart(type); // NOPMD
                    part.addContent(content);
                    parts.add(part);
                    content = new ArrayList<Byte>(); // NOPMD
                    type = getType(currentByte);
                }
                content.add(currentByte);
            }
            // At the end the not yet written part is added to the protocol part
            // list
            part = new ProtocolPart(type);
            part.addContent(content);
            parts.add(part);
        }
    }

    /**
     * Returns the type of a byte: null is considered as a variable part, a
     * value is considered as a fixed part.
     *
     * @param b a singe byte
     * @return the type variable or unvariant
     */
    private Type getType(final Byte b) { // NOPMD
        // If the byte is null the type is VAR
        return (b == null ? Type.VAR : Type.FIXED);
    }

    /**
     * Gets the files.
     *
     * @return the file list
     */
    public List<ProtocolFile> getFiles() {
        return files;
    }

    /**
     * Gets the protocol parts.
     *
     * @return the protocol part list
     */
    public List<ProtocolPart> getParts() {
        return parts;
    }

    /**
     * Gets the number of temporary sequences currently in use by the learning process.
     *
     * @return the number of temporary sequences.
     */
    public int getNumOfTmpSequences() {
        return numOfTmpSequences;
    }

    /*
     * (non-Javadoc)
     *
     * @see java.util.Observer#update(java.util.Observable, java.lang.Object)
     */
    @Override
    public void update(final Observable observable, final Object arg) {
        final LearnR data = (LearnR) observable;
        files = data.getFiles();
        numOfTmpSequences = data.getNumOfTmpSequences();
        generateProtocolParts(data.getSequence());
        super.update(observable, arg);
    }

}