package org.uuzics.common;

import java.util.List;

/**
 * Configuration for workers
 *
 * @author Uuzics
 */
public class WorkerConfig {
    private List<String> ip; // list of bound IP addresses
    private int quantity; // quantity of log to generate
    private int eps; // expected logs (events) per second
    private int threads; // threads for each target, multiplexer for log generation rate
    private String protocol; // syslog protocol, only udp supported
    private String sampleEncode; // log sample encode
    private String sampleFile; // log sample path

    public WorkerConfig() {
        super();
    }

    /**
     * Constructor
     *
     * @param ip         bound IP addresses
     * @param quantity   quantity of log to generate
     * @param eps        expected logs (events) per second
     * @param sampleFile log sample path
     */
    public WorkerConfig(List<String> ip, int ipStart, int ipStop, int quantity, int eps, String sampleFile) {
        super();
        this.ip = ip;
        this.quantity = quantity;
        this.eps = eps;
        this.sampleFile = sampleFile;
    }

    public List<String> getIp() {
        return ip;
    }

    public void setIp(List<String> ip) {
        this.ip = ip;
    }

    public String getSampleFile() {
        return sampleFile;
    }

    public void setSampleFile(String sampleFile) {
        this.sampleFile = sampleFile;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public int getEps() {
        return eps;
    }

    public void setEps(int eps) {
        this.eps = eps;
    }

    public int getThreads() {
        return threads;
    }

    public void setThreads(int threads) {
        this.threads = threads;
    }

    public String getProtocol() {
        return protocol;
    }

    public void setProtocol(String protocol) {
        this.protocol = protocol;
    }

    public String getSampleEncode() {
        return sampleEncode;
    }

    public void setSampleEncode(String sampleEncode) {
        this.sampleEncode = sampleEncode;
    }

    @Override
    public String toString() {
        String ret = "{";
        ret += quantity + ", ";
        ret += eps + ", ";
        ret += protocol + ", ";
        ret += sampleEncode + ", ";
        ret += sampleFile;
        ret += "}";
        return ret;
    }

}
