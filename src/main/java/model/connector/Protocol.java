package model.connector;

import model.Model;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.Map.Entry;

class Protocol {

    private final InputStream inputStream;
    private final OutputStream outputStream;
    private State state;

    /**
     * Instantiates a new monitor protocol responsible for communicating with the remote monitor component.
     *
     * @param inputStream  the monitor socket's input stream
     * @param outputStream the monitor socket's output stream
     */
    public Protocol(InputStream inputStream, OutputStream outputStream) {
        this.inputStream = inputStream;
        this.outputStream = outputStream;
        state = State.NEW;
    }

    /**
     * Sends an "are you there" message that checks whether the monitor component is reachable. If the monitor is
     * receiving this message, it must respond with its version number.
     *
     * @return the monitor's version number or -1 in case of an error
     * @throws IOException if an I/O error occurs when creating the input stream, the socket is closed or the socket is
     *                     not connected
     */
    public int ayt() throws IOException {
        if (state != State.NEW) {
            throw new IllegalStateException("Illegal try to send an AYT command to monitor");
        }
        //noinspection HardCodedStringLiteral
        String message = "AYT 0 ";
        byte[] response = getResponse(message.getBytes(StandardCharsets.UTF_8));
        if (response != null) {
            state = State.CONNECTED;
            return Integer.parseInt(new String(response, StandardCharsets.UTF_8));
        }
        return -1;
    }

    /**
     * Returns the monitor's response to a given message. If the monitor returns an ROK command, the method will return
     * the response's content (maybe an empty array). If the monitor returns an ERR command or an invalid command, the
     * return value will be null.
     *
     * @param message the message to be sent to the monitor
     * @return the monitor's response or null in case of an error in monitor's response
     * @throws IOException if an I/O error occurs when creating the input stream, the socket is closed or the socket is
     *                     not connected
     */
    private byte[] getResponse(byte... message) throws IOException {
        byte[] response = sendReceive(message);
        String command = new String(Arrays.copyOfRange(response, 0, 3), StandardCharsets.UTF_8);
        switch (command) {
            //noinspection HardCodedStringLiteral
            case "ROK":
                int from = 4; // Set from to 4 because of 'CMD L…'
                do {
                    from++;
                } while (response[from - 1] != 32);
                return Arrays.copyOfRange(response, from, response.length);
            //noinspection HardCodedStringLiteral
            case "ERR":
                Model.INSTANCE.getLogger().error("Error in monitor: " + response[2]);
                break;
            default:
                Model.INSTANCE.getLogger().error("Bad answer from monitor");
        }
        return null;
    }

    /**
     * Sends a given message to the monitor and returns the monitor's response.
     *
     * @param message the message to be sent to the monitor
     * @return the monitor's response
     * @throws IOException if an I/O error occurs when creating the input stream, the socket is closed or the socket is
     *                     not connected
     */
    private byte[] sendReceive(byte... message) throws IOException {
        // Inspections suppressed because the two streams can used multiple times by calling this method. It is the
        // Monitor class' responsibility to close the socket correctly

        //noinspection IOResourceOpenedButNotSafelyClosed,resource
        DataInputStream in = new DataInputStream(inputStream);
        //noinspection IOResourceOpenedButNotSafelyClosed,resource
        DataOutputStream out = new DataOutputStream(outputStream);
        out.write(message);
        out.flush();
        List<Byte> response = new ArrayList<>();
        for (int i = 0; i < 4; i++) {
            response.add(in.readByte());
        }
        StringBuilder stringBuilder = new StringBuilder();
        while (true) {
            byte b = in.readByte();
            response.add(b);
            char c = (char) b;
            if (c == ' ') {
                break;
            } else {
                stringBuilder.append(c);
            }
        }
        int length = Integer.parseInt(stringBuilder.toString());
        for (int i = 0; i < length; i++) {
            response.add(in.readByte());
        }
        byte[] result = new byte[response.size()];
        for (int i = 0; i < response.size(); i++) {
            result[i] = response.get(i);
        }
        return result;
    }

