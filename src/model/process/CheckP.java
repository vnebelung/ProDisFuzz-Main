/**
 * This file is part of the ProDisFuzz program.
 * (c) by Volker Nebelung, 2012
 */
package model.process;

import model.ProtocolFile;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

/**
 * The Class CheckP encapsulates the process of generating all necessary
 * variables from the data collected by CollectProcess for the Class
 * LearnProcess.
 *
 * @author Volker Nebelung
 * @version 1.0
 */
public class CheckP extends AbstractP {

    /**
     * The file list.
     */
    private final List<ProtocolFile> files;

    /*
     * (non-Javadoc)
     *
     * @see model.process.AbstractProcess#reset()
     */
    public void reset() {
        files.clear();
        spreadUpdate(true);
    }

    /**
     * Fills the file list with the paths of the given files and sets all
     * properties.
     *
     * @param filePaths the file paths
     */
    public void initFiles(final Set<Path> filePaths) {
        for (Path path : filePaths) {
            files.add(new ProtocolFile(path)); // NOPMD
        }
        // Sorts the files by their custom sort method
        Collections.sort(files);
        spreadUpdate(false);
    }

    /**
     * Instantiates a new check process.
     */
    public CheckP() {
        super();
        files = new ArrayList<ProtocolFile>();
    }

    /**
     * Gets the file list.
     *
     * @return the file list
     */
    public List<ProtocolFile> getFiles() {
        return files;
    }

    /**
     * Returns the number of checked files, that means the number of files which
     * are marked by the user for usage in the learn process.
     *
     * @return the number of checked files
     */
    public int numOfCheckedFiles() {
        int num = 0;
        for (ProtocolFile file : files) {
            if (file.isChecked()) {
                num++;
            }
        }
        return num;
    }

    /**
     * Sets the checked marker of all protocol files according to the input
     * array.
     *
     * @param checked the checked states
     */
    public void setChecked(final boolean[] checked) {
        boolean hasChanged = false;
        for (int i = 0; i < checked.length; i++) {
            if (files.get(i).isChecked() != checked[i]) {
                files.get(i).setChecked(checked[i]);
                hasChanged = true;
            }
        }
        if (hasChanged) {
            spreadUpdate(false);
        }
    }
}