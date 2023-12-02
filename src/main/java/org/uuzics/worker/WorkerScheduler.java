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
package org.uuzics.worker;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.uuzics.common.ConfigManager;
import org.uuzics.common.WorkerConfig;
import org.uuzics.common.CommonUtil;
import org.uuzics.file.LogSampleManager;

public class WorkerScheduler {

    ThreadPoolExecutor threadPool = null; // thread pool
    List<UdpWorker> udpList = new ArrayList<UdpWorker>();

    /**
     * Start workers
     *
     * @param confMan
     * @param logMan
     */
    public void start(ConfigManager confMan, LogSampleManager logMan) {

        String loggerIP = confMan.getBenchConfig().getLoggerAddr();
        int loggerPort = confMan.getBenchConfig().getLoggerPort();

        // init thread pool
        this.threadPool = new ThreadPoolExecutor(500, 600, 200, TimeUnit.MILLISECONDS,
                new ArrayBlockingQueue<Runnable>(10));

        // acquire worker list
        List<WorkerConfig> workerConfigList = confMan.getWorkerConfig();

        // start workers
        for (WorkerConfig config : workerConfigList) {

            // init and start udp worker
            if ("udp".equals(config.getProtocol())) {

                List<String> ipList = config.getIp();

                // start threads
                for (String ip : ipList) {

                    int threads = config.getThreads();
                    for (int threadCount = 0; threadCount < threads; threadCount++) {

                        // set worker config
                        UdpWorker worker = new UdpWorker(config, logMan, loggerIP, loggerPort, ip);

                        // start and observe worker
                        threadPool.execute(worker);
                        udpList.add(worker);

                        // wait until current worker socket ready
                        while (!worker.socketIsUp()) {
                            try {
                                Thread.sleep(50);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }
                        CommonUtil.commonPrintLog(0, "UDPworker(" + ip + ") thread " + threadCount + " started.");
                    }
                }
            }
        }
    }

    /**
     * Stop workers gracefully
     */
    public void strike() {
        // stop udp workers
        for (UdpWorker worker : this.udpList) {
            worker.halt();
        }
        // shutdown thread pool
        CommonUtil.commonPrintLog(0, "Shutting down thread pool...");
        this.threadPool.shutdownNow();
    }

}
