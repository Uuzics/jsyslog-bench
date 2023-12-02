package org.uuzics.common;

import java.util.List;

/**
 * Benchmark configuration
 *
 * @author Uuzics
 */
public class BenchConfig {
    private String loggerAddr; // syslog receiver address
    private int loggerPort; // syslog receiver port
    private List<WorkerConfig> workerConfig; // list of worker configs
    private int telnetPort; // local telnet port

    public BenchConfig() {
        super();
    }

    /**
     * Constructor
     *
     * @param loggerAddr   syslog receiver address
     * @param loggerPort   syslog receiver port
     * @param workerConfig list of worker configs
     */
    public BenchConfig(String loggerAddr, int loggerPort, List<WorkerConfig> workerConfig) {
        super();
        this.loggerAddr = loggerAddr;
        this.loggerPort = loggerPort;
        this.workerConfig = workerConfig;
    }

    public String getLoggerAddr() {
        return loggerAddr;
    }

    public void setLoggerAddr(String loggerAddr) {
        this.loggerAddr = loggerAddr;
    }

    public int getLoggerPort() {
        return loggerPort;
    }

    public void setLoggerPort(int loggerPort) {
        this.loggerPort = loggerPort;
    }

    public List<WorkerConfig> getWorkerConfig() {
        return workerConfig;
    }

    public void setWorkerConfig(List<WorkerConfig> workerConfig) {
        this.workerConfig = workerConfig;
    }

    public int getTelnetPort() {
        return telnetPort;
    }

    public void setTelnetPort(int telnetPort) {
        this.telnetPort = telnetPort;
    }

    @Override
    public String toString() {
        String ret = "{";
        ret += loggerAddr + ", ";
        ret += loggerPort + ", ";
        ret += workerConfig.size();
        ret += "}";
        return ret;
    }

}
