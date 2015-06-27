/*
 * This file is part of ProDisFuzz, modified on 28.06.15 01:23.
 * Copyright (c) 2013-2015 Volker Nebelung <vnebelung@prodisfuzz.net>
 * This work is free. You can redistribute it and/or modify it under the
 * terms of the Do What The Fuck You Want To Public License, Version 2,
 * as published by Sam Hocevar. See the COPYING file for more details.
 */

package model;

import model.logger.ExceptionHandler;
import model.logger.Logger;
import model.process.collect.CollectProcess;
import model.process.export.ExportProcess;
import model.process.fuzzOptions.FuzzOptionsProcess;
import model.process.fuzzing.FuzzingProcess;
import model.process.import_.ImportProcess;
import model.process.learn.LearnProcess;
import model.process.monitor.MonitorProcess;
import model.process.report.ReportProcess;
import model.updater.UpdateCheck;

public enum Model {

    INSTANCE;
    private final CollectProcess collectProcess;
    private final LearnProcess learnProcess;
    private final ExportProcess exportProcess;
    private final ImportProcess importProcess;
    private final MonitorProcess monitorProcess;
    private final FuzzOptionsProcess fuzzOptionsProcess;
    private final FuzzingProcess fuzzingProcess;
    private final ReportProcess reportProcess;
    private final Logger logger;
    private final UpdateCheck updateCheck;

    /**
     * Instantiates a new singleton model responsible for managing the data and logic.
     */
    @SuppressWarnings("OverlyCoupledMethod")
    Model() {
        collectProcess = new CollectProcess();
        learnProcess = new LearnProcess();
        exportProcess = new ExportProcess();
        importProcess = new ImportProcess();
        monitorProcess = new MonitorProcess();
        fuzzOptionsProcess = new FuzzOptionsProcess();
        fuzzingProcess = new FuzzingProcess();
        reportProcess = new ReportProcess();
        logger = new Logger();
        updateCheck = new UpdateCheck();
        Thread.setDefaultUncaughtExceptionHandler(new ExceptionHandler());
    }

    /**
     * Resets all variables and options to the default by calling the reset methods of all process classes.
     */
    public void reset() {
        logger.reset();
        collectProcess.reset();
        learnProcess.reset();
        exportProcess.reset();
        importProcess.reset();
        monitorProcess.reset();
        fuzzOptionsProcess.reset();
        fuzzingProcess.reset();
        reportProcess.reset();
    }

    /**
     * Returns the collect process that is responsible for collecting all communication files.
     *
     * @return the collect process
     */
    public CollectProcess getCollectProcess() {
        return collectProcess;
    }

    /**
     * Returns the learn process that is responsible for learning the protocol structure.
     *
     * @return the learn process
     */
    public LearnProcess getLearnProcess() {
        return learnProcess;
    }

    /**
     * Returns the export process that is responsible for exporting the learned protocol structure into an XML format.
     *
     * @return the export process
     */
    public ExportProcess getExportProcess() {
        return exportProcess;
    }

    /**
     * Returns the import process that is responsible for importing the learned protocol structure from an XML format.
     *
     * @return the import process
     */
    public ImportProcess getImportProcess() {
        return importProcess;
    }

    /**
     * Returns the fuzz options process that is responsible for setting all relevant fuzzing options.
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
     * Returns the report process that is responsible for generating the final report.
     *
     * @return the report process
     */
    public ReportProcess getReportProcess() {
        return reportProcess;
    }

    /**
     * Returns the monitor process that is responsible for connecting to the monitor.
     *
     * @return the report process
     */
    public MonitorProcess getMonitorProcess() {
        return monitorProcess;
    }

    /**
     * Returns the logging mechanism that is responsible for collecting all kind of internal messages.
     *
     * @return the logger
     */
    public Logger getLogger() {
        return logger;
    }

    /**
     * Returns the update check that is responsible for checking for new releases of ProDisFuzz at prodisfuzz.net.
     *
     * @return the update check
     */
    public UpdateCheck getUpdateCheck() {
        return updateCheck;
    }
}
