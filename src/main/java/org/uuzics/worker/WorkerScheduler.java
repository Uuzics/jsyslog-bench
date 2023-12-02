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

/**
 * Worker scheduler
 *
 * @author Uuzics
 */
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
