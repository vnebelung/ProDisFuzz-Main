/**
 * This file is part of the ProDisFuzz program.
 * (c) by Volker Nebelung, 2012
 */
package model.process;

import model.ProtocolPart;
import model.ProtocolPart.DataMode;
import model.ProtocolPart.Type;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

/**
 * The Class OptionsP encapsulates the process of setting all fuzzing options
 * for the fuzzing process through a socks server.
 *
 * @author Volker Nebelung
 * @version 1.0
 */
public class OptionsP extends AbstractP {

    /**
     * The minimum port number.
     */
    public final static int PORT_MIN = 1;

    /**
     * The maximum port number.
     */
    public final static int PORT_MAX = 65535;

    /**
     * The minimum send interval.
     */
    public final static int INTERVAL_MIN = 100;

    /**
     * The maximum send interval.
     */
    public final static int INTERVAL_MAX = 30000;

    /**
     * The minimum send interval.
     */
    public final static int TIMEOUT_MIN = 50;

    /**
     * The maximum send interval.
     */
    public final static int TIMEOUT_MAX = 10000;

    /**
     * The fuzzing modes.
     */
    public static enum Mode {
        /**
         */
        SIMULTANEOUS, /**
         */
        SEPARATE
    }

    ;

    /**
     * The fuzzing mode.
     */
    private Mode mode;

    /**
     * The destination address and port.
     */
    private InetSocketAddress destination;

    /**
     * The connection timeout in milliseconds.
     */
    private int timeout;

    /**
     * The fuzzing interval in milliseconds.
     */
    private int interval;

    /**
     * The file list.
     */
    private List<ProtocolPart> parts;

    /**
     * The save communication flag.
     */
    private boolean saveCommunication;

    /**
     * The flaag indicating whether a connection to the destination could be established.
     */
    private boolean destinationOk;

    /**
     * Resets all variables to the default value and notifies all observers.
     */
    public void reset() {
        parts.clear();
        mode = Mode.SEPARATE;
        saveCommunication = false;
        destination = new InetSocketAddress("", PORT_MIN);
        destinationOk = false;
        interval = (int) (INTERVAL_MIN * 5);
        timeout = TIMEOUT_MIN * 5;
        spreadUpdate(true);
    }

    /**
     * Initiates the protocol parts.
     *
     * @param parts the protocol parts
     */
    public void initParts(final List<ProtocolPart> parts) {
        this.parts = parts;
        spreadUpdate(false);
    }

    /**
     * Instantiates a new options process.
     */
    public OptionsP() {
        super();
        saveCommunication = false;
        parts = new ArrayList<ProtocolPart>();
        mode = Mode.SEPARATE;
        destination = new InetSocketAddress("", PORT_MIN);
        destinationOk = false;
        interval = (int) (INTERVAL_MIN * 5);
        timeout = TIMEOUT_MIN * 5;
    }

    /**
     * Sets the path of a library file for a given variable protocol file.
     *
     * @param path  the path to the library file
     * @param index the index of the variable part
     */
    public void setLibraryPath(final String path, final int index) {
        boolean hasChanged = false;
        switch (mode) {
            case SIMULTANEOUS:
                for (ProtocolPart varPart : getVarParts()) {
                    if (varPart.setLibraryPath(path)) {
                        hasChanged = true;
                    }
                }
                break;
            case SEPARATE:
                if (getVarParts().get(index).setLibraryPath(path)) {
                    hasChanged = true;
                }
                break;
            default:
                break;
        }
        if (hasChanged) {
            spreadUpdate(false);
        }
    }

    /**
     * Returns only the protocol parts which have the Type VAR.
     *
     * @return the variable protocol parts
     */
    public List<ProtocolPart> getVarParts() {
        final List<ProtocolPart> varParts = new ArrayList<ProtocolPart>();
        for (ProtocolPart part : parts) {
            if (part.getType() == Type.VAR) {
                varParts.add(part);
            }
        }
        return varParts;
    }

    /**
     * Returns the fuzzing mode.
     *
     * @return the fuzzing mode
     */
    public Mode getMode() {
        return mode;
    }

    /**
     * Sets the fuzzing mode.
     *
     * @param mode the fuzzing mode
     */
    public void setMode(final Mode mode) {
        final Mode oldMode = this.mode;
        if (this.mode != mode) {
            this.mode = mode;
            spreadUpdate(false);
        }
        switch (mode) {
            case SIMULTANEOUS:
                setDataMode(getVarParts().get(0).getDataMode(), 0);
                break;
            case SEPARATE:
                // If the old mode was not SEPERATE, the library of all variable
                // parts except the first one will be set to null
                if (oldMode != Mode.SEPARATE) {
                    for (int i = 1; i < getVarParts().size(); i++) {
                        setLibraryPath("", i);
                    }
                }
                break;
            default:
                break;
        }
    }

