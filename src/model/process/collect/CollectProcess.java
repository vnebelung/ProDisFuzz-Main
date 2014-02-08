/*
 * This file is part of ProDisFuzz, modified on 07.02.14 00:21.
 * Copyright (c) 2013-2014 Volker Nebelung <vnebelung@prodisfuzz.net>
 * This work is free. You can redistribute it and/or modify it under the
 * terms of the Do What The Fuck You Want To Public License, Version 2,
 * as published by Sam Hocevar. See the COPYING file for more details.
 */

package model.process.collect;

import model.Model;
import model.ProtocolFile;
import model.process.AbstractProcess;

import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

public class CollectProcess extends AbstractProcess {

    private final List<ProtocolFile> files;
    private final Map<String, Boolean> selected;
    private Path folder;

    /**
     * Instantiates a new process responsible for collecting all files which contain protocol information.
     */
    public CollectProcess() {
        super();
        files = new ArrayList<>();
        folder = null;
        selected = new HashMap<>();
    }

    /**
     * Sets all detected files in the already defined folder. Note: The method does not notify observers about a
     * change of the files.
     */
    private void setFiles() {
        files.clear();
        selected.clear();
        try (DirectoryStream<Path> stream = Files.newDirectoryStream(folder)) {
            int count = 0;
            for (Path each : stream) {
                // Sort out any files that are not an actual file, are not readable or are hidden
                if (Files.isRegularFile(each) && Files.isReadable(each) && !Files.isHidden(each)) {
                    ProtocolFile file = new ProtocolFile(each);
                    files.add(file);
                    selected.put(file.getName(), true);
                    count++;
                }
            }
            Model.INSTANCE.getLogger().info(count + " files detected");
            Collections.sort(files);
        } catch (IOException e) {
            Model.INSTANCE.getLogger().error(e);
        }
    }

    /**
     * Sets the folder for collecting all files.
     *
     * @param s the folder path
     */
    public void setFolder(String s) {
        Path newPath = Paths.get(s).toAbsolutePath().normalize();
        if (!newPath.equals(folder)) {
            if (Files.isDirectory(newPath) && !s.isEmpty()) {
                folder = newPath;
                Model.INSTANCE.getLogger().info("Directory for collecting set to '" + folder.toString() + "'");
                setFiles();
            } else if (folder != null) {
                folder = null;
                files.clear();
                Model.INSTANCE.getLogger().error("Directory for collecting invalid");
            }
            spreadUpdate();
        }
    }

    /**
     * Returns the files of the current folder.
     *
     * @return the collected files
     */
    public List<ProtocolFile> getFiles() {
        return Collections.unmodifiableList(files);
    }

    /**
     * Returns all files that are marked as selected by the user.
     *
     * @return the selected files
     */
    public List<ProtocolFile> getSelectedFiles() {
        List<ProtocolFile> selection = new ArrayList<>();
        for (ProtocolFile each : files) {
            if (selected.get(each.getName())) {
                selection.add(each);
            }
        }
        return Collections.unmodifiableList(selection);
    }

    @Override
    public void reset() {
        folder = null;
        files.clear();
        selected.clear();
        spreadUpdate();
    }

    /**
     * Sets the select status of a protocol file to the given value. If true, the file will be used in the further
     * steps of protocol learning.
     *
     * @param s the file name of the protocol file
     * @param b true if the file with the given name is selected
     */
    public void setSelected(String s, boolean b) {
        if (!isFileNameInList(s) || b == selected.get(s)) {
            return;
        }
        selected.put(s, b);
        spreadUpdate();
        if (getNumOfSelectedFiles() < 2) {
            Model.INSTANCE.getLogger().warning("At least 2 files must be selected");
        } else {
            Model.INSTANCE.getLogger().info(getNumOfSelectedFiles() + " files selected");
        }
    }

    /**
     * Returns the number of files selected by the user.
     *
     * @return the number of selected files
     */
    public int getNumOfSelectedFiles() {
        int numChecked = 0;
        for (ProtocolFile each : files) {
            if (selected.get(each.getName())) {
                numChecked++;
            }
        }
        return numChecked;
    }

    /**
     * Returns whether a protocol file located at the given list index is selected by the user.
     *
     * @param s the file name of the protocol file
     * @return true if the file is selected, false otherwise or if the file specified by the given name does not exists
     */
    public Boolean isSelected(String s) {
        for (ProtocolFile each : files) {
            if (each.getName().equals(s)) {
                return selected.get(s);
            }
        }
        return false;
    }

    /**
     * Returns whether the folder for collecting protocol samples is valid.
     *
     * @return true, if the collect folder is valid
     */
    public boolean isFolderValid() {
        return folder != null;
    }

    /**
     * Returns whether the file list contains an element with the given file name
     *
     * @param s the file name to find
     * @return true, if the file list contains a file with the given name
     */
    private boolean isFileNameInList(String s) {
        for (ProtocolFile each : files) {
            if (each.getName().equals(s)) {
                return true;
            }
        }
        return false;
    }

}
