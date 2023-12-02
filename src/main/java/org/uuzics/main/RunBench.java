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
package org.uuzics.main;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

import org.uuzics.common.CommonUtil;
import org.uuzics.common.ConfigManager;
import org.uuzics.file.LogSampleManager;
import org.uuzics.worker.WorkerScheduler;

public class RunBench {

    /**
     * Start jsyslog-bench
     */
    public void run(String[] args) {
        // welcome
        System.out.println("jsyslog-bench alpha0.6");

        // load config file
        CommonUtil.commonPrintLog(0, "Loading bench configuration file...");
        String configFile = parseConfigFileArg(args);
        ConfigManager confMan = new ConfigManager(configFile);
        CommonUtil.commonPrintLog(0, "Bench configuration loaded!");
        CommonUtil.startupInfo();

        // load log samples
        LogSampleManager logMan = new LogSampleManager();
        logMan.setup(confMan);

        // start benchmark
        CommonUtil.commonPrintLog(0, "Starting workers...");
        WorkerScheduler theUnion = new WorkerScheduler();
        theUnion.start(confMan, logMan);
        CommonUtil.commonPrintLog(0, "Workers started!");

        // start local telnet port
        // wait for STOP command
        int telnetPort = confMan.getBenchConfig().getTelnetPort();
        CommonUtil.commonPrintLog(0, "Port " + telnetPort + " is open now. Send \"STOP\" to halt jsyslog-bench.");
        boolean flag = telnetListener(telnetPort);

        // shutting down
        if (flag) {
            CommonUtil.commonPrintLog(0, "Terminating jsyslog-bench...");
            theUnion.strike();
            CommonUtil.commonPrintLog(0, "All workers should have been stopped.");
            CommonUtil.commonPrintLog(0,
                    "Note that in case a socket is blocked, jsyslog-bench may not exit (gracefully), then a force shutdown is needed.");
            CommonUtil.commonPrintLog(0, "Bye.");
        } else {
            CommonUtil.commonPrintLog(1, "Something went wrong. You may need to terminate jsyslog-bench manually.");
        }
    }

    private String parseConfigFileArg(String[] args) {
        String configFile = "benchConfig.json";
        CommandLineParser parser = new DefaultParser();
        Options options = new Options();
        options.addOption("c", "config", true, "Path of configuration file.");
        try {
            CommandLine line = parser.parse(options, args);
            if (line.hasOption("config")) {
                configFile = line.getOptionValue("config");
            }
        } catch (ParseException ignored) {
        }

        return configFile;
    }

    private boolean telnetListener(int telnetPort) {
        // start port
        ServerSocket serverSocket = null;
        boolean run = true;
        try {
            serverSocket = new ServerSocket(telnetPort);
        } catch (IOException e) {
            CommonUtil.commonPrintLog(2, "Fail to open port " + telnetPort);
            return false;
        }

        // listen for STOP
        Socket socket = null;
        InputStream socketIn = null;
        BufferedReader br = null;
        try {
            while (run) {
                socket = serverSocket.accept();
                socketIn = socket.getInputStream();
                br = new BufferedReader(new InputStreamReader(socketIn, "utf-8"));
                String cmd = null;
                while (null != (cmd = br.readLine())) {
                    if ("STOP".equals(cmd)) {
                        run = false;
                        break;
                    }
                }
            }
        } catch (IOException e) {
            // telnet exception
            CommonUtil.commonPrintLog(2, "Exception occured on receiving commands.");
            e.printStackTrace();
            return false;
        } finally {
            // cleanup
            try {
                if (null != br) {
                    br.close();
                }
            } catch (IOException ignored) {
            }
            try {
                if (null != socketIn) {
                    socketIn.close();
                }
            } catch (IOException ignored) {
            }
            try {
                if (null != socket) {
                    socket.close();
                }
            } catch (IOException ignored) {
            }
            try {
                if (null != serverSocket) {
                    serverSocket.close();
                }
            } catch (IOException ignored) {
            }
        }

        return true;
    }

}
