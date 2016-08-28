/*
 * This file is part of ProDisFuzz, modified on 28.08.16 20:30.
 * Copyright (c) 2013-2016 Volker Nebelung <vnebelung@prodisfuzz.net>
 * This work is free. You can redistribute it and/or modify it under the
 * terms of the Do What The Fuck You Want To Public License, Version 2,
 * as published by Sam Hocevar. See the COPYING file for more details.
 */

package model.process.import_;

import model.Model;
import model.process.AbstractRunner;
import model.protocol.ProtocolStructure;
import model.util.XmlExchange;
import model.util.XmlSchema;
import nu.xom.Document;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

/**
 * This class is the import runnable, responsible for importing the protocol structure from a file.
 */
class Runner extends AbstractRunner {

    private ProtocolStructure protocolStructure;
    private Path path;

    /**
     * Constructs an export runnable.
     *
     * @param path the path to the file that will be imported
     */
    public Runner(Path path) {
        super(1);
        this.path = path;
    }

    @Override
    public void run() {
        try {
            markStart();

            protocolStructure = null;

            Path file = path.toAbsolutePath().normalize();
            if (!Files.isRegularFile(file)) {
                Model.INSTANCE.getLogger().error("File '" + file + "' is not a regular file");
                markCancel();
                return;
            }
            if (!Files.isReadable(file)) {
                Model.INSTANCE.getLogger().error("File '" + file + "' is not readable");
                markCancel();
                return;
            }
            if (!XmlSchema.validateProtocol(file)) {
                markCancel();
                return;
            }
            Document document = XmlExchange.load(file);
            if (document == null) {
                markCancel();
                return;
            }

            // Start new workunit
            //noinspection TypeMayBeWeakened
            ProtocolStructureBuilder protocolStructureBuilder = new ProtocolStructureBuilder(document);
            Future<ProtocolStructure> protocolStructureBuilderFuture = submitToThreadPool(protocolStructureBuilder);
            protocolStructure = protocolStructureBuilderFuture.get();
            Model.INSTANCE.getLogger().info("Protocol structure successfully imported");
            markFinish();
        } catch (InterruptedException ignored) {
            Model.INSTANCE.getLogger().info("Importing protocol structure cancelled");
            markCancel();
        } catch (ExecutionException e) {
            // Should not happen
            Model.INSTANCE.getLogger().error(e);
        }
    }

    /**
     * Returns the imported protocol structure.
     *
     * @return the protocol structure
     */
    public ProtocolStructure getProtocolStructure() {
        return protocolStructure;
    }
}
