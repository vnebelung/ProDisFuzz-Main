/**
 * This file is part of the ProDisFuzz program.
 * (c) by Volker Nebelung, 2012
 */
package model.process;

import model.ProtocolPart;
import model.runnable.LoadXMLR;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Observable;

/**
 * The Class LoadXMLP encapsulates the process of loading and parsing a XML
 * file.
 *
 * @author Volker Nebelung
 * @version 1.0
 */
public class LoadXMLP extends AbstractThreadP {

    /**
     * The list of protocol parts.
     */
    private List<ProtocolPart> parts;

    /**
     * The XML file path.
     */
    private Path filePath;

    /**
     * Instantiates a new XML loading process.
     */
    public LoadXMLP() {
        super();
        parts = new ArrayList<ProtocolPart>();
        filePath = null; // NOPMD
    }

    /**
     * Sets the XML file.
     *
     * @param path the XML file
     */
    public void setFile(final String path) {
        final Path newPath = Paths.get(path).toAbsolutePath().normalize();
        if (!newPath.equals(filePath)) {
            if (Files.isRegularFile(newPath) && Files.isReadable(newPath)) {
                filePath = newPath;
            } else {
                filePath = null; // NOPMD
            }
            spreadUpdate(false);
        }
    }

    /**
     * Resets all variables to the default value and notifies all observers.
     */
    public void reset() {
        filePath = null; // NOPMD
        parts.clear();
        super.reset();
    }

    /**
     * Returns the loaded protocol parts.
     *
     * @return parts the protocol parts
     */
    public List<ProtocolPart> getParts() {
        return parts;
    }

    /**
     * Gets the XLM file path.
     *
     * @return the XML file path. Can be null if it has not been set yet
     */
    public Path getFilePath() {
        return filePath;
    }

    /**
     * Starts the thread with the load XML runnable.
     */
    public void start() {
        super.start(new LoadXMLR(filePath));
    }

    /*
     * (non-Javadoc)
     *
     * @see java.util.Observer#update(java.util.Observable, java.lang.Object)
     */
    @Override
    public void update(final Observable observable, final Object arg) {
        final LoadXMLR data = (LoadXMLR) observable;
        parts = data.getParts();
        super.update(observable, arg);
    }

}