/*
 * This file is part of ProDisFuzz, modified on 01.10.13 23:27.
 * Copyright (c) 2013 Volker Nebelung <vnebelung@prodisfuzz.net>
 * This work is free. You can redistribute it and/or modify it under the
 * terms of the Do What The Fuck You Want To Public License, Version 2,
 * as published by Sam Hocevar. See the COPYING file for more details.
 */

package model;

import model.logger.Logger;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class ProtocolFile implements Comparable<ProtocolFile> {

    private final Path filePath;
    private String sha256;

    /**
     * Instantiates a new protocol file.
     *
     * @param filePath the file path
     */
    public ProtocolFile(final Path filePath) {
        this.filePath = filePath;
        sha256 = "";
        try {
            sha256 = generateHash(MessageDigest.getInstance("SHA-256"));
        } catch (NoSuchAlgorithmException e) {
            Logger.getInstance().error(e);
        }
    }

    /**
     * Generates a Hash for the file depending on the given message digest
     * algorithm.
     *
     * @param algorithm the message digest algorithm
     * @return the hash for the file
     */
    private String generateHash(final MessageDigest algorithm) {
        final StringBuilder hash = new StringBuilder();
        algorithm.reset();
        byte[] bytes;
        try {
            bytes = Files.readAllBytes(filePath);
            algorithm.update(bytes, 0, bytes.length);
            final byte[] digest = algorithm.digest();
            String hex;
            for (final byte aDigest : digest) {
                hex = Integer.toHexString(0xff & aDigest);
                if (hex.length() == 1) {
                    hash.append('0');
                }
                hash.append(hex);
            }
        } catch (IOException e) {
            hash.delete(0, hash.length());
            hash.append("File could not be read");
            Logger.getInstance().error(e);
        } catch (OutOfMemoryError e) {
            hash.delete(0, hash.length());
            hash.append("File too large");
            Logger.getInstance().warning("File '" + getName() + "' is too large for checksum calculating");
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
    public String getSHA256() {
        return sha256;
    }

    /**
     * Gets the size of the file in the current file system.
     *
     * @return the file size or 0 if the file can not be read.
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
     * Gets the file content (in bytes) or an empty array if the file can not be read or is too large.
     *
     * @return the file content
     */
    public byte[] getContent() {
        byte[] content;
        try {
            content = Files.readAllBytes(filePath);
        } catch (IOException e) {
            Logger.getInstance().error("File '" + filePath.getFileName() + "' can not be read");
            content = new byte[0];
        } catch (OutOfMemoryError e) {
            Logger.getInstance().error("File '" + filePath.getFileName() + "' is too large to be read");
            content = new byte[0];
        }
        return content;
    }

    @Override
    public int compareTo(final ProtocolFile otherFile) {
        // Custom comparison by comparing the name of the particular files
        return getName().compareTo(otherFile.getName());
    }

}