    /**
     * Sets the destination address and port.
     *
     * @param address the destination address
     * @param port    the destination port
     */
    public void setDestination(final String address, final int port) {
        // Update only if the new socket is not the same as the old one
        if (!destination.getHostString().equals(address)
                || destination.getPort() != port) {
            destination = new InetSocketAddress(address, port);
            destinationOk = testDestination();
            spreadUpdate(false);
        }
    }

    /**
     * Returns the destination.
     *
     * @return the destination
     */
    public InetSocketAddress getDestination() {
        return destination;
    }

    /**
     * Gets the fuzzing interval.
     *
     * @return the interval
     */
    public int getInterval() {
        return interval;
    }

    /**
     * Gets the connection timeout.
     *
     * @return the timeout in ms
     */
    public int getTimeout() {
        return timeout;
    }

    /**
     * Sets the connection timeout.
     *
     * @param timeout the timeout to set
     */
    public void setTimeout(final int timeout) {
        this.timeout = Math.max(timeout, TIMEOUT_MIN);
        this.timeout = Math.min(this.timeout, TIMEOUT_MAX);
    }

    /**
     * Sets the fuzzing interval.
     *
     * @param interval the interval to set
     */
    public void setInterval(final int interval) {
        this.interval = Math.max(interval, INTERVAL_MIN);
        this.interval = Math.min(this.interval, INTERVAL_MAX);
    }

    /**
     * Checks whether a connection to the destination can be established.
     *
     * @return true if a connection could be established
     */
    private boolean testDestination() { // NOPMD
        boolean success = true;
        Socket socket = null;
        try {
            // Establish a test connection without sending any data
            socket = new Socket();
            socket.setSoTimeout(timeout);
            socket.connect(destination, timeout);
        } catch (IOException e) {
            success = false;
        } finally {
            try {
                if (socket != null) {
                    socket.close();
                }
            } catch (IOException e) { // NOPMD
                // Should not happen
            }
        }
        return success;
    }

    /**
     * Sets the data mode according to the mode chosen. If the mode is
     * SIMULTANEOUS all variable parts will have the same data mode.
     *
     * @param dataMode the data mode
     * @param index    the index of the variable part
     */
    public void setDataMode(final DataMode dataMode, final int index) {
        boolean hasChanged = false;
        switch (mode) {
            case SEPARATE:
                if (getVarParts().get(index).getDataMode() != dataMode) {
                    getVarParts().get(index).setDataMode(dataMode);
                    hasChanged = true;
                }
                break;
            case SIMULTANEOUS:
                // Set the library file for all variable parts the same as the first
                // variable part
                for (int i = 0; i < getVarParts().size(); i++) {
                    if (getVarParts().get(i).getDataMode() != dataMode) {
                        hasChanged = true;
                        getVarParts().get(i).setDataMode(dataMode);
                        if (getVarParts().get(0).getLibraryPath() == null) {
                            setLibraryPath("", 0);
                        } else {
                            setLibraryPath(getVarParts().get(0).getLibraryPath()
                                    .toString(), 0);
                        }
                    }

                }
                break;
            default:
                break;
        }
        if (hasChanged) {
            spreadUpdate(false);
        }
    }

    /**
     * Gets all protocol parts
     *
     * @return the protocol parts
     */
    public List<ProtocolPart> getParts() {
        return parts;
    }

    /**
     * Checks whether the options of all variable protocol parts are filled with
     * complete and valid parameters.
     *
     * @return true if the options of all variable protocol parts are filled
     *         with complete and valid parameters
     */
    public boolean varPartsOk() {
        boolean ok = true; // NOPMD
        for (ProtocolPart part : getVarParts()) {
            // If there is only one part where the parameters are not complete
            // or not valid, it returns false
            if (part.getDataMode() == DataMode.LIBRARY
                    && part.getLibraryPath() == null) {
                ok = false;
                break;
            }
        }
        return ok;
    }

    /**
     * Sets the communication flag that indicates whether all messages interchanged between the fuzzer and the destination shall be saved to files.
     *
     * @param saveCommunication the save communication flag
     */
    public void setSaveCommunication(final boolean saveCommunication) {
        this.saveCommunication = saveCommunication;
    }

    /**
     * Gets the communication flag that indicates whether all messages interchanged between the fuzzer and the destination shall be saved to files.
     *
     * @return the save communication flag
     */
    public boolean isSaveCommunication() {
        return saveCommunication;
    }

    /**
     * Gets the flag indication whether a connection to the destination could
     * successfully be established or not.
     *
     * @return true if a connection could successfully be established
     */
    public boolean isDestiantionOk() {
        return destinationOk;
    }

}