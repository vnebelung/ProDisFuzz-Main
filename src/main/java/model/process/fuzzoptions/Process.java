/*
 * This file is part of ProDisFuzz, modified on 28.08.16 19:39.
 * Copyright (c) 2013-2016 Volker Nebelung <vnebelung@prodisfuzz.net>
 * This work is free. You can redistribute it and/or modify it under the
 * terms of the Do What The Fuck You Want To Public License, Version 2,
 * as published by Sam Hocevar. See the COPYING file for more details.
 */

package model.process.fuzzoptions;

import model.process.AbstractProcess;
import model.process.AbstractRunner;
import model.process.AbstractRunner.ExternalState;
import model.protocol.InjectedProtocolBlock.DataInjection;
import model.protocol.InjectedProtocolStructure;
import model.protocol.ProtocolStructure;

import java.net.InetSocketAddress;
import java.nio.file.Path;
import java.util.Observable;

/**
 * This class is the fuzz options process, responsible for setting all fuzz options.
 */
public class Process extends AbstractProcess {


    private InjectedProtocolStructure injectedProtocolStructure;
    private InjectionMethod injectionMethod; // TODO: Refactor into the injection protocol structure
    private int timeout;
    private int interval;
    private RecordingMethod recordingMethod;
    private InetSocketAddress target;

    /**
     * Constructs a new fuzz options process.
     */
    public Process() {
        super();
        injectedProtocolStructure = new InjectedProtocolStructure();
    }

    /**
     * Initializes the fuzz options process.
     *
     * @param protocolStructure the protocol structure
     */
    public void init(ProtocolStructure protocolStructure) {
        injectedProtocolStructure = new InjectedProtocolStructure(protocolStructure);
        timeout = 5 * TimeoutRunner.TIMEOUT_MIN;
        interval = 5 * IntervalRunner.INTERVAL_MIN;
        injectionMethod = InjectionMethod.SIMULTANEOUS;
        recordingMethod = RecordingMethod.CRITICAL;
        target = null;
        spreadUpdate(State.IDLE);
    }

    @Override
    public void reset() {
        super.reset();
        injectedProtocolStructure.clear();
        timeout = 5 * TimeoutRunner.TIMEOUT_MIN;
        interval = 5 * IntervalRunner.INTERVAL_MIN;
        injectionMethod = InjectionMethod.SIMULTANEOUS;
        recordingMethod = RecordingMethod.CRITICAL;
        target = null;
        spreadUpdate(State.IDLE);
    }

    /**
     * Returns the data injection method. The injection method specifies the mechanism data is injected into the
     * variable protocol parts
     *
     * @return the injection method
     */
    public InjectionMethod getInjectionMethod() {
        return injectionMethod;
    }

    /**
     * Starts the process for setting the method for injecting data to the given value.
     *
     * @param injectionMethod the injection method
     */
    public void setInjectionMethod(InjectionMethod injectionMethod) {
        AbstractRunner runner = new InjectionMethodRunner(injectedProtocolStructure.copy(), injectionMethod);
        runner.addObserver(this);
        submitToThreadPool(runner);
    }

    /**
     * Returns the setting to record the communication data.
     *
     * @return the communication save method
     */
    public RecordingMethod getRecordingMethod() {
        return recordingMethod;
    }

    /**
     * Starts the process for setting the method for recording communication data sent between ProDisFuzz and the
     * target.
     *
     * @param recordingMethod the recording method
     */
    public void setRecordingMethod(RecordingMethod recordingMethod) {
        AbstractRunner runner = new RecordingMethodRunner(recordingMethod);
        runner.addObserver(this);
        submitToThreadPool(runner);
    }

    /**
     * Starts the process for setting a given library file for a protocol block identified through the given index.
     *
     * @param index   the index of the injected protocol block
     * @param library the library file to set
     */
    public void setLibraryForVarProtocolBlock(int index, Path library) {
        AbstractRunner runner = new LibraryRunner(injectedProtocolStructure.copy(), injectionMethod, index, library);
        runner.addObserver(this);
        submitToThreadPool(runner);
    }

    /**
     * Starts the process for setting the kind of injection data to the given value for a protocol block identified
     * through the given index.
     *
     * @param index         the index of the injected protocol block
     * @param dataInjection the data injection
     */
    public void setInjectionDataForVarProtocolBlock(int index, DataInjection dataInjection) {
        AbstractRunner runner =
                new InjectionDataRunner(injectedProtocolStructure.copy(), dataInjection, injectionMethod, index);
        runner.addObserver(this);
        submitToThreadPool(runner);
    }

    /**
     * Sets the target's address and port.
     *
     * @param address the target's address
     * @param port    the target's port
     */
    public void setTarget(String address, int port) {
        if (target != null && target.getHostString().equals(address) && target.getPort() == port) {
            return;
        }
        AbstractRunner runner = new TargetRunner(address, port, timeout);
        runner.addObserver(this);
        submitToThreadPool(runner);
    }

    /**
     * Returns whether the use-defined target is reachable.
     *
     * @return true if the target is reachable
     */
    public boolean isTargetReachable() {
        return target != null;
    }

