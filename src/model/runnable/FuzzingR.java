/**
 * This file is part of the ProDisFuzz program.
 * (c) by Volker Nebelung, 2012
 */
package model.runnable;

import model.FuzzedMessage;
import model.ProtocolPart;
import model.ProtocolPart.DataMode;
import model.ProtocolPart.Type;
import model.RunnableThread.RunnableState;
import model.TempRecordFile;
import model.process.OptionsP.Mode;
import model.runnable.component.*;

import java.io.IOException;
import java.io.LineNumberReader;
import java.net.InetSocketAddress;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * The Class FuzzingR implements the runnable which is responsible for fuzzing.
 *
 * @author Volker Nebelung
 * @version 1.0
 */
public class FuzzingR extends AbstractR {

    /**
     * The fuzzing interval.
     */
    private final int interval;

    /**
     * The protocol part list.
     */
    private final List<ProtocolPart> parts;

    /**
     * The fuzzing mode.
     */
    private final Mode mode;

    /**
     * The time of the latest crash.
     */
    private long crashTime;

    /**
     * The start time.
     */
    private long startTime;

    /**
     * The end time.
     */
    private long endTime;

    /**
     * The save communication flag.
     */
    private boolean saveCommunication;

    /**
     * The fuzzing check library component.
     */
    private final FuzzingCheckLibraryC fuzzingCheckLibraryC;

    /**
     * The fuzzing separate infinite component.
     */
    private final FuzzingSepInfMessageC fuzzingSepInfMessageC;

    /**
     * The fuzzing separate infinite component.
     */
    private final FuzzingSepFinMessageC fuzzingSepFinMessageC;

    /**
     * The fuzzing simultaneous infinite component.
     */
    private final FuzzingSimInfMessageC fuzzingSimInfMessageC;

    /**
     * The fuzzing simultaneous infinite component.
     */
    private final FuzzingSimFinMessageC fuzzingSimFinMessageC;

    /**
     * The fuzzing connection component.
     */
    private final FuzzingConnectionC fuzzingConnectionC;

    /**
     * The fuzzing connection component.
     */
    private final FuzzingRecordC fuzzingRecordC;

    /**
     * The record files.
     */
    private final List<TempRecordFile> recordFiles;

    /**
     * Instantiates a new fuzzing runnable.
     *
     * @param parts    the protocol parts
     * @param interval the fuzzing interval
     * @param mode     the fuzzing mode
     * @param timeout  the connection timeout
     */
    public FuzzingR(final List<ProtocolPart> parts, final int interval,
                    final Mode mode, final InetSocketAddress destination,
                    final boolean saveCommunication, final int timeout) {
        super();
        this.interval = interval;
        this.parts = parts;
        this.mode = mode;
        this.saveCommunication = saveCommunication;
        recordFiles = new ArrayList<TempRecordFile>();
        fuzzingCheckLibraryC = new FuzzingCheckLibraryC(this, parts, mode);
        fuzzingSepInfMessageC = new FuzzingSepInfMessageC(this, parts);
        fuzzingSepFinMessageC = new FuzzingSepFinMessageC(this, parts);
        fuzzingSimInfMessageC = new FuzzingSimInfMessageC(this, parts);
        fuzzingSimFinMessageC = new FuzzingSimFinMessageC(this, parts);
        fuzzingConnectionC = new FuzzingConnectionC(this, destination, timeout);
        fuzzingRecordC = new FuzzingRecordC(this);
    }

