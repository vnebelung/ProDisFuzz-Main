/*
 * This file is part of ProDisFuzz, modified on 03.10.13 22:24.
 * Copyright (c) 2013 Volker Nebelung <vnebelung@prodisfuzz.net>
 * This work is free. You can redistribute it and/or modify it under the
 * terms of the Do What The Fuck You Want To Public License, Version 2,
 * as published by Sam Hocevar. See the COPYING file for more details.
 */

package model;

import model.connector.AbstractConnector;
import model.logger.ExceptionHandler;
import model.logger.Logger;
import model.modificator.AbstractModificator;
import model.process.*;

public enum Model {

    INSTANCE;
    private final CollectProcess collectProcess;
    private final LearnProcess learnProcess;
    private final ExportProcess exportProcess;
    private final ImportProcess importProcess;
    private final FuzzOptionsProcess fuzzOptionsProcess;
    private final FuzzingProcess fuzzingProcess;
    private final ReportProcess reportProcess;
    private final Logger logger;
    private AbstractConnector connector;
    private AbstractModificator modificator;

    /**
     * Instantiates a new singelton model.
     */
    private Model() {
        collectProcess = new CollectProcess();
        learnProcess = new LearnProcess();
        exportProcess = new ExportProcess();
        importProcess = new ImportProcess();
        fuzzOptionsProcess = new FuzzOptionsProcess();
        fuzzingProcess = new FuzzingProcess();
        reportProcess = new ReportProcess();
        logger = new Logger();
        Thread.setDefaultUncaughtExceptionHandler(new ExceptionHandler());
    }

    /**
     * Returns the current memory usage of ProDisFuzz.
     *
     * @return the current memory usage in bytes
     */
    public long getMemoryUsage() {
        return Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();
    }

    /**
     * Resets all variables and options to the default by calling the reset
     * methods of all process classes.
     */
    public void reset() {
        Model.INSTANCE.getLogger().reset();
        collectProcess.reset();
        learnProcess.reset();
        exportProcess.reset();
        importProcess.reset();
        fuzzOptionsProcess.reset();
        fuzzingProcess.reset();
    }

    /**
     * Gets the collect process, responsible for collecting all communication files.
     *
     * @return the collect process
     */
    public CollectProcess getCollectProcess() {
        return collectProcess;
    }

    /**
     * Gets the learn process, responsible for learning the protocol structure.
     *
     * @return the learn process
     */
    public LearnProcess getLearnProcess() {
        return learnProcess;
    }

    /**
     * Gets the export process, responsible for exporting the learned protocol structure into a XML format.
     *
     * @return the export process
     */
    public ExportProcess getExportProcess() {
        return exportProcess;
    }

    /**
     * Gets the import process, responsible for importing the learned protocol structure from a XML format.
     *
     * @return the import process
     */
    public ImportProcess getImportProcess() {
        return importProcess;
    }

    /**
     * Gets the fuzz options process, responsible for setting all relevant fuzzing options.
     *
     * @return the fuzz options process
     */
    public FuzzOptionsProcess getFuzzOptionsProcess() {
        return fuzzOptionsProcess;
    }

    /**
     * Gets the fuzzing process, responsible for executing the fuzz testing.
     *
     * @return the fuzz options process
     */
    public FuzzingProcess getFuzzingProcess() {
        return fuzzingProcess;
    }

    /**
     * Gets the fuzreportzing process, responsible for generating the final report.
     *
     * @return the report process
     */
    public ReportProcess getReportProcess() {
        return reportProcess;
    }

    /**
     * Gets the logging mechanism.
     *
     * @return the logger
     */
    public Logger getLogger() {
        return logger;
    }
}
