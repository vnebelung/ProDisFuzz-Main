/**
 * This file is part of the ProDisFuzz program.
 * (c) by Volker Nebelung, 2012
 */
package model;

/**
 * The Class ProtocolPartFile contains for a specific protocol part informations
 * about the position and the length of the part in all protocol files. It maps
 * the part of the learned sequence to the content of all files.
 *
 * @author Volker Nebelung
 * @version 1.0
 */
public class ProtocolPartFile {

    /**
     * The position of the part.
     */
    private final int pos;

    /**
     * The length of the part.
     */
    private final int length;

    /**
     * Instantiates a new part with position and length.
     *
     * @param pos    the position
     * @param length the length
     */
    public ProtocolPartFile(final int pos, final int length) {
        this.pos = pos;
        this.length = length;
    }

    /**
     * Gets the position of the part.
     *
     * @return the pos
     */
    public int getPos() {
        return pos;
    }

    /**
     * gets the length of the part.
     *
     * @return the length
     */
    public int getLength() {
        return length;
    }
}
