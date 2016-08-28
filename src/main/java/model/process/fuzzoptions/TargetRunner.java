/*
 * This file is part of ProDisFuzz, modified on 28.08.16 20:30.
 * Copyright (c) 2013-2016 Volker Nebelung <vnebelung@prodisfuzz.net>
 * This work is free. You can redistribute it and/or modify it under the
 * terms of the Do What The Fuck You Want To Public License, Version 2,
 * as published by Sam Hocevar. See the COPYING file for more details.
 */

package model.process.fuzzoptions;

import model.Model;
import model.process.AbstractRunner;

import java.net.InetSocketAddress;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

/**
 * This class is the target runnable, responsible for setting a fuzzing target.
 */
class TargetRunner extends AbstractRunner {

    private InetSocketAddress target;
    private String address;
    private int port;
    private int timeout;

    /**
     * Constructs a new target runner.
     *
     * @param address the target's address
     * @param port    the target's port
     * @param timeout the target's timeout in milliseconds
     */
    protected TargetRunner(String address, int port, int timeout) {
        super(1);
        this.address = address;
        this.port = port;
        this.timeout = timeout;
    }

    @Override
    public void run() {
        try {
            markStart();

            // Start work unit
            Callable<Boolean> targetChecker = new TargetChecker(address, port, timeout);
            Future<Boolean> futureTargetChecker = submitToThreadPool(targetChecker);
            boolean targetReachable = futureTargetChecker.get();
            if (targetReachable) {
                target = new InetSocketAddress(address, port);
                Model.INSTANCE.getLogger().fine("Target '" + address + ':' + port + "' is reachable");
            } else {
                target = null;
                Model.INSTANCE.getLogger().error("Target '" + address + ':' + port + "' is not reachable");
            }
            markFinish();
        } catch (InterruptedException ignored) {
            Model.INSTANCE.getLogger().info("Verifying target interrupted");
            markCancel();
        } catch (ExecutionException e) {
            // Should not happen
            Model.INSTANCE.getLogger().error(e);
        }
    }

    /**
     * Returns the fuzzing target.
     *
     * @return the target or null, if the target is invalid
     */
    public InetSocketAddress getTarget() {
        return target;
    }

}
