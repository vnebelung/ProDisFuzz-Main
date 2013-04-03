/**
 * This file is part of the ProDisFuzz program.
 * (c) by Volker Nebelung, 2012
 */
package model;

import model.process.*;

/**
 * The Class Model implements the model of the MVC pattern.
 *
 * @author Volker Nebelung
 * @version 1.0
 */
public class Model {

    /**
     * The collect process.
     */
    private final CollectP collectProcess;

    /**
     * The check process.
     */
    private final CheckP checkProcess;

    /**
     * The learn process.
     */
    private final LearnP learnProcess;

    /**
     * The XML generation process.
     */
    private final XMLGenP xmlGenProcess;

    /**
     * The XML loading process.
     */
    private final LoadXMLP loadXmlProcess;

    /**
     * The fuzzing options process.
     */
    private final OptionsP optionsProcess;

    /**
     * The fuzzing process.
     */
    private final FuzzingP fuzzingProcess;

    /**
     * The report generation process.
     */
    private final ReportGenP reportGenProcess;

    /**
     * Instantiates a new model.
     */
    public Model() {
        collectProcess = new CollectP();
        checkProcess = new CheckP();
        learnProcess = new LearnP();
        xmlGenProcess = new XMLGenP();
        loadXmlProcess = new LoadXMLP();
        optionsProcess = new OptionsP();
        fuzzingProcess = new FuzzingP();
        reportGenProcess = new ReportGenP();
    }

    /**
     * Resets all variables and options to the default by calling the reset
     * methods of all process classes.
     */
    public void reset() {
        collectProcess.reset();
        checkProcess.reset();
        learnProcess.reset();
        xmlGenProcess.reset();
        loadXmlProcess.reset();
        optionsProcess.reset();
        fuzzingProcess.reset();
        reportGenProcess.reset();
    }

    /**
     * Gets the collect process.
     *
     * @return the collect process
     */
    public CollectP getCollectProcess() {
        return collectProcess;
    }

    /**
     * Gets the check process.
     *
     * @return the collect process
     */
    public CheckP getCheckProcess() {
        return checkProcess;
    }

    /**
     * Gets the learn process.
     *
     * @return the learn process
     */
    public LearnP getLearnProcess() {
        return learnProcess;
    }

    /**
     * Gets the XML generation process.
     *
     * @return the XML generation process
     */
    public XMLGenP getXmlGenProcess() {
        return xmlGenProcess;
    }

    /**
     * Gets the XML loading process.
     *
     * @return the XML loading process
     */
    public LoadXMLP getLoadXmlProcess() {
        return loadXmlProcess;
    }

    /**
     * Gets the fuzzing options process.
     *
     * @return the fuzzing options process
     */
    public OptionsP getOptionsProcess() {
        return optionsProcess;
    }

    /**
     * Gets the fuzzing process.
     *
     * @return the fuzzing process
     */
    public FuzzingP getFuzzingProcess() {
        return fuzzingProcess;
    }

    /**
     * Gets the report generation process.
     *
     * @return the report generation process
     */
    public ReportGenP getReportGenProcess() {
        return reportGenProcess;
    }

    /**
     * Updates all observers of the model by calling the spreadUpdate methods of
     * all process classes.
     */
    public void spreadUpdate() {
        collectProcess.spreadUpdate(true);
        checkProcess.spreadUpdate(true);
        learnProcess.spreadUpdate(true);
        xmlGenProcess.spreadUpdate(true);
        loadXmlProcess.spreadUpdate(true);
        optionsProcess.spreadUpdate(true);
        fuzzingProcess.spreadUpdate(true);
        reportGenProcess.spreadUpdate(true);
    }

    /**
     * Returns the current memory usage of ProDisFuzz in bytes.
     *
     * @return the current memory usage in bytes
     */
    public static long getMemoryUsage() {
        return (Runtime.getRuntime().totalMemory() - Runtime.getRuntime()
                .freeMemory());
    }
}
