/**
 * This file is part of the ProDisFuzz program.
 * (c) by Volker Nebelung, 2012
 */
package model;

import java.util.ArrayList;
import java.util.List;

/**
 * The Class FuzzedMessage implements a modified message, both with random and
 * library values.
 *
 * @author Volker Nebelung
 * @version 1.0
 */
public class FuzzedMessage {

    /**
     * The message.
     */
    private final List<Byte> message;

    /**
     * Instantiates a new fuzzed message.
     */
    public FuzzedMessage() {
        message = new ArrayList<Byte>();
    }

    /**
     * Adds new bytes at the end of the existing message.
     *
     * @param bytes the bytes to append
     */
    public void addBytes(final byte[] bytes) {
        for (byte b : bytes) {
            message.add(b);
        }
    }

    /**
     * Adds new bytes at the end of the existing message.
     *
     * @param bytes the bytes to append
     */
    public void addBytes(final List<Byte> bytes) {
        message.addAll(bytes);
    }

    /**
     * Gets the complete message.
     *
     * @return the message as a byte array
     */
    public byte[] getBytes() {
        byte[] bytesArray = new byte[message.size()];
        for (int i = 0; i < message.size(); i++) {
            bytesArray[i] = message.get(i);
        }
        return bytesArray;
    }

    /**
     * Clears the message.
     */
    public void clear() {
        message.clear();
    }
}
