/*
 * This file is part of ProDisFuzz, modified on 28.08.16 20:30.
 * Copyright (c) 2013-2016 Volker Nebelung <vnebelung@prodisfuzz.net>
 * This work is free. You can redistribute it and/or modify it under the
 * terms of the Do What The Fuck You Want To Public License, Version 2,
 * as published by Sam Hocevar. See the COPYING file for more details.
 */

package model.process.monitor;

import model.Model;
import model.monitor.protocol.Protocol;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;

public class Monitor {

    private static final int TIMEOUT = 50;
    private Socket socket;
    private Protocol protocol;
    private InetSocketAddress inetSocketAddress;

    /**
     * Sets the monitor's address.
     *
     * @param hostname the hostname of the monitor's address
     * @param port     the port number of the monitor's address
     */
    public void setAddress(String hostname, int port) {
        if (hostname.isEmpty()) {
            inetSocketAddress = null;
            return;
        }
        try {
            inetSocketAddress = new InetSocketAddress(hostname, port);
        } catch (IllegalArgumentException ignored) {
            Model.INSTANCE.getLogger().error("Monitor address is invalid");
            inetSocketAddress = null;
        }
    }

    /**
     * Returns the monitor's address name.
     *
     * @return the address name or an empty string if the monitor's address is invalid
     */
    public String getAddressName() {
        return (inetSocketAddress == null) ? "" : inetSocketAddress.getHostString();
    }

    /**
     * Returns the monitor's address port
     *
     * @return the address port or -1 if the monitor's address is invalid
     */
    public int getAddressPort() {
        return (inetSocketAddress == null) ? -1 : inetSocketAddress.getPort();
    }

    /**
     * Tries to connect to the monitor component and checks whether the monitor is reachable under its current address.
     * To be accepted as reachable the monitor must respond successfully with a version number equal to the version
     * number of this release.
     *
     * @return true, if the connection to the monitor could be successfully established.
     */
    public boolean connect() {
        if (socket == null || socket.isClosed()) {
            socket = new Socket();
        }
        if (inetSocketAddress == null) {
            Model.INSTANCE.getLogger().error("Monitor address must not be empty");
            return false;
        }
        //noinspection OverlyBroadCatchBlock
        try {
            socket.setSoTimeout(TIMEOUT);
            socket.connect(inetSocketAddress, TIMEOUT);
            protocol = new Protocol(new DataInputStream(socket.getInputStream()),
                    new DataOutputStream(socket.getOutputStream()));
        } catch (IOException ignored) {
            disconnect();
            Model.INSTANCE.getLogger().error("Cannot connect to monitor '" + inetSocketAddress.getHostString() +
                    ':' + inetSocketAddress.getPort() + '\'');
            return false;
        }
        //        int monitorVersion;
        //        try {
        //           // TODO: monitorVersion = protocol.ayt();
        //        } catch (IllegalStateException e) {
        //            Model.INSTANCE.getLogger().error(e);
        //            return false;
        //        } catch (IOException ignored) {
        //            disconnect();
        //            Model.INSTANCE.getLogger().error("Monitor '" + inetSocketAddress.getHostString() + ':' +
        //                    inetSocketAddress.getPort() + "' is not responding");
        //            return false;
        //        }
        //        if (monitorVersion == -1) {
        //            disconnect();
        //            Model.INSTANCE.getLogger().error("Connection to monitor '" + inetSocketAddress.getHostString()
        // + ':' +
        //                    inetSocketAddress.getPort() + "' is lost");
        //            return false;
        //        }
        //        if (monitorVersion != Constants.RELEASE_NUMBER) {
        //            disconnect();
        //            Model.INSTANCE.getLogger().error("Version mismatch between monitor '" + inetSocketAddress
        // .getHostString()
        //                    + ':' + inetSocketAddress.getPort() + "' (release " + monitorVersion + ") and main instance (" +
        //                    Constants.RELEASE_NUMBER + ')');
        //            return false;
        //        }
        Model.INSTANCE.getLogger().fine("Monitor '" + inetSocketAddress.getHostString() + ':' + inetSocketAddress
                .getPort() + "' is reachable");
        return true;
    }

    /**
     * Sends the fuzzing parameters to the monitor component. If the monitor cannot set the parameters an error message
     * is shown.
     *
     * @param parameters the fuzzing parameters as key => value
     */
    public void setParameters(Map<String, String> parameters) {
        if (!isSocketOpen()) {
            return;
        }
        //        try {
        //    TODO        if (!protocol.spm(parameters)) {
        //                protocol.rst();
        //                Model.INSTANCE.getLogger().error("Malformed or wrong parameters");
        //                return;
        //            }
        //        } catch (IllegalArgumentException | IllegalStateException e) {
        //            Model.INSTANCE.getLogger().error(e);
        //            return;
        //        } catch (IOException ignored) {
        //            Model.INSTANCE.getLogger().error("Connection to monitor was lost");
        //            return;
        //        }
        Model.INSTANCE.getLogger().fine("Monitor successfully received the parameters");
    }

    /**
     * Returns parameter values for the given keys from the monitor. If the monitor cannot find any value for the given
     * keys or an connection error occurs an empty string is returned.
     *
     * @param keys the parameter keys
     * @return the parameter key/value pairs or an empty map, if the monitor cannot find any of the parameters or the
     * connection is closed during reading
     */
    public Map<String, String> getParameters(String... keys) {
        if (!isSocketOpen()) {
            return new HashMap<>(0);
        }
        //  TODO      try {
        //            return protocol.gpm(keys);
        //        } catch (IllegalStateException e) {
        //            Model.INSTANCE.getLogger().error(e);
        //            return new HashMap<>(0);
        //        } catch (IOException ignored) {
        //            Model.INSTANCE.getLogger().error("Connection to monitor was lost");
        return new HashMap<>(0);
        //        }
    }

    /**
     * Checks whether the socket is currently connected to the monitor component.
     *
     * @return true, if the socket is connected
     */
    private boolean isSocketOpen() {
        if (!socket.isConnected()) {
            Model.INSTANCE.getLogger().error("Monitor address is invalid");
            return false;
        }
        if (socket.isClosed()) {
            Model.INSTANCE.getLogger().error("Socket at '" + inetSocketAddress.getHostString() + ':' +
                    inetSocketAddress.getPort() + "' is not reachable");
            return false;
        }
        return true;
    }

    /**
     * Disconnects the monitor connection and closes the socket.
     */
    public void disconnect() {
        if (socket == null) {
            return;
        }
        try {
            socket.close();
        } catch (IOException ignored) {
        }
    }
}
