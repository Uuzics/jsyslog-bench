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

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.List;

import com.google.gson.Gson;

public class ConfigManager {
    private BenchConfig benchConfig; // Benchmark configuration

    public ConfigManager() {
        super();
    }

    /**
     * Constructor - load config as utf-8
     *
     * @param filePath path to config file
     */
    public ConfigManager(String filePath) {
        loadConfigFromFile(filePath, "utf-8");
    }

    /**
     * Constructor load config with designated encoding
     *
     * @param filePath path to config file
     */
    public ConfigManager(String filePath, String encoding) {
        loadConfigFromFile(filePath, encoding);
    }

    /**
     * Load benchmark config
     *
     * @param filePath path to config file
     * @param encoding encoding
     */
    public void loadConfigFromFile(String filePath, String encoding) {
        // Load json string
        String jsonObj = "";
        try {
            File file = new File(filePath);
            if (file.exists() && file.isFile()) {
                InputStreamReader reader = new InputStreamReader(new FileInputStream(file), encoding);
                BufferedReader bufferedReader = new BufferedReader(reader);
                String textLine = "";
                while ((textLine = bufferedReader.readLine()) != null) {
                    jsonObj += textLine;
                    // System.out.println(textLine);
                }
                bufferedReader.close();
                reader.close();
            } else {
                CommonUtil.commonPrintLog(2, "Fail to open configuration file.");
                CommonUtil.commonPrintLog(1, "A faulty path or bad file would cause error.");
                CommonUtil.commonPrintLog(1, "This is a known issue but I won't fix it, be careful when setting path");
            }
        } catch (Exception e) {
            CommonUtil.commonPrintLog(2, "Fail to open configuration file.");
            CommonUtil.commonPrintLog(1, "A faulty path or bad file would cause error.");
            CommonUtil.commonPrintLog(1, "This is a known issue but I won't fix it, be careful when setting path");
            e.printStackTrace();
        }
        // Convert json to benchmark config object
        Gson gson = new Gson();
        this.benchConfig = gson.fromJson(jsonObj, BenchConfig.class);
    }

    public BenchConfig getBenchConfig() {
        return this.benchConfig;
    }

    public List<WorkerConfig> getWorkerConfig() {
        return this.benchConfig.getWorkerConfig();
    }

}