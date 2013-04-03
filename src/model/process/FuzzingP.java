/**
 j * This file is part of the ProDisFuzz program.
 * (c) by Volker Nebelung, 2012
 */
package model.process;

import model.ProtocolPart;
import model.TempRecordFile;
import model.process.OptionsP.Mode;
import model.runnable.FuzzingR;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.Duration;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.Observable;

/**
 * The Class FuzzingP encapsulates the fuzzing process through a socks server.
 *
 * @author Volker Nebelung
 * @version 1.0
 */
public class FuzzingP extends AbstractThreadP {

    /**
     * The fuzzing mode.
     */
    private Mode mode;

    /**
     * The destination address and port.
     */
    private InetSocketAddress destination;

    /**
     * The fuzzing interval in milliseconds.
     */
    private int interval;

    /**
     * The protocol parts.
     */
    private List<ProtocolPart> parts;

    /**
     * The recorded files.
     */
    private List<TempRecordFile> recordFiles;

    /**
     * The fuzzing duration.
     */
    private Duration duration;

    /**
     * The start time of the fuzzing runnable.
     */
    private long startTime;

    /**
     * The last done fuzzing progress.
     */
    private int lastProgress;

    /**
     * The save communication flag.
     */
    private boolean saveCommunication;

    /**
     * The connection timeout in milliseconds.
     */
    private int timeout;

    /**
     * Instantiates a new fuzzing process.
     */
    public FuzzingP() {
        super();
        parts = new ArrayList<ProtocolPart>();
        recordFiles = new ArrayList<TempRecordFile>();
        startTime = -1;
        lastProgress = 0;
        try {
            duration = DatatypeFactory.newInstance().newDuration(0);
        } catch (DatatypeConfigurationException e) { // NOPMD
            // Should not happen
        }
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
     * Returns the fuzzing mode.
     *
     * @return the fuzzing mode
     */
    public Mode getMode() {
        return mode;
    }

    /**
     * Initializes the protocol parts.
     *
     * @param parts the protocol parts
     */
    public void initParts(final List<ProtocolPart> parts) {
        this.parts = parts;
    }

    /**
     * Initializes all options.
     *
     * @param destination       the destination
     * @param mode              the fuzzing mode
     * @param interval          the fuzzing interval
     * @param saveCommunication the save communication flag
     * @param timeout           the connection timeout
     */
    public void initOptions(final InetSocketAddress destination,
                            final Mode mode, final int interval,
                            final boolean saveCommunication, final int timeout) {
        this.destination = destination;
        this.mode = mode;
        this.interval = interval;
        this.saveCommunication = saveCommunication;
        this.timeout = timeout;
    }

    /**
     * Resets all variables to the default value and notifies all observers.
     */
    public void reset() {
        parts.clear();
        try {
            for (int i = 0; i < recordFiles.size(); i++) {
                Files.delete(recordFiles.get(i).getFilePath());
            }
        } catch (IOException e) { // NOPMD
            // Should not happen
        }
        recordFiles.clear();
        startTime = -1;
        lastProgress = 0;
        try {
            duration = DatatypeFactory.newInstance().newDuration(0);
        } catch (DatatypeConfigurationException e) { // NOPMD
            // Should not happen
        }
        super.reset();
    }

    /**
     * Starts the fuzzing thread.
     */
    public void start() {
        super.start(new FuzzingR(parts, interval, mode, destination,
                saveCommunication, timeout));
        startTime = -1;
    }

    @Override
    public void update(final Observable observable, final Object arg) {
        final FuzzingR data = (FuzzingR) observable;
        recordFiles = data.getRecordFiles();
        startTime = data.getStartTime();
        try {
            duration = DatatypeFactory.newInstance().newDuration(
                    data.getDuration());
        } catch (DatatypeConfigurationException e) { // NOPMD
            // Should not happen
        }
        lastProgress = data.getProgress();
        super.update(observable, arg);
    }

    /**
     * Gets all recorded files.
     *
     * @return the recorded files
     */
    public List<TempRecordFile> getRecordFiles() {
        return recordFiles;
    }

    /**
     * Gets the number of recorded crashes.
     *
     * @return the number of crashes
     */
    public int getNumOfCrashes() {
        int num = 0;
        for (TempRecordFile file : recordFiles) {
            if (file.isCrash()) {
                num++;
            }
        }
        return num;
    }

    /**
     * Gets the fuzzing duration.
     *
     * @return the fuzzing duration
     */
    public Duration getDuration() {
        return duration;
    }

    /**
     * Gets the destination
     *
     * @return the destination
     */
    public InetSocketAddress getDestination() {
        return destination;
    }

    /**
     * Gets the interval
     *
     * @return the interval
     */
    public int getInterval() {
        return interval;
    }

    /**
     * Gets the protocol parts
     *
     * @return the list of protocol parts
     */
    public List<ProtocolPart> getParts() {
        return parts;
    }

    /**
     * Gets the protocol parts
     *
     * @return the progress of the last fuzzing round
     */
    public int getLastProgress() {
        return lastProgress;
    }

    /**
     * Gets the start time of the runnable. Start time is -1 if the runnable is currently not running
     *
     * @return the start time of the runnable
     */
    public long getStartTime() {
        return startTime;
    }

    /**
     * Gets the communication flag that indicates whether all messages interchanged between the fuzzer and the destination shall be saved to files.
     *
     * @return the save communication flag
     */
    public boolean isSaveCommunication() {
        return saveCommunication;
    }

}