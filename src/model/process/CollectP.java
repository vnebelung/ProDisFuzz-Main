/**
 * This file is part of the ProDisFuzz program.
 * (c) by Volker Nebelung, 2012
 */
package model.process;

import model.runnable.CollectR;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashSet;
import java.util.Observable;
import java.util.Set;

/**
 * The Class CollectP encapsulates the process of collecting all files in a
 * specific directory and storing them for later usage.
 *
 * @author Volker Nebelung
 * @version 1.0
 */
public class CollectP extends AbstractThreadP {

    /**
     * The file paths.
     */
    private Set<Path> filePaths;

    /**
     * The collect directory path.
     */
    private Path directoryPath;

    /**
     * Instantiates a new collect process.
     */
    public CollectP() {
        super();
        filePaths = new HashSet<Path>();
        directoryPath = null; // NOPMD
    }

    /**
     * Sets the directory path for collecting files.
     *
     * @param path the new directory path
     */
    public void setDirectory(final String path) {
        final Path newPath = Paths.get(path).toAbsolutePath().normalize();
        if (!newPath.equals(directoryPath)) {
            if (Files.isDirectory(newPath) && !path.isEmpty()) {
                directoryPath = newPath;
            } else {
                directoryPath = null; // NOPMD
            }
            spreadUpdate(false);
        }
    }

    /**
     * Gets the collect directory path.
     *
     * @return the directory path
     */
    public Path getDirectoryPath() {
        return directoryPath;
    }

    /**
     * Gets the paths of the collected files
     *
     * @return the paths of the collected files
     */
    public Set<Path> getFilePaths() {
        return filePaths;
    }

    /*
     * (non-Javadoc)
     *
     * @see model.process.AbstractThreadProcess#reset()
     */
    @Override
    public void reset() {
        directoryPath = null; // NOPMD
        filePaths.clear();
        super.reset();
    }

    /*
     * (non-Javadoc)
     *
     * @see java.util.Observer#update(java.util.Observable, java.lang.Object)
     */
    @Override
    public void update(final Observable observable, final Object arg) {
        final CollectR data = (CollectR) observable;
        filePaths = data.getFilePaths();
        super.update(observable, arg);
    }

    /**
     * Starts the thread with the collect runnable.
     */
    public void start() {
        super.start(new CollectR(directoryPath));
    }

}