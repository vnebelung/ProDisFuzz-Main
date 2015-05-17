package model.connector;

import model.Model;
import model.utilities.Constants;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.Map;

public class Monitor {

    private static final int TIMEOUT = 50;
    private final Socket socket;
    private Protocol protocol;
    private InetSocketAddress inetSocketAddress;

    public Monitor() {
        super();
        socket = new Socket();
    }

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
        if (inetSocketAddress == null) {
            Model.INSTANCE.getLogger().error("Monitor address must not be empty");
            return false;
        }
        //noinspection OverlyBroadCatchBlock
        try {
            socket.setSoTimeout(TIMEOUT);
            socket.connect(inetSocketAddress, TIMEOUT);
            protocol = new Protocol(socket.getInputStream(), socket.getOutputStream());
        } catch (IOException ignored) {
            Model.INSTANCE.getLogger().error("Could not connect to monitor '" + inetSocketAddress.getHostString() +
                    ':' + inetSocketAddress.getPort() + '\'');
            return false;
        }
        int monitorVersion;
        try {
            monitorVersion = protocol.ayt();
        } catch (IOException ignored) {
            Model.INSTANCE.getLogger().error("Connection to monitor was lost");
            return false;
        }
        if (monitorVersion == -1) {
            Model.INSTANCE.getLogger().error("Monitor '" + inetSocketAddress.getHostString() + ':' +
                    inetSocketAddress.getPort() + "' is not responding");
            return false;
        }
        if (monitorVersion != Constants.RELEASE_NUMBER) {
            Model.INSTANCE.getLogger().error("Version mismatch between monitor '" + inetSocketAddress.getHostString()
                    + ':' + inetSocketAddress.getPort() + "' (release " + monitorVersion + ") and main instance (" +
                    Constants.RELEASE_NUMBER + ')');
            return false;
        }
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
    public void setFuzzingParameters(Map<String, String> parameters) {
        if (!isSocketOpen()) {
            return;
        }
        try {
            if (!protocol.sfp(parameters)) {
                Model.INSTANCE.getLogger().error("Malformed or wrong fuzzing parameters");
                return;
            }
        } catch (IOException ignored) {
            Model.INSTANCE.getLogger().error("Connection to monitor was lost");
            return;
        }
        Model.INSTANCE.getLogger().fine("Monitor successfully received the fuzzing parameters");
    }

    /**
     * Returns a fuzzing parameter value with the given key from the monitor. If the monitor cannot find the value with
     * the given key or an connection error occurs an empty string is returned.
     *
     * @param key the fuzzing parameter key
     * @return the parameter value or an empty string, if the monitor cannot find the parameter or the connection is
     * closed during reading
     */
    public String getFuzzingParameter(String key) {
        if (!isSocketOpen()) {
            return "";
        }
        try {
            return protocol.gfp(key);
        } catch (IOException ignored) {
            Model.INSTANCE.getLogger().error("Connection to monitor was lost");
            return "";
        }
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
        try {
            socket.close();
        } catch (IOException e) {
            Model.INSTANCE.getLogger().error(e);
        }
    }
}
