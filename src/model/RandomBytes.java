/**
 * This file is part of the ProDisFuzz program.
 * (c) by Volker Nebelung, 2012
 */
package model;

import java.security.SecureRandom;
import java.util.List;
import java.util.Random;

/**
 * The Class RandomBytes implements the functionality to generate a random
 * number of random bytes.
 *
 * @author Volker Nebelung
 * @version 1.0
 */
public final class RandomBytes {

    /**
     * Private class attribute. Only 1 instance
     */
    private static final RandomBytes INSTANCE = new RandomBytes();

    /**
     * Singelton Constructor.
     */
    private RandomBytes() {
    }

    /**
     * Gets the only instance of ImagerRepository.
     */
    public static RandomBytes getInstance() {
        return INSTANCE;
    }

    /**
     * Generates an amount of random bytes within a range from 0 to the given
     * maximum length x 10000.
     *
     * @return the random bytes as an array
     */
    public byte[] generateRandomBytes(final int maxlength) {
        // Generate random bytes according to the maximum length of the given
        // protocol part
        final Random random = new SecureRandom();
        final int fuzzDataLength = random.nextInt(maxlength * 10000 + 1);
        final byte[] bytes = new byte[fuzzDataLength];
        random.nextBytes(bytes);
        return bytes;
    }

    /**
     * Generates an amount of random bytes within a range from 0 to 10000 x
     * maximum length of all protocol parts.
     *
     * @param parts the protocol parts
     * @return the random bytes as an array
     */
    public byte[] generateRandomBytes(final List<ProtocolPart> parts) {
        int maxLength = 0;
        // Search for the maximum length of all protocol parts
        for (ProtocolPart part : parts) {
            maxLength = Math.max(maxLength, part.getMaxLength());
        }
        // Generate random bytes
        final Random random = new SecureRandom();
        final int fuzzDataLength = random.nextInt(maxLength * 10000 + 1);
        final byte[] bytes = new byte[fuzzDataLength];
        random.nextBytes(bytes);
        return bytes;
    }

}
