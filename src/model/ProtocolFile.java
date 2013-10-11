/*
 * This file is part of ProDisFuzz, modified on 11.10.13 22:40.
 * Copyright (c) 2013 Volker Nebelung <vnebelung@prodisfuzz.net>
 * This work is free. You can redistribute it and/or modify it under the
 * terms of the Do What The Fuck You Want To Public License, Version 2,
 * as published by Sam Hocevar. See the COPYING file for more details.
 */

package model;

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
     * @param p the file path
     */
    public ProtocolFile(final Path p) {
        filePath = p;
        sha256 = "";
        try {
            sha256 = generateHash(MessageDigest.getInstance("SHA-256"));
        } catch (NoSuchAlgorithmException e) {
            Model.INSTANCE.getLogger().error(e);
        }
    }

    /**
     * Generates a Hash for the file depending on the given message digest algorithm.
     *
     * @param algorithm the message digest algorithm
     * @return the file hash
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
            for (final byte each : digest) {
                hex = Integer.toHexString(0xff & each);
                if (hex.length() == 1) {
                    hash.append('0');
                }
                hash.append(hex);
            }
        } catch (IOException e) {
            hash.delete(0, hash.length());
            hash.append("File could not be read");
            Model.INSTANCE.getLogger().error(e);
        } catch (OutOfMemoryError e) {
            hash.delete(0, hash.length());
            hash.append("File too large");
            Model.INSTANCE.getLogger().warning("File '" + getName() + "' is too large for checksum calculating");
        }
        return hash.toString();
    }

    /**
     * Returns the system file name.
     *
     * @return the file name
     */
    public String getName() {
        return filePath.getFileName().toString();
    }

    /**
     * Returns the SHA-256 hash for the file.
     *
     * @return the hash
     */
    public String getSHA256() {
        return sha256;
    }

    /**
     * Returns the size of the file in the current file system.
     *
     * @return the file size or 0 if the file can not be read.
     */
    public long getSize() {
        try {
            return Files.size(filePath);
        } catch (IOException e) {
            Model.INSTANCE.getLogger().error(e);
            return 0;
        }
    }

    /**
     * Returns the time the file was last modified (in milliseconds).
     *
     * @return the modification time or 0 if the file can not be read
     */
    public long getLastModified() {
        try {
            return Files.getLastModifiedTime(filePath).toMillis();
        } catch (IOException e) {
            Model.INSTANCE.getLogger().error(e);
            return 0;
        }
    }

    /**
     * Returns the file content.
     *
     * @return the file content in bytes, can be empty if the file can not be read or is too large
     */
    public byte[] getContent() {
        byte[] content;
        try {
            content = Files.readAllBytes(filePath);
        } catch (IOException | OutOfMemoryError e) {
            Model.INSTANCE.getLogger().error(e);
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