    /*
     * (non-Javadoc)
     *
     * @see java.lang.Runnable#run()
     */
    @Override
    public void run() {
        startTime = System.currentTimeMillis();
        if (!isInterrupted()) {
            try {
                fuzzingCheckLibraryC.check();
            } catch (NumberFormatException | IOException e) {
                interrupt("e:" + e.getMessage());
            }
            sleep(SLEEPING_TIME);
        }
        if (!isInterrupted()) {
            setStateMessage("i:Starting fuzzing.", RunnableState.RUNNING);
            if (isFinite()) {
                if (mode == Mode.SEPARATE) {
                    try {
                        seperateFiniteFuzzing();
                    } catch (IOException e) {
                        interrupt("e:" + e.getMessage());
                    }
                } else {
                    try {
                        simultaneousFiniteFuzzing();
                    } catch (IOException e) {
                        interrupt("e:" + e.getMessage());
                    }
                }
            } else {
                if (mode == Mode.SEPARATE) {
                    seperateInfiniteFuzzing();
                } else {
                    try {
                        simultaneousInfiniteFuzzing();
                    } catch (IOException e) {
                        interrupt("e:" + e.getMessage());
                    }
                }
            }
        }
        endTime = System.currentTimeMillis();
        spreadUpdate(isInterrupted() ? RunnableState.CANCELED
                : RunnableState.FINISHED);
    }

    /**
     * Executes the fuzzing process for seperate values with an infinite number
     * of iterations.
     */
    private void seperateInfiniteFuzzing() {
        while (!isInterrupted()) {
            sendMessage(fuzzingSepInfMessageC.create(), 0);
            sleep(interval);
        }
    }

    /**
     * Sends a message to the destination
     *
     * @param message the message to sent
     * @param count   the zero-based number of the current try
     */
    private void sendMessage(final FuzzedMessage message, final int count) {
        if (fuzzingConnectionC.send(message)) {
            if (saveCommunication) {
                try {
                    recordFiles.add(fuzzingRecordC.write(message.getBytes(),
                            false, System.currentTimeMillis()));
                    recordFiles.add(fuzzingRecordC.write(
                            fuzzingConnectionC.getLastResponse(), false,
                            System.currentTimeMillis()));
                } catch (IOException e) {
                    interrupt("e: Could not save data to file. Reason: "
                            + e.getMessage());
                }
            }
        } else {
            // If this is the 1. try, save the time of the possible crash;
            // if this is the 1. or 2. try, give an error message and try again;
            // otherwise save crash data
            switch (count) {
                case 0:
                    crashTime = System.currentTimeMillis();
                case 1:
                    final DecimalFormat decimalFormat = new DecimalFormat(",##0.0");
                    // Error interval has a logarithmic style curve
                    final double errorInterval = interval
                            * Math.pow(count + 2, 0.75);
                    setStateMessage(
                            "w:Error while sending message: Target is not reachable. Will try again in "
                                    + (decimalFormat.format(errorInterval / 1000))
                                    + " seconds.", RunnableState.STUCK);
                    sleep((long) errorInterval);
                    // Send the fuzzed message again
                    if (!isInterrupted()) {
                        sendMessage(message, count + 1);
                    }
                    break;
                case 2:
                    setStateMessage(
                            "e:Target was not reachable for 3 times in a row. Information about the crash is being saved.",
                            RunnableState.STUCK);
                    try {
                        recordFiles.add(fuzzingRecordC.write(message.getBytes(),
                                true, crashTime));
                    } catch (IOException e) {
                        interrupt("e:Could not save data to file. Reason: "
                                + e.getMessage());
                    }
                    if (!isInterrupted()) {
                        reconnect(0);
                    }
                    break;
                default:
                    break;
            }
        }
    }

    /**
     * Tries to reconnect after the connection was lost.
     *
     * @param count the zero-based number of the current try
     */
    private void reconnect(final int count) {
        if (fuzzingConnectionC.reconnect()) {
            startTime += (System.currentTimeMillis() - crashTime);
        } else {
            // Error interval has a logarithmic style curve
            final double errorInterval = Math.pow(count + 1, 0.75) * interval;
            final DecimalFormat decimalFormat = new DecimalFormat(",##0.0");
            setStateMessage(
                    "e:Destination server not reachable. Will try again in "
                            + (decimalFormat.format(errorInterval / 1000))
                            + " seconds.", RunnableState.STUCK);
            sleep((long) errorInterval);
            // Try again to connect
            if (!isInterrupted()) {
                reconnect(count + 1);
            }
        }
    }

