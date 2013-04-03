/**
 * This file is part of the ProDisFuzz program.
 * (c) by Volker Nebelung, 2012
 */
package model.runnable.component;

import model.runnable.AbstractR;

import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashSet;
import java.util.Set;

/**
 * The Class CollectFilesC implements the functionality to look for files in a
 * directory.
 *
 * @author Volker Nebelung
 * @version 1.0
 */
public class CollectFilesC extends AbstractC {

    /**
     * The directory path that will be monitored.
     */
    final private Path directoryPath;

    /**
     * Instantiates a new collect files component.
     *
     * @param runnable      the parent runnable
     * @param directoryPath the directory path that will be monitored for files
     */
    public CollectFilesC(final AbstractR runnable, final Path directoryPath) {
        super(runnable);
        this.directoryPath = directoryPath;
    }

    /**
     * Gets the paths of all readable and not hidden files of the given
     * directory.
     *
     * @return filePaths the paths of all readable and not hidden files
     * @throws IOException
     */
    public Set<Path> getFilePaths() throws IOException {
        final Set<Path> filePaths = new HashSet<Path>();
        DirectoryStream<Path> directoryStream = null;
        try {
            directoryStream = Files.newDirectoryStream(directoryPath);
            for (Path path : directoryStream) {
                // Sort out any files that are not an actual file, are
                // not readable or are hidden
                if (Files.isRegularFile(path) && Files.isReadable(path)
                        && !Files.isHidden(path)) {
                    filePaths.add(path);
                }
            }
        } finally {
            if (directoryStream != null) {
                directoryStream.close();
            }
        }
        return filePaths;
    }

    /*
     * (non-Javadoc)
     *
     * @see model.runnable.component.AbstractComponent#getTotalProgress()
     */
    @Override
    public int getTotalProgress() {
        return 1;
    }

}