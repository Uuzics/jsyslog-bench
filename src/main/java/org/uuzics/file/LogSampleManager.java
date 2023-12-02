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
package org.uuzics.file;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.uuzics.common.ConfigManager;
import org.uuzics.common.WorkerConfig;
import org.uuzics.common.CommonUtil;

public class LogSampleManager {

    private Map<String, LogSample> sampleList; // list of log samples
    private Map<String, String> fileList = new HashMap<String, String>();

    /**
     * Constructor
     */
    public LogSampleManager() {
        super();
        this.sampleList = new HashMap<String, LogSample>();
    }

    /**
     * @param confMan
     */
    public void setup(ConfigManager confMan) {
        List<WorkerConfig> workerList = confMan.getWorkerConfig();
        this.fileList = new HashMap<String, String>();

        for (WorkerConfig wConfig : workerList) {
            String fileName = wConfig.getSampleFile();
            String fileEncode = wConfig.getSampleEncode();
            if (!fileList.containsKey(fileName)) {
                fileList.put(fileName, fileEncode);
                loadLogSample(fileName, fileEncode);
            }
        }
    }

    /**
     * Get log sample object (deprecated)
     *
     * @param sampleName sample name (path log sample file)
     * @return log sample object
     * @deprecated
     */
    public LogSample getLogSample(String sampleName) {
        if (this.sampleList.containsKey(sampleName)) {
            return this.sampleList.get(sampleName);
        }
        // may cause NPE if sample path incorrectly configured in config file, so config it carefully
        // deprecated, won't fix
        return null;
    }

    /**
     * Load log sample
     *
     * @param fileName path log sample file
     * @param encoding log file encode
     * @return load success or not
     */
    public boolean loadLogSample(String fileName, String encoding) {
        LogSample sample = new LogSample(fileName);
        final int LINE_LIMIT = 600000; // load first LINE_LIMIT lines if sample is too large, avoid OOM

        try {
            File file = new File(fileName);
            if (file.exists() && file.isFile()) {
                CommonUtil.commonPrintLog(0, "Loading log sample: " + fileName);
                InputStreamReader reader = new InputStreamReader(new FileInputStream(file), encoding);
                BufferedReader bufferedReader = new BufferedReader(reader);
                String textLine = "";
                int count = 0;
                while (((textLine = bufferedReader.readLine()) != null) && (count < LINE_LIMIT)) {
                    sample.addLine(textLine);
                    count++;
                }
                bufferedReader.close();
                reader.close();
            } else {
                CommonUtil.commonPrintLog(2, "Not a file and skip: " + fileName);
                CommonUtil.commonPrintLog(1, "A faulty path or bad file would cause error.");
                CommonUtil.commonPrintLog(1, "This is a known issue but I won't fix it, be careful when setting path");
                return false;
            }
        } catch (Exception e) {
            CommonUtil.commonPrintLog(2, "Fail to load: " + fileName);
            e.printStackTrace();
            return false;
        }

        this.sampleList.put(fileName, sample);
        return true;

    }

    /**
     * Get a line of log sample
     *
     * @param sampleName
     * @return a line of log sample
     */
    public String getSampleLine(String sampleName) {
        if (this.sampleList.containsKey(sampleName)) {
            return this.getLogSample(sampleName).grabLine();
        } else {
            return "";
        }
    }

    /**
     * Get log sample encode
     *
     * @param sampleName
     * @return encode of log sample
     */
    public String getLogSampleEncode(String sampleName) {
        String sampleEncode = this.fileList.get(sampleName);
        return sampleEncode;
    }

}
