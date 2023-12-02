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

import java.util.ArrayList;
import java.util.List;

public class LogSample {

    private String sampleName; // sample name (path log sample file)
    private List<String> logSampleLines; // lines of log sample
    private volatile int lineCount;
    private volatile int lineCurser; // indicator of currently used line

    /**
     * Constructor
     *
     * @param sampleName sample name (path log sample file)
     */
    public LogSample(String sampleName) {
        super();
        this.sampleName = sampleName;
        this.logSampleLines = new ArrayList<String>();
        this.lineCount = 0;
        this.lineCurser = 0;
    }

    /**
     * Append log line
     *
     * @param newLine a new line of log
     */
    public void addLine(String newLine) {
        this.logSampleLines.add(newLine);
        this.lineCount++;
    }

    /**
     * Get a log line
     *
     * @return a line of log
     */
    public String grabLine() {
//		this.lineCurser++;
//		if (this.lineCurser >= this.lineCount) {
//			this.lineCurser = 0;
//		}
        this.lineCurser = (this.lineCurser + 1) % this.lineCount;
        String ret = "";
        try {
            ret = this.logSampleLines.get(this.lineCurser);
        } catch (IndexOutOfBoundsException e) {
            ret = this.logSampleLines.get(0);
        }
        return ret;
    }

    /**
     * Get log sample name
     *
     * @return sample name (path log sample file)
     */
    public String getSampleName() {
        return this.sampleName;
    }

}