    /**
     * Executes the fuzzing process for seperate values with an finite number of
     * iterations.
     *
     * @throws IOException
     */
    private void seperateFiniteFuzzing() throws IOException {
        List<ProtocolPart> varParts = getVarParts();
        LineNumberReader lineNumberReader;
        String line;
        // Use a different file for every part
        for (int i = 0; i < varParts.size() && !isInterrupted(); i++) {
            lineNumberReader = new LineNumberReader(Files.newBufferedReader(
                    varParts.get(0).getLibraryPath(), Charset.forName("UTF-8")));
            // Read line by line of the file of the current part
            while ((line = lineNumberReader.readLine()) != null
                    && !isInterrupted()) {
                sendMessage(fuzzingSepFinMessageC.create(i, line, varParts), 0);
                sleep(interval);
            }
        }
    }

    /**
     * Executes the fuzzing process for only simultaneous values with an
     * infinite number of iterations.
     *
     * @throws IOException
     */
    private void simultaneousInfiniteFuzzing() throws IOException {
        while (!isInterrupted()) {
            sendMessage(fuzzingSimInfMessageC.create(), 0);
            sleep(interval);
        }
    }

    /**
     * Executes the fuzzing process for only simultaneous values with an finite
     * number of iterations.
     *
     * @throws IOException
     */
    private void simultaneousFiniteFuzzing() throws IOException {
        LineNumberReader lineNumberReader;
        String line;
        lineNumberReader = new LineNumberReader(
                Files.newBufferedReader(getVarParts().get(0).getLibraryPath(),
                        Charset.forName("UTF-8")));
        // Use the same library file for every protocol part
        while ((line = lineNumberReader.readLine()) != null // NOPMD
                && !isInterrupted()) {
            sendMessage(fuzzingSimFinMessageC.create(line), 0);
            sleep(interval);
        }
        lineNumberReader.close();
    }

    /**
     * Returns only the protocol parts that have the Type VAR.
     *
     * @return the variable protocol parts
     */
    private List<ProtocolPart> getVarParts() {
        final List<ProtocolPart> varParts = new ArrayList<ProtocolPart>();
        for (ProtocolPart part : parts) {
            if (part.getType() == Type.VAR) {
                varParts.add(part);
            }
        }
        return varParts;
    }

    /**
     * Gets the recorded files.
     *
     * @return the recorded files
     */
    public List<TempRecordFile> getRecordFiles() {
        return recordFiles;
    }

    /**
     * Gets the duration of the fuzzing.
     *
     * @return the fuzzing duration in milliseconds.
     */
    public long getDuration() {
        return endTime - startTime;
    }

    /**
     * Gets the start time.
     */
    public long getStartTime() {
        return startTime;
    }

    /**
     * Checks whether there are a finite number of fuzzing iterations
     *
     * @return true, if the number of iterations is finite
     */
    private boolean isFinite() {
        // If there is at least one random part the fuzzing will go on
        // forever
        boolean isFinite = true;
        for (ProtocolPart part : parts) {
            if (part.getType() == Type.VAR
                    && part.getDataMode() == DataMode.RANDOM) {
                isFinite = false;
                break;
            }
        }
        return isFinite;
    }

    /*
     * (non-Javadoc)
     *
     * @see model.runnable.AbstractRunnable#setTotalProgress()
     */
    @Override
    protected void setTotalProgress() {
        if (isFinite()) {
            if (mode == Mode.SEPARATE) {
                totalProgress = 0;
                for (ProtocolPart part : getVarParts()) {
                    totalProgress *= part.getNumOfLibraryLines()
                            * fuzzingConnectionC.getTotalProgress();
                }
            } else {
                totalProgress = getVarParts().get(0).getNumOfLibraryLines()
                        * fuzzingConnectionC.getTotalProgress();
            }
        } else {
            totalProgress = -1;
        }
    }
}
