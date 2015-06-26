/*
 * This file is part of ProDisFuzz, modified on 6/26/15 9:26 PM.
 * Copyright (c) 2013-2015 Volker Nebelung <vnebelung@prodisfuzz.net>
 * This work is free. You can redistribute it and/or modify it under the
 * terms of the Do What The Fuck You Want To Public License, Version 2,
 * as published by Sam Hocevar. See the COPYING file for more details.
 */

package model.process.fuzzing;

import java.io.DataOutputStream;
import java.io.EOFException;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

@SuppressWarnings("HardCodedStringLiteral")
public class SimulatedServer extends Thread {

    private static void handleClient(Socket client) throws IOException {
        try (DataOutputStream out = new DataOutputStream(client.getOutputStream())) {
            out.write("response".getBytes(StandardCharsets.UTF_8));
            out.flush();
        }
    }

    @SuppressWarnings("RefusedBequest")
    @Override
    public void run() {
        try (ServerSocket serverSocket = new ServerSocket(10020); Socket client = serverSocket.accept()) {
            handleClient(client);
        } catch (EOFException ignored) {
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