    /**
     * Sends a "set fuzzing parameters" message that includes all parameters for the fuzzing process. If the monitor is
     * receiving this message and understands it, it responds with "ROK".
     *
     * @return true, if the monitor confirms with ROK
     * @throws IOException if an I/O error occurs when creating the input stream, the socket is closed or the socket is
     *                     not connected
     */
    public boolean sfp(Map<String, String> parameters) throws IOException {
        if ((state != State.CONNECTED) && (state != State.CONFIGURED)) {
            throw new IllegalStateException("Error in communication with monitor: illegal try to set state to " +
                    "'CONFIGURED'");
        }
        if (parameters.isEmpty()) {
            throw new IllegalArgumentException("Parameters submitted to monitor must not be empty");
        }
        StringBuilder stringBuilder = new StringBuilder();
        for (Entry<String, String> each : parameters.entrySet()) {
            // Format: key:value,key:value, …
            stringBuilder.append(each.getKey()).append('=').append(each.getValue()).append(',');
        }
        stringBuilder.deleteCharAt(stringBuilder.length() - 1);
        //noinspection HardCodedStringLiteral
        String message = "SFP " + stringBuilder.length() + ' ' + stringBuilder;
        byte[] response = getResponse(message.getBytes(StandardCharsets.UTF_8));
        if (response != null) {
            state = State.CONFIGURED;
            return true;
        }
        return false;
    }

    /**
     * Sends a "get fuzzing parameter" message that returns a fuzzing parameter. If the monitor is receiving this
     * message and understands it, it responds the parameter value with the given name. If the monitor cannot find a
     * parameter with the given name, the monitor will respond an empty string
     *
     * @return the value of the parameter, or an empty string if the monitor cannot find the parameter
     * @throws IOException if an I/O error occurs when creating the input stream, the socket is closed or the socket is
     *                     not connected
     */
    public String gfp(String parameter) throws IOException {
        if ((state != State.CONNECTED) && (state != State.CONFIGURED)) {
            throw new IllegalStateException("Error in communication with monitor: illegal try to set state to " +
                    "'CONFIGURED'");
        }
        //noinspection HardCodedStringLiteral
        String message = "GFP " + parameter.length() + ' ' + parameter;
        byte[] response = getResponse(message.getBytes(StandardCharsets.UTF_8));
        if (response != null) {
            return new String(response, StandardCharsets.UTF_8);
        }
        return "";
    }

    /**
     * Sends a "call target with data" message that tells the monitor to call the target with the given fuzzed data. The
     * monitor responds whether the target has crashed when executing the data.
     *
     * @param data the fuzzed data for the target
     * @return true, if the data caused a crash
     * @throws IOException if an I/O error occurs when creating the input stream, the socket is closed or the socket is
     *                     not connected
     */
    public boolean ctd(byte... data) throws IOException {
        if ((state != State.CONFIGURED) && (state != State.FUZZING)) {
            throw new IllegalStateException("Error in communication with monitor: illegal try to set state to " +
                    "'FUZZING'");
        }
        byte[] message = new byte[4 + String.valueOf(data.length).length() + 1 + data.length];
        //noinspection HardCodedStringLiteral
        String header = "CTD " + data.length + ' ';
        System.arraycopy(header.getBytes(StandardCharsets.UTF_8), 0, message, 0, header.length());
        System.arraycopy(data, 0, message, header.length(), data.length);
        byte[] response = getResponse(message);
        if (response != null) {
            state = State.FUZZING;
            Map<String, String> targetReaction = extractKeyValues(new String(response, StandardCharsets.UTF_8));
            //noinspection HardCodedStringLiteral
            return "yes".equals(targetReaction.get("crashed"));
        }
        return false;
    }

    /**
     * Transforms a given string to a key value structure by extracting the key/value pairs.
     *
     * @param string the input sequence
     * @return the key/value structure
     */
    private static Map<String, String> extractKeyValues(String string) {
        String[] keyValues = string.split(",");
        Map<String, String> result = new HashMap<>(keyValues.length);
        for (String each : keyValues) {
            String[] keyValue = each.split("=");
            result.put(keyValue[0], keyValue[1]);
        }
        return result;
    }

    private enum State {NEW, CONNECTED, CONFIGURED, FUZZING}
}
