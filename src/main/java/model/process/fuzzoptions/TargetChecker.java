/*
 * This file is part of ProDisFuzz, modified on 28.08.16 20:30.
 * Copyright (c) 2013-2016 Volker Nebelung <vnebelung@prodisfuzz.net>
 * This work is free. You can redistribute it and/or modify it under the
 * terms of the Do What The Fuck You Want To Public License, Version 2,
 * as published by Sam Hocevar. See the COPYING file for more details.
 */

package model.process.fuzzoptions;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.util.concurrent.Callable;

/**
 * This class is the target checker callable, responsible for verifying that a target with a given address and port
 * is reachable.
 */
class TargetChecker implements Callable<Boolean> {

    private String address;
    private int port;
    private int timeout;

    /**
     * Constructs the callable.
     *
     * @param address the target's address
     * @param port    the target's port
     * @param timeout the target's timeout in milliseconds
     */
    public TargetChecker(String address, int port, int timeout) {
        this.address = address;
        this.port = port;
        this.timeout = timeout;
    }

    @Override
    public Boolean call() {
        if (address.isEmpty()) {
            return false;
        }
        //noinspection OverlyBroadCatchBlock
        try (Socket socket = new Socket()) {
            SocketAddress target = new InetSocketAddress(address, port);
            // Establish a test connection without sending any data
            socket.setSoTimeout(timeout);
            socket.connect(target, timeout);
        } catch (IOException ignored) {
            return false;
        }
        return true;
    }
}
