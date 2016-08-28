/*
 * This file is part of ProDisFuzz, modified on 28.08.16 19:39.
 * Copyright (c) 2013-2016 Volker Nebelung <vnebelung@prodisfuzz.net>
 * This work is free. You can redistribute it and/or modify it under the
 * terms of the Do What The Fuck You Want To Public License, Version 2,
 * as published by Sam Hocevar. See the COPYING file for more details.
 */

package model.protocol;

import model.Model;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * This class is a protocol file, responsible for handling file information about a single protocol file.
 */
public class ProtocolFile implements Comparable<ProtocolFile> {

    private final Path path;
    private String sha256;

    /**
     * Constructs a new protocol file.
     *
     * @param path the file path
     */
    public ProtocolFile(Path path) {
        this.path = path;
        try {
            //noinspection HardCodedStringLiteral
            sha256 = generateHash(MessageDigest.getInstance("SHA-256"));
        } catch (NoSuchAlgorithmException e) {
            sha256 = "";
            Model.INSTANCE.getLogger().error(e);
        }
    }

    /**
     * Generates a Hash for the file depending on the given message digest algorithm.
     *
     * @param algorithm the message digest algorithm
     * @return the file hash
     */
    private String generateHash(MessageDigest algorithm) {
        algorithm.reset();
        StringBuilder hash = new StringBuilder();
        try {
            byte[] bytes = Files.readAllBytes(path);
            algorithm.update(bytes, 0, bytes.length);
            byte[] digest = algorithm.digest();
            for (byte each : digest) {
                //noinspection HardCodedStringLiteral
                hash.append(String.format("%02x", each));
            }
        } catch (IOException e) {
            hash.delete(0, hash.length());
            hash.append("File could not be read");
            Model.INSTANCE.getLogger().error(e);
        } catch (OutOfMemoryError ignored) {
            hash.delete(0, hash.length());
            hash.append("File too large");
            Model.INSTANCE.getLogger().warning("File '" + getName() + "' is too large for checksum calculating");
        }
        return hash.toString();
    }

    /**
     * Returns the file name including its extension.
     *
     * @return the file name
     */
    public String getName() {
        return path.getFileName().toString();
    }

    /**
     * Returns the SHA-256 hash for the file.
     *
     * @return the hash
     */
    public String getSha256() {
        return sha256;
    }

    /**
     * Returns the size of the file in the current file system.
     *
     * @return the file size (in bytes) or 0 if the file can not be read.
     */
    public long getSize() {
        try {
            return Files.size(path);
        } catch (IOException e) {
            Model.INSTANCE.getLogger().error(e);
            return 0;
        }
    }

    /**
     * Returns the time the file was last modified (in milliseconds).
     *
     * @return the modification time, since the epoch (1970-01-01T00:00:00Z), or 0 if the file cannot be read
     */
    public long getLastModified() {
        try {
            return Files.getLastModifiedTime(path).toMillis();
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
            content = Files.readAllBytes(path);
        } catch (IOException | OutOfMemoryError e) {
            Model.INSTANCE.getLogger().error(e);
            //noinspection ZeroLengthArrayAllocation
            content = new byte[0];
        }
        return content;
    }

    @Override
    public int compareTo(ProtocolFile o) {
        // Custom comparison by comparing the name of the particular files
        return getName().compareTo(o.getName());
    }
}
