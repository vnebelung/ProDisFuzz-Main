/*
 * This file is part of ProDisFuzz, modified on 08.02.14 23:13.
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
import java.util.concurrent.Callable;

class FuzzingReconnectCallable implements Callable<Boolean> {

    private final InetSocketAddress target;
    private final int timeout;

    /**
     * Instantiates a new callable that is responsible for reconnect to a target in ace the connection was being lost.
     *
     * @param target  the fuzzing target
     * @param timeout the timeout to wait before retrying to connect
     */
    public FuzzingReconnectCallable(InetSocketAddress target, int timeout) {
        super();
        this.target = target;
        this.timeout = timeout;
    }

    @SuppressWarnings("ElementOnlyUsedFromTestCode")
    @Override
    public Boolean call() throws IOException {
        // Open connection
        try (Socket socket = new Socket()) {
            socket.setSoTimeout(timeout);
            socket.connect(target, timeout);
            //noinspection NestedTryStatement
            try (DataOutputStream out = new DataOutputStream(socket.getOutputStream());
                 DataInputStream in = new DataInputStream(socket.getInputStream())) {
                out.writeBoolean(false);
                in.readByte();
                return true;
            }
        } catch (SocketTimeoutException | SocketException ignored) {
            return false;
        }
    }
}
