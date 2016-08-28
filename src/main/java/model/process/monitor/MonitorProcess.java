/*
 * This file is part of ProDisFuzz, modified on 28.08.16 20:30.
 * Copyright (c) 2013-2016 Volker Nebelung <vnebelung@prodisfuzz.net>
 * This work is free. You can redistribute it and/or modify it under the
 * terms of the Do What The Fuck You Want To Public License, Version 2,
 * as published by Sam Hocevar. See the COPYING file for more details.
 */

package model.process.monitor;

import java.util.Observable;

/**
 * This class is the monitor process, responsible for connecting to monitor.
 */
public class MonitorProcess extends Observable {
    //
    //    private final Monitor monitor;
    //    private boolean monitorReachable;
    //
    //    /**
    //     * Constructs a new monitor process.
    //     */
    //    public MonitorProcess() {
    //        super();
    //        monitor = new Monitor();
    //        monitorReachable = false;
    //    }
    //
    //    @Override
    //    public void reset() {
    //        monitor.disconnect();
    //        monitorReachable = false;
    //        monitor.setAddress("", 0);
    //        spreadUpdate();
    //    }
    //
    //    /**
    //     * Sets the monitor's address and port.
    //     *
    //     * @param address the target's address
    //     * @param port    the target's port
    //     */
    //    public void setMonitor(String address, int port) {
    //        if (monitor.getAddressName().equals(address) && (monitor.getAddressPort() == port)) {
    //            return;
    //        }
    //        monitor.setAddress(address, port);
    //        monitorReachable = monitor.connect();
    //        spreadUpdate();
    //    }
    //
    //    /**
    //     * Returns the monitor component.
    //     *
    //     * @return the monitor component
    //     */
    //    public Monitor getMonitor() {
    //        return monitor;
    //    }
    //
    //    /**
    //     * Returns whether the monitor component is reachable.
    //     *
    //     * @return true, if the monitor is responding
    //     */
    //    public boolean isMonitorReachable() {
    //        return monitorReachable;
    //    }
}
