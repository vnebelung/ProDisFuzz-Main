/*
 * This file is part of ProDisFuzz, modified on 28.08.16 20:29.
 * Copyright (c) 2013-2016 Volker Nebelung <vnebelung@prodisfuzz.net>
 * This work is free. You can redistribute it and/or modify it under the
 * terms of the Do What The Fuck You Want To Public License, Version 2,
 * as published by Sam Hocevar. See the COPYING file for more details.
 */

package support;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

@SuppressWarnings("HardCodedStringLiteral")
public class SimulatedServer extends Thread {

    private Mode mode;
    private int port;
    private int sleep;

    public SimulatedServer(Mode mode, int sleep) {
        super();
        this.sleep = sleep;
        this.mode = mode;
    }

    public SimulatedServer(Mode mode) {
        this(mode, 0);
    }

    @SuppressWarnings("RefusedBequest")
    @Override
    public void run() {
        try (ServerSocket serverSocket = new ServerSocket(0)) {
            port = serverSocket.getLocalPort();
            while (true) {
                //noinspection SocketOpenedButNotSafelyClosed
                Socket client = serverSocket.accept();
                switch (mode) {
                    case STABLE:
                        new Thread(new StableHandler(client)).start();
                        break;
                    case UNSTABLE:
                        new Thread(new UnstableHandler(client)).start();
                        break;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public int getPort() throws InterruptedException {
        //noinspection WhileLoopSpinsOnField
        while (port == 0) {
            //noinspection BusyWait
            Thread.sleep(50);
        }
        return port;
    }

    public enum Mode {STABLE, UNSTABLE}

    @SuppressWarnings("NonStaticInnerClassInSecureContext")
    private class StableHandler implements Runnable {
        private Socket socket;

        public StableHandler(Socket socket) {
            this.socket = socket;
        }

        @Override
        public void run() {
            try (OutputStream out = socket.getOutputStream()) {
                Thread.sleep(sleep);
                out.write("response".getBytes(StandardCharsets.UTF_8));
                out.flush();
            } catch (InterruptedException | IOException e) {
                e.printStackTrace();
            } finally {
                try {
                    socket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    @SuppressWarnings("NonStaticInnerClassInSecureContext")
    private class UnstableHandler implements Runnable {
        private Socket socket;

        public UnstableHandler(Socket socket) {
            this.socket = socket;
        }

        @Override
        public void run() {
            try (InputStream in = socket.getInputStream(); OutputStream out = socket.getOutputStream()) {
                Thread.sleep(sleep);
                byte[] incoming = new byte[1];
                //noinspection ResultOfMethodCallIgnored
                in.read(incoming);
                //noinspection NumericCastThatLosesPrecision
                if (incoming[0] == (byte) 0xee) {
                    socket.close();
                    return;
                }
                out.write("response".getBytes(StandardCharsets.UTF_8));
                out.flush();
            } catch (InterruptedException | IOException e) {
                e.printStackTrace();
            } finally {
                try {
                    socket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
