package model.process.monitor;

import model.connector.Monitor;
import model.process.AbstractProcess;

public class MonitorProcess extends AbstractProcess {

    private final Monitor monitor;
    private boolean monitorReachable;

    /**
     * Instantiates a new process responsible for connecting to monitor.
     */
    public MonitorProcess() {
        super();
        monitor = new Monitor();
        monitorReachable = false;
    }

    @Override
    public void reset() {
        monitor.disconnect();
        monitorReachable = false;
        monitor.setAddress("", 0);
        spreadUpdate();
    }

    /**
     * Sets the monitor's address and port.
     *
     * @param address the target's address
     * @param port    the target's port
     */
    public void setMonitor(String address, int port) {
        if (monitor.getAddressName().equals(address) && (monitor.getAddressPort() == port)) {
            return;
        }
        monitor.setAddress(address, port);
        monitorReachable = monitor.connect();
        spreadUpdate();
    }

    /**
     * Returns the monitor component.
     *
     * @return the monitor component
     */
    public Monitor getMonitor() {
        return monitor;
    }

    /**
     * Returns whether the monitor component is reachable.
     *
     * @return true, if the monitor is responding
     */
    public boolean isMonitorReachable() {
        return monitorReachable;
    }
}
