package org.uuzics.file;

import java.util.ArrayList;
import java.util.List;

/**
 * Log sample
 *
 * @author Uuzics
 */
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
