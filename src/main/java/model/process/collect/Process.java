/*
 * This file is part of ProDisFuzz, modified on 28.08.16 20:30.
 * Copyright (c) 2013-2016 Volker Nebelung <vnebelung@prodisfuzz.net>
 * This work is free. You can redistribute it and/or modify it under the
 * terms of the Do What The Fuck You Want To Public License, Version 2,
 * as published by Sam Hocevar. See the COPYING file for more details.
 */

package model.process.collect;

import model.Model;
import model.process.AbstractProcess;
import model.process.AbstractRunner;
import model.process.AbstractRunner.ExternalState;
import model.protocol.ProtocolFile;

import java.util.*;
import java.util.stream.Collectors;

/**
 * This class is the collect process, responsible for controlling the collecting of protocol files.
 */
public class Process extends AbstractProcess {

    private List<ProtocolFile> files;
    private Map<String, Boolean> selections;
    private boolean isDirectoryValid;

    /**
     * Constructs a new collect process.
     */
    public Process() {
        super();
        files = new ArrayList<>();
        selections = new HashMap<>();
        isDirectoryValid = false;
    }

    /**
     * Returns the files of the current folder.
     *
     * @return the collected files
     */
    @SuppressWarnings("TypeMayBeWeakened")
    public Set<ProtocolFile> getFiles() {
        return Collections.unmodifiableSet(new HashSet<>(files));
    }

    /**
     * Returns all files that are marked as selections by the user.
     *
     * @return the selections files
     */
    public Set<ProtocolFile> getSelectedFiles() {
        Set<ProtocolFile> result = new HashSet<>(selections.size());
        result.addAll(files.stream().filter(each -> selections.get(each.getName())).collect(Collectors.toList()));
        return Collections.unmodifiableSet(result);
    }

    @Override
    public void reset() {
        super.reset();
        isDirectoryValid = false;
        files.clear();
        selections.clear();
        spreadUpdate(State.IDLE);
    }

    @SuppressWarnings("RefusedBequest")
    @Override
    public boolean isComplete() {
        return getNumOfSelectedFiles() >= 2;
    }

    /**
     * Starts the collect process for reading a given directory.
     *
     * @param directory the directory path
     */
    public void readDirectory(String directory) {
        AbstractRunner runner = new DirectoryRunner(directory);
        runner.addObserver(this);
        submitToThreadPool(runner);
    }

    /**
     * Starts the collect process for selecting/deselecting protocol files.
     *
     * @param fileName the file name
     * @param selected the file selection flag
     */
    public void toggleSelection(String fileName, boolean selected) { // TODO: toggle should make 2nd parameter useless
        AbstractRunner fileSelection = new FileRunner(new HashMap<>(selections), fileName, selected);
        fileSelection.addObserver(this);
        submitToThreadPool(fileSelection);
    }

    /**
     * Returns the number of files selected by the user.
     *
     * @return the number of selections files
     */
    private int getNumOfSelectedFiles() {
        int numChecked = 0;
        for (ProtocolFile each : files) {
            if (selections.get(each.getName())) {
                numChecked++;
            }
        }
        return numChecked;
    }

    /**
     * Returns whether a protocol file located at the given list index is selections by the user.
     *
     * @param file the file name of the protocol file
     * @return true if the file is selections, false otherwise or if the file specified by the given name does not
     * exists
     */
    public boolean isSelected(String file) {
        for (ProtocolFile each : files) {
            if (each.getName().equals(file)) {
                return selections.get(file);
            }
        }
        return false;
    }

    /**
     * Returns whether the directory for collecting protocol files is valid.
     *
     * @return true, if the directory is valid
     */
    public boolean isDirectoryValid() {
        return isDirectoryValid;
    }

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
                // noinspection InstanceofInterfaces
                //noinspection ChainOfInstanceofChecks
                if (o instanceof DirectoryRunner) {
                    updateDirectory((DirectoryRunner) o);
                    spreadUpdate(State.IDLE);
                    break;
                }
                //noinspection InstanceofInterfaces
                if (o instanceof FileRunner) {
                    updateFile((FileRunner) o);
                    spreadUpdate(State.IDLE);
                }
                break;
        }
    }

    /**
     * Updates this process from the directory runner.
     *
     * @param runner the directory runner
     */

    private void updateDirectory(DirectoryRunner runner) {
        isDirectoryValid = runner.isDirectoryValid();
        files = new ArrayList<>(runner.getProtocolFiles());
        for (ProtocolFile each : files) {
            selections.put(each.getName(), false);
        }
        Model.INSTANCE.getLogger().info(files.size() + " files detected");
    }

    /**
     * Updates this process from the file runner.
     *
     * @param runner the file runner
     */
    private void updateFile(FileRunner runner) {
        selections.putAll(runner.getFileSelections());
        if (getNumOfSelectedFiles() < 2) {
            Model.INSTANCE.getLogger().warning("At least 2 files must be selected");
        } else {
            Model.INSTANCE.getLogger().info(getNumOfSelectedFiles() + " files selected");
        }
    }

}
