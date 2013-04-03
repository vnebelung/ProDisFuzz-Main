/**
 * This file is part of the ProDisFuzz program.
 * (c) by Volker Nebelung, 2012
 */
package model.runnable.component;

import model.FuzzedMessage;
import model.RunnableThread.RunnableState;
import model.runnable.AbstractR;

import java.io.*;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketTimeoutException;

/**
 * The Class FuzzingConnectionC implements the functionality to send a given
 * message to the destination.
 *
 * @author Volker Nebelung
 * @version 1.0
 */
public class FuzzingConnectionC extends AbstractC { // NOPMD

    /**
     * The destination address and port.
     */
    private final InetSocketAddress destination;

    /**
     * The last response.
     */
    private byte[] lastResponse;

    /**
     * The connection timeout in milliseconds.
     */
    private final int timeout;

    /**
     * Instantiates a new fuzzing connection component.
     *
     * @param runnable    the parent runnable
     * @param destination the destination
     * @param timeout     the connection timeout
     */
    public FuzzingConnectionC(final AbstractR runnable,
                              final InetSocketAddress destination, final int timeout) {
        super(runnable);
        this.destination = destination;
        lastResponse = new byte[0];
        this.timeout = timeout;
    }

    /**
     * Sends a message over the given proxy server to the destination.
     *
     * @param message the message
     * @return true if the message was successfully sent
     */
    public boolean send(final FuzzedMessage message) { // NOPMD
        runnable.setStateMessage("i:Sending fuzzed message ...",
                RunnableState.RUNNING);
        boolean success = true;
        lastResponse = new byte[0];
        final Socket socket = new Socket();
        DataInputStream in = null; // NOPMD
        DataOutputStream out = null;
        try {
            // Connect to destination
            socket.setSoTimeout(timeout);
            socket.connect(destination, timeout);
            out = new DataOutputStream(socket.getOutputStream());
            in = new DataInputStream(socket.getInputStream());
            try {
                while (true) {
                    in.readByte();
                }
            } catch (SocketTimeoutException e) { // NOPMD
            }
            out.write(message.getBytes());
            out.flush();
            // Read the response in blocks of 1024 bytes
            try {
                final byte[] buffer = new byte[1024];
                byte[] tmpBuffer;
                int countRead = -1;
                while ((countRead = in.read(buffer, 0, buffer.length)) > -1) { // NOPMD
                    // Temporary buffer size = bytes already read + bytes last
                    // read
                    tmpBuffer = new byte[lastResponse.length + countRead]; // NOPMD
                    System.arraycopy(lastResponse, 0, tmpBuffer, 0,
                            lastResponse.length);
                    System.arraycopy(buffer, 0, tmpBuffer, lastResponse.length,
                            countRead);
                    lastResponse = tmpBuffer;
                }
            } catch (SocketTimeoutException e) { // NOPMD
                // Timeout expected
            }
            if (lastResponse.length == 0) {
                success = false;
            }
        } catch (IOException e) {
            success = false;
        } finally {
            // Close connection
            try {
                if (in != null) {
                    in.close();
                }
                if (out != null) {
                    out.close();
                }
                if (socket != null) {
                    socket.close();
                }
            } catch (IOException e) { // NOPMD
                // SHould not happen
            }

        }
        if (success) {
            runnable.increaseProgress("s:done.", RunnableState.RUNNING);
        }
        return success;
    }

    /**
     * Tries to reestablish a connection to the destination.
     *
     * @return true if a connection could be established
     */
    public boolean reconnect() {
        boolean success = true;
        runnable.setStateMessage("i:Trying to reconnect to destination ...",
                RunnableState.STUCK);
        // Open connection
        final Socket socket = new Socket();
        DataOutputStream out = null;
        BufferedReader in = null; // NOPMD
        try {
            socket.setSoTimeout(timeout);
            socket.connect(destination, timeout);
            out = new DataOutputStream(socket.getOutputStream());
            in = new BufferedReader(new InputStreamReader(
                    socket.getInputStream()));
            out.writeBoolean(false);
            final String response = in.readLine();
            if (response.isEmpty()) {
                success = false;
            }
        } catch (IOException e) {
            success = false;
        } finally {
            // Close connection
            try {
                if (out != null) {
                    out.close();
                }
                if (in != null) {
                    in.close();
                }
                if (socket != null) {
                    socket.close();
                }
            } catch (IOException e) { // NOPMD
            }

        }
        if (success) {
            runnable.setStateMessage("s:successful.", RunnableState.RUNNING);
        }
        return success;
    }

    /**
     * Gets the bytes of the last response from the server
     *
     * @return the last response
     */
    public byte[] getLastResponse() {
        return lastResponse; // NOPMD
    }

    /*
     * (non-Javadoc)
     *
     * @see model.runnable.component.AbstractComponent#getTotalProgress()
     */
    @Override
    public int getTotalProgress() {
        return 1;
    }

}