/**
 * This file is part of the ProDisFuzz program.
 * (c) by Volker Nebelung, 2012
 */
package model;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * The Class ProtocolFile implements all information about a collected protocol
 * file.
 *
 * @author Volker Nebelung
 * @version 1.0
 */
public class ProtocolFile implements Comparable<ProtocolFile> {

    /**
     * The file.
     */
    private final Path filePath;

    /**
     * The checked status.
     */
    private boolean checked;

    /**
     * The learned status.
     */
    private boolean learned;

    /**
     * The MD5 hash of the file.
     */
    private final String md5;

    /**
     * Instantiates a new protocol file.
     *
     * @param filePath the file path
     */
    public ProtocolFile(final Path filePath) {
        this.filePath = filePath;
        checked = true;
        learned = false;
        md5 = generateHash("MD5");
    }

    /**
     * Generates a Hash for the file depending on the given message digest
     * algorithm.
     *
     * @param messageDigestAlg the message digest algorithm
     * @return the hash for the file
     */
    private String generateHash(final String messageDigestAlg) {
        final StringBuffer hash = new StringBuffer();
        MessageDigest messageDigest;
        try {
            switch (messageDigestAlg) {
                case "SHA1":
                    messageDigest = MessageDigest.getInstance("SHA-1");
                    break;
                case "SHA-256":
                    messageDigest = MessageDigest.getInstance("SHA-256");
                    break;
                default:
                    messageDigest = MessageDigest.getInstance("MD5");
                    break;
            }
            // Compute the hash
            messageDigest.reset();
            byte[] bytes;
            try {
                bytes = Files.readAllBytes(filePath);

            } catch (IOException e) {
                bytes = new byte[0];
            }
            messageDigest.update(bytes, 0, bytes.length);
            final byte[] digest = messageDigest.digest();

            String hex;
            for (int i = 0; i < digest.length; i++) {
                hex = Integer.toHexString(0xff & digest[i]);
                if (hex.length() == 1) {
                    hash.append('0');
                }
                hash.append(hex);
            }
        } catch (NoSuchAlgorithmException e) {
            hash.delete(0, hash.length());
            hash.append(e.getMessage());
        }
        return hash.toString();
    }

    /**
     * Gets the file name.
     *
     * @return the file name
     */
    public String getName() {
        return filePath.getFileName().toString();
    }

    /**
     * Gets the MD5 hash for the file.
     *
     * @return the hash
     */
    public String getMD5() {
        return md5;
    }

    /**
     * Checks if the file is checked.
     *
     * @return true, if it is checked
     */
    public boolean isChecked() {
        return checked;
    }

    /**
     * Sets the checked status.
     *
     * @param state the new checked status
     */
    public void setChecked(final boolean state) {
        checked = state;
    }

    /**
     * Gets the file size (in bytes) or. Can be 0 if the file can not be read.
     *
     * @return the file size
     */
    public long getSize() {
        long size;
        try {
            size = Files.size(filePath);
        } catch (IOException e) {
            size = 0;
        }
        return size;
    }

    /**
     * Gets the learned status.
     *
     * @return the learned status
     */
    public boolean isLearned() {
        return learned;
    }

    /**
     * Gets the time (in millis) the files was last modified. Can be 0 if the
     * file can not be read.
     *
     * @return the time
     */
    public long getLastModified() {
        long time;
        try {
            time = Files.getLastModifiedTime(filePath).toMillis();
        } catch (IOException e) {
            time = 0;
        }
        return time;
    }

    /**
     * Sets the learned status.
     *
     * @param learned the learned status to set
     */
    public void setLearnStatus(final boolean learned) {
        this.learned = learned;
    }

    /**
     * Gets the file content (in bytes) or an empty array if the file can not be
     * read.
     *
     * @return the file content
     */
    public byte[] getContent() {
        byte[] content;
        try {
            content = Files.readAllBytes(filePath);
        } catch (IOException e) {
            content = new byte[0];
        }
        return content;
    }

    /*
     * (non-Javadoc)
     *
     * @see java.lang.Comparable#compareTo(java.lang.Object)
     */
    @Override
    public int compareTo(final ProtocolFile otherFile) {
        // Custom comparison by comparing the name of the particular files
        return getName().compareTo(otherFile.getName());
    }

}
