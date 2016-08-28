/*
 * This file is part of ProDisFuzz, modified on 28.08.16 19:39.
 * Copyright (c) 2013-2016 Volker Nebelung <vnebelung@prodisfuzz.net>
 * This work is free. You can redistribute it and/or modify it under the
 * terms of the Do What The Fuck You Want To Public License, Version 2,
 * as published by Sam Hocevar. See the COPYING file for more details.
 */

package model.process.export;

import model.Model;
import model.process.AbstractRunner;
import model.protocol.ProtocolStructure;
import model.util.XmlExchange;
import nu.xom.Document;

import java.nio.file.Path;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

/**
 * This class is the export runnable, responsible for exporting the protocol structure to a file.
 */
class Runner extends AbstractRunner {

    private ProtocolStructure protocolStructure;
    private Path path;

    /**
     * Constructs an export runnable.
     *
     * @param protocolStructure the protocol structure
     * @param path              the path the protocol structure will be exported to
     */
    public Runner(Path path, ProtocolStructure protocolStructure) {
        super(2);
        this.protocolStructure = protocolStructure;
        this.path = path;
    }

    @Override
    public void run() {
        try {
            markStart();

            // Start new workunit
            //noinspection TypeMayBeWeakened
            XmlBuilder xmlBuilder = new XmlBuilder(protocolStructure);
            Future<Document> xmlBuilderFuture = submitToThreadPool(xmlBuilder);
            Document document = xmlBuilderFuture.get();
            markProgress();

            boolean exported = XmlExchange.save(document, path);
            if (!exported) {
                markCancel();
                return;
            }
            Model.INSTANCE.getLogger().info("Protocol structure successfully exported");
            markFinish();
        } catch (InterruptedException ignored) {
            Model.INSTANCE.getLogger().info("Exporting protocol structure cancelled");
            markCancel();
        } catch (ExecutionException e) {
            // Should not happen
            Model.INSTANCE.getLogger().error(e);
        }
    }
}
