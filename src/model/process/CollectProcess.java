/*
 * This file is part of ProDisFuzz, modified on 03.10.13 22:24.
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
    private Path directory;
    private final Map<ProtocolFile, Boolean> selected;


    /**
     * Instantiates a new collect process.
     */
    public CollectProcess() {
        super();
        files = new ArrayList<>();
        directory = null;
        selected = new HashMap<>();
    }

    /**
     * Sets all detected files in the already defined directory. Note: The method does not notify
     * observers about a change of the files.
     */
    private void setFiles() {
        files.clear();
        selected.clear();
        try (DirectoryStream<Path> stream = Files.newDirectoryStream(directory)) {
            int count = 0;
            for (final Path path : stream) {
                // Sort out any files that are not an actual file, are not readable or are hidden
                if (Files.isRegularFile(path) && Files.isReadable(path) && !Files.isHidden(path)) {
                    final ProtocolFile file = new ProtocolFile(path);
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
     * Sets the directory for collecting filePaths.
     *
     * @param path the new directory path
     */
    public void setDirectory(final String path) {
        final Path newPath = Paths.get(path).toAbsolutePath().normalize();
        if (!newPath.equals(directory)) {
            if (Files.isDirectory(newPath) && !path.isEmpty()) {
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
     * Gets the collected files.
     *
     * @return the collected files
     */
    public List<ProtocolFile> getFiles() {
        return Collections.unmodifiableList(files);
    }

    /**
     * Gets selected files only.
     *
     * @return selected files
     */
    public List<ProtocolFile> getSelectedFiles() {
        final List<ProtocolFile> selection = new ArrayList<>();
        for (final ProtocolFile file : files) {
            if (selected.get(file)) {
                selection.add(file);
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
     * Sets the status of a protocol file to selected.
     *
     * @param index the index of the protocol file
     */
    public void setSelected(final int index) {
        if (index < files.size() && !selected.get(files.get(index))) {
            selected.put(files.get(index), true);
            spreadUpdate();
            if (getNumOfSelectedFiles() < 2) {
                Model.INSTANCE.getLogger().warning("At least 2 files must be selected");
            } else {
                Model.INSTANCE.getLogger().info(getNumOfSelectedFiles() + " files selected");
            }
        }
    }

    /**
     * Sets the status of a protocol file to unselected.
     *
     * @param index the index of the protocol file
     */
    public void setUnselected(final int index) {
        if (index < files.size() && selected.get(files.get(index))) {
            selected.put(files.get(index), false);
            spreadUpdate();
            if (getNumOfSelectedFiles() < 2) {
                Model.INSTANCE.getLogger().warning("At least 2 files must be selected");
            } else {
                Model.INSTANCE.getLogger().info(getNumOfSelectedFiles() + " files selected");
            }
        }
    }

    /**
     * Gets the umber of selected files.
     *
     * @return the number of selected files
     */
    public int getNumOfSelectedFiles() {
        int numChecked = 0;
        for (int i = 0; i < files.size(); i++) {
            if (isSelected(i)) {
                numChecked++;
            }
        }
        return numChecked;
    }

    /**
     * Returns whether a protocol file is selected.
     *
     * @param index the index of the protocol file
     * @return true if the file is selected
     */
    public Boolean isSelected(final int index) {
        return selected.get(files.get(index));
    }

}