    /**
     * Returns the fuzzing target.
     *
     * @return the target or null, if there is no valid target
     */
    public InetSocketAddress getTarget() {
        return target;
    }

    /**
     * Returns the fuzzing interval, that is the pause between two fuzzing iterations.
     *
     * @return the interval in ms
     */
    public int getInterval() {
        return interval;
    }

    /**
     * Starts the process for setting the interval value to the given value, that is the pause between two fuzzing
     * iterations.
     *
     * @param interval the fuzzing interval in ms
     */
    public void setInterval(int interval) {
        if (interval == this.interval) {
            return;
        }
        AbstractRunner runner = new IntervalRunner(interval);
        runner.addObserver(this);
        submitToThreadPool(runner);
    }

    /**
     * Returns the timeout, that is the time in which a target application must react on input data before a crash
     * occurrence is assumed.
     *
     * @return the timeout in ms
     */
    public int getTimeout() {
        return timeout;
    }

    /**
     * Starts the process for setting the timeout value to the given value, that is the time in which a target
     * application must react on input data before a crash occurrence is assumed.y
     *
     * @param timeout the timeout in ms
     */
    public void setTimeout(int timeout) {
        if (timeout == this.timeout) {
            return;
        }
        AbstractRunner runner = new TimeoutRunner(timeout);
        runner.addObserver(this);
        submitToThreadPool(runner);
    }

    /**
     * Returns the injected protocol structure. Each block has its particular fuzz options.
     *
     * @return the injected protocol structure
     */
    public InjectedProtocolStructure getInjectedProtocolStructure() {
        return injectedProtocolStructure;
    }

    @SuppressWarnings({"OverlyComplexMethod", "InstanceofConcreteClass"})
    @Override
    public void update(Observable o, Object arg) {
        ExternalState state = (ExternalState) arg;
        switch (state) {
            case IDLE:
                spreadUpdate(State.IDLE);
                break;
            case RUNNING:
                spreadUpdate(State.RUNNING);
                break;
            case FINISHED:
                //noinspection ChainOfInstanceofChecks,InstanceofInterfaces,IfStatementWithTooManyBranches
                if (o instanceof RecordingMethodRunner) {
                    updateRecording((RecordingMethodRunner) o);
                    spreadUpdate(State.IDLE);
                } else //noinspection InstanceofConcreteClass,InstanceofConcreteClass
                    if (o instanceof LibraryRunner) {
                        updateLibrary((LibraryRunner) o);
                        spreadUpdate(State.IDLE);
                    } else //noinspection InstanceofConcreteClass
                        if (o instanceof InjectionDataRunner) {
                            updateInjectionData((InjectionDataRunner) o);
                            spreadUpdate(State.IDLE);
                        } else //noinspection InstanceofConcreteClass
                            if (o instanceof InjectionMethodRunner) {
                                updateInjectionMethod((InjectionMethodRunner) o);
                                spreadUpdate(State.IDLE);
                            } else //noinspection InstanceofConcreteClass
                                if (o instanceof IntervalRunner) {
                                    updateInterval((IntervalRunner) o);
                                    spreadUpdate(State.IDLE);
                                } else //noinspection InstanceofConcreteClass
                                    if (o instanceof TimeoutRunner) {
                                        updateTimeout((TimeoutRunner) o);
                                        spreadUpdate(State.IDLE);
                                    } else //noinspection InstanceofConcreteClass
                                        if (o instanceof TargetRunner) {
                                            updateTarget((TargetRunner) o);
                                            spreadUpdate(State.IDLE);
                                        }
        }
    }

    /**
     * Updates this process from the target runner.
     *
     * @param runner the target runner
     */
    private void updateTarget(TargetRunner runner) {
        target = runner.getTarget();
    }

    /**
     * Updates this process from the timeout runner.
     *
     * @param runner the timeout runner
     */
    private void updateTimeout(TimeoutRunner runner) {
        timeout = runner.getTimeout();
    }

    /**
     * Updates this process from the interval runner.
     *
     * @param runner the interval runner
     */
    private void updateInterval(IntervalRunner runner) {
        interval = runner.getInterval();
    }

    /**
     * Updates this process from the injection method runner.
     *
     * @param runner the injection method runner
     */
    private void updateInjectionMethod(InjectionMethodRunner runner) {
        injectedProtocolStructure = runner.getInjectedProtocolStructure();
        injectionMethod = runner.getInjectionMethod();
    }

    /**
     * Updates this process from the injection data runner.
     *
     * @param runner the injection data runner
     */
    private void updateInjectionData(InjectionDataRunner runner) {
        injectedProtocolStructure = runner.getInjectedProtocolStructure();
    }

    /**
     * Updates this process from the library runner.
     *
     * @param runner the library runner
     */
    private void updateLibrary(LibraryRunner runner) {
        injectedProtocolStructure = runner.getInjectedProtocolStructure();
    }

    /**
     * Updates this process from the recording method runner.
     *
     * @param runner the recording method runner
     */
    private void updateRecording(RecordingMethodRunner runner) {
        recordingMethod = runner.getRecordingMethod();
    }

    public enum RecordingMethod {ALL, CRITICAL}

    public enum InjectionMethod {SIMULTANEOUS, SEPARATE}
}
