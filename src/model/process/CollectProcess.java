/*
 * This file is part of ProDisFuzz, modified on 11.10.13 22:43.
 * Copyright (c) 2013 Volker Nebelung <vnebelung@prodisfuzz.net>
 * This work is free. You can redistribute it and/or modify it under the
 * terms of the Do What The Fuck You Want To Public License, Version 2,
 * as published by Sam Hocevar. See the COPYING file for more details.
 */

package model.process;

import model.Model;
import model.ProtocolFile;

import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

public class CollectProcess extends AbstractProcess {

    private final List<ProtocolFile> files;
    private final Map<ProtocolFile, Boolean> selected;
    private Path directory;


    /**
     * Instantiates a new process responsible for collecting all files which contain protocol information.
     */
    public CollectProcess() {
        super();
        files = new ArrayList<>();
        directory = null;
        selected = new HashMap<>();
    }

    /**
     * Sets all detected files in the already defined directory. Note: The method does not notify observers about a
     * change of the files.
     */
    private void setFiles() {
        files.clear();
        selected.clear();
        try (DirectoryStream<Path> stream = Files.newDirectoryStream(directory)) {
            int count = 0;
            for (final Path each : stream) {
                // Sort out any files that are not an actual file, are not readable or are hidden
                if (Files.isRegularFile(each) && Files.isReadable(each) && !Files.isHidden(each)) {
                    final ProtocolFile file = new ProtocolFile(each);
                    files.add(file);
                    selected.put(file, true);
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
     * Sets the directory for collecting all files.
     *
     * @param s the directory path
     */
    public void setDirectory(final String s) {
        final Path newPath = Paths.get(s).toAbsolutePath().normalize();
        if (!newPath.equals(directory)) {
            if (Files.isDirectory(newPath) && !s.isEmpty()) {
                directory = newPath;
                Model.INSTANCE.getLogger().info("Directory for collecting set to '" + directory.toString() + "'");
                setFiles();
            } else if (directory != null) {
                directory = null;
                files.clear();
                Model.INSTANCE.getLogger().error("Directory for collecting invalid");
            }
            spreadUpdate();
        }
    }

    /**
     * Returns the files of the current directory.
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
        final List<ProtocolFile> selection = new ArrayList<>();
        for (final ProtocolFile each : files) {
            if (selected.get(each)) {
                selection.add(each);
            }
        }
        return Collections.unmodifiableList(selection);
    }

    @Override
    public void reset() {
        directory = null;
        files.clear();
        selected.clear();
        spreadUpdate();
    }

    /**
     * Sets the status of a protocol file to the given value.
     *
     * @param index  the index of the protocol file
     * @param select true if the file at the given index is selected
     */
    public void setSelected(final int index, final boolean select) {
        if (index < files.size() && select != selected.get(files.get(index))) {
            selected.put(files.get(index), select);
            spreadUpdate();
            if (getNumOfSelectedFiles() < 2) {
                Model.INSTANCE.getLogger().warning("At least 2 files must be selected");
            } else {
                Model.INSTANCE.getLogger().info(getNumOfSelectedFiles() + " files selected");
            }
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
            if (selected.get(each)) {
                numChecked++;
            }
        }
        return numChecked;
    }

    /**
     * Returns whether a protocol file located at the given list index is selected by the user.
     *
     * @param index the index of the protocol file
     * @return true if the file is selected
     */
    public Boolean isSelected(final int index) {
        return selected.get(files.get(index));
    }

}
