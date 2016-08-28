/*
 * This file is part of ProDisFuzz, modified on 28.08.16 19:39.
 * Copyright (c) 2013-2016 Volker Nebelung <vnebelung@prodisfuzz.net>
 * This work is free. You can redistribute it and/or modify it under the
 * terms of the Do What The Fuck You Want To Public License, Version 2,
 * as published by Sam Hocevar. See the COPYING file for more details.
 */

package support;

import model.util.Constants;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.EOFException;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

@SuppressWarnings({"HardCodedStringLiteral", "OverlyComplexMethod"})
public class SimulatedMonitor extends Thread {

    private final Map<String, String> parameters;
    private int port;

    public SimulatedMonitor(int port) {
        super();
        this.port = port;
        parameters = new HashMap<>();
    }

    @SuppressWarnings("RefusedBequest")
    @Override
    public void run() {
        try (ServerSocket serverSocket = new ServerSocket(port)) {
            while (!isInterrupted()) {
                //noinspection NestedTryStatement
                try (Socket client = serverSocket.accept()) {
                    handleClient(client);
                } catch (EOFException ignored) {
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void handleClient(Socket client) throws IOException {
        try (DataInputStream in = new DataInputStream(client.getInputStream()); DataOutputStream out = new
                DataOutputStream(client.getOutputStream())) {
            while (!client.isClosed()) {
                StringBuilder stringBuilder = new StringBuilder(3);
                for (int i = 0; i < 3; i++) {
                    stringBuilder.append((char) in.readByte());
                }
                String header = stringBuilder.toString();
                in.readByte();
                stringBuilder = new StringBuilder();
                while (true) {
                    char c = (char) in.readByte();
                    if (c == ' ') {
                        break;
                    } else {
                        stringBuilder.append(c);
                    }
                }
                int length = Integer.parseInt(stringBuilder.toString());
                stringBuilder = new StringBuilder();
                for (int i = 0; i < length; i++) {
                    char c = (char) in.readByte();
                    stringBuilder.append(c);
                }
                String body = stringBuilder.toString();
                switch (header) {
                    case "AYT":
                        out.write(("ROK " + String.valueOf(Constants.RELEASE_NUMBER).length() + ' ' + Constants
                                .RELEASE_NUMBER).getBytes(StandardCharsets.UTF_8));
                        out.flush();
                        break;
                    case "GPM":
                        String[] param = body.split(",");
                        StringBuilder tmp = new StringBuilder();
                        for (String each : param) {
                            if (parameters.containsKey(each)) {
                                tmp.append(each).append('=').append(parameters.get(each)).append(',');
                            }
                        }
                        if (tmp.length() > 0) {
                            tmp.deleteCharAt(tmp.length() - 1);
                            out.write(("ROK " + tmp.length() + ' ' + tmp).getBytes(StandardCharsets.UTF_8));
                            out.flush();
                        } else {
                            out.write("ERR 3 n/a".getBytes(StandardCharsets.UTF_8));
                            out.flush();
                        }
                        break;
                    case "SPM":
                        boolean bad = false;
                        String[] pairs = body.split(",");
                        for (String each : pairs) {
                            String[] p = each.split("=");
                            if (p[0].isEmpty()) {
                                bad = true;
                            } else if (p.length == 1) {
                                parameters.remove(p[0]);
                            } else {
                                parameters.put(p[0], p[1]);
                            }
                        }
                        if (bad) {
                            out.write("ERR 21 Not testkey=testvalue".getBytes(StandardCharsets.UTF_8));
                            out.flush();
                        } else {
                            out.write("ROK 0 ".getBytes(StandardCharsets.UTF_8));
                            out.flush();
                        }
                        break;
                    case "CTD":
                        Instant now = Instant.now();
                        String newBody;
                        if (body.length() == 1) {
                            newBody = "crashed=no,time=" + now;
                            out.write(("ROK " + newBody.length() + ' ' + newBody).getBytes(StandardCharsets.UTF_8));
                            out.flush();
                        } else {
                            newBody = "crashed=yes,time=" + now + ",crashcause=test";
                            out.write(("ROK " + newBody.length() + ' ' + newBody).getBytes(StandardCharsets.UTF_8));
                            out.flush();
                        }
                        break;
                    default:
                }
            }
        }
    }
}
