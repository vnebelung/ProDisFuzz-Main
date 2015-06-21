/*
 * This file is part of ProDisFuzz, modified on 13.03.14 20:16.
 * Copyright (c) 2013-2014 Volker Nebelung <vnebelung@prodisfuzz.net>
 * This work is free. You can redistribute it and/or modify it under the
 * terms of the Do What The Fuck You Want To Public License, Version 2,
 * as published by Sam Hocevar. See the COPYING file for more details.
 */

package model.process.fuzzing;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.util.Arrays;
import java.util.concurrent.Callable;

public class FuzzingSendCallable implements Callable<Boolean> {

    private final byte[] message;
    private final InetSocketAddress target;
    private final int timeout;
    private byte[] lastResponse;

    /**
     * Instantiates a new callable that is responsible for sending a message to the fuzzing target.
     *
     * @param bytes       the message to send to the target
     * @param target  the fuzzing target
     * @param timeout the timeout to wait before assuming a crash on target side
     */
    public FuzzingSendCallable(byte[] bytes, InetSocketAddress target, int timeout) {
        super();
        message = bytes.clone();
        this.target = target;
        this.timeout = timeout;
        //noinspection ZeroLengthArrayAllocation
        lastResponse = new byte[0];
    }

    @SuppressWarnings("OverlyBroadThrowsClause")
    @Override
    public Boolean call() throws IOException {
        try (Socket socket = new Socket()) {
            // Connect to target
            socket.setSoTimeout(timeout);
            socket.connect(target, timeout);
            //noinspection NestedTryStatement
            try (DataOutputStream out = new DataOutputStream(socket.getOutputStream()); DataInputStream in = new
                    DataInputStream(socket.getInputStream())) {
                // Clear the input stream
                //noinspection NestedTryStatement
                try {
                    //noinspection InfiniteLoopStatement
                    while (true) {
                        in.readByte();
                    }
                } catch (SocketTimeoutException ignored) {
                }
                // Send fuzzed message
                //noinspection NestedTryStatement
                try {
                    out.write(message);
                } catch (SocketException ignored) {
                    return false;
                }
                out.flush();
                // If target is responding, great
                //noinspection NestedTryStatement
                try {
                    byte[] buffer = new byte[1024];
                    int countRead;
                    while ((countRead = in.read(buffer, 0, buffer.length)) > -1) {
                        // Temporary buffer size = bytes already read + bytes last read
                        byte[] tmpBuffer = new byte[lastResponse.length + countRead];
                        System.arraycopy(lastResponse, 0, tmpBuffer, 0, lastResponse.length);
                        System.arraycopy(buffer, 0, tmpBuffer, lastResponse.length, countRead);
                        lastResponse = tmpBuffer;
                    }
                } catch (SocketTimeoutException ignored) {
                }
                return lastResponse.length != 0;
            }
        } catch (SocketTimeoutException ignored) {
            return false;
        }
    }

    /**
     * Returns the last response from the target. If the target crashed the response from the last valid request is
     * returned.
     *
     * @return the last response
     */
    public byte[] getLastResponse() {
        return Arrays.copyOf(lastResponse, lastResponse.length);
    }

}
