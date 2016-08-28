/*
 * This file is part of ProDisFuzz, modified on 28.08.16 20:29.
 * Copyright (c) 2013-2016 Volker Nebelung <vnebelung@prodisfuzz.net>
 * This work is free. You can redistribute it and/or modify it under the
 * terms of the Do What The Fuck You Want To Public License, Version 2,
 * as published by Sam Hocevar. See the COPYING file for more details.
 */

package model.process.fuzzing;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.Arrays;
import java.util.concurrent.Callable;

/**
 * This class is the fuzzing send callable, responsible for sending a message to the fuzzing target.
 */
class FuzzedMessageSender implements Callable<Boolean> {

    private final byte[] message;
    private final InetSocketAddress target;
    private final int timeout;
    private byte[] lastResponse;

    /**
     * Constructs a fuzzed message sender.
     *
     * @param bytes   the message to send to the target
     * @param target  the fuzzing target
     * @param timeout the timeout to wait before assuming a crash on target side
     */
    public FuzzedMessageSender(byte[] bytes, InetSocketAddress target, int timeout) {
        super();
        message = bytes.clone();
        this.target = target;
        this.timeout = timeout;
        //noinspection ZeroLengthArrayAllocation
        lastResponse = new byte[0];
    }

    @SuppressWarnings("ElementOnlyUsedFromTestCode")
    @Override
    public Boolean call() {
        //noinspection OverlyBroadCatchBlock
        try (Socket socket = new Socket()) {
            // Connect to target
            socket.setSoTimeout(timeout);
            socket.connect(target, timeout);
            //noinspection NestedTryStatement
            try (OutputStream out = socket.getOutputStream(); InputStream in = socket.getInputStream()) {
                // Send fuzzed message
                out.write(message);
                out.flush();
                // If target is responding, great
                byte[] buffer = new byte[1024];
                int countRead;
                while ((countRead = in.read(buffer, 0, buffer.length)) > -1) {
                    // Temporary buffer size = bytes already read + bytes last read
                    byte[] tmpBuffer = new byte[lastResponse.length + countRead];
                    System.arraycopy(lastResponse, 0, tmpBuffer, 0, lastResponse.length);
                    System.arraycopy(buffer, 0, tmpBuffer, lastResponse.length, countRead);
                    lastResponse = tmpBuffer;
                }
                return lastResponse.length != 0;
            }
        } catch (IOException ignored) {
            return false;
        }
    }

    /**
     * Returns the last response from the target. If the target crashed the response from the last valid request is
     * returned.
     *
     * @return the last response or en empty array if there is no last response
     */
    public byte[] getLastResponse() {
        return Arrays.copyOf(lastResponse, lastResponse.length);
    }

}
