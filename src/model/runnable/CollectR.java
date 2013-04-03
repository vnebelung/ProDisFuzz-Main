/**
 * This file is part of the ProDisFuzz program.
 * (c) by Volker Nebelung, 2012
 */
package model.runnable;

import model.RunnableThread.RunnableState;
import model.runnable.component.CollectFilesC;

import java.io.IOException;
import java.nio.file.*;
import java.util.HashSet;
import java.util.Set;

/**
 * The Class CollectR implements the runnable which is responsible for reading
 * all protocol files in a directory and store them.
 *
 * @author Volker Nebelung
 * @version 1.0
 */
public class CollectR extends AbstractR {

    /**
     * The paths of the current files.
     */
    private Set<Path> filePaths;

    /**
     * The collect files component.
     */
    final private CollectFilesC collectFilesC;

    /**
     * The directory path that will be monitored.
     */
    final private Path directoryPath;

    /**
     * Instantiates a new collect runnable.
     *
     * @param directoryPath the directory path to collect the files
     */
    public CollectR(final Path directoryPath) {
        super();
        filePaths = new HashSet<Path>();
        this.directoryPath = directoryPath;
        collectFilesC = new CollectFilesC(this, directoryPath);
    }

    /*
     * (non-Javadoc)
     *
     * @see java.lang.Runnable#run()
     */
    @Override
    public void run() {
        spreadUpdate(RunnableState.RUNNING);
        // Determine the file list
        WatchService watcher;
        try {
            watcher = FileSystems.getDefault().newWatchService();
            WatchKey watchKey = directoryPath.register(watcher,
                    StandardWatchEventKinds.ENTRY_CREATE,
                    StandardWatchEventKinds.ENTRY_DELETE);
            try {
                while (watchKey.reset()) {
                    filePaths = collectFilesC.getFilePaths();
                    spreadUpdate(RunnableState.RUNNING);
                    watchKey = watcher.take();
                    watchKey.pollEvents();
                }
            } catch (InterruptedException e) {
                // Interrupted by the parent runnable
            } finally {
                watchKey.cancel();
                watcher.close();
            }
        } catch (IOException e) { // NOPMD
            // Should not happen
        }
        spreadUpdate(RunnableState.FINISHED);
    }

    /**
     * Gets the paths of the current files.
     *
     * @return the paths of the current files
     */
    public Set<Path> getFilePaths() {
        return filePaths;
    }

    /*
     * (non-Javadoc)
     *
     * @see model.runnable.AbstractRunnable#setTotalProgress()
     */
    @Override
    protected void setTotalProgress() {
        totalProgress = -1;
    }

}