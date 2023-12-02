/*
 * Copyright 2021 Uuzics
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.uuzics.common;

import java.util.List;

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
