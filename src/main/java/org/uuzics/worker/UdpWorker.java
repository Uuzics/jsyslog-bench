package org.uuzics.worker;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import org.uuzics.common.WorkerConfig;
import org.uuzics.file.LogSampleManager;
import org.uuzics.common.CommonUtil;

/**
 * Syslog (UDP) worker
 * 
 * @author Uuzics
 */
public class UdpWorker implements Runnable {

	private WorkerConfig workerConfig;
	private LogSampleManager logMan;

	private String loggerIP;
	private int loggerPort;
	private String workerIP;
	private String sampleName;

	private DatagramSocket workerSocket; // udp socket

	private volatile boolean socketUp = false; // indicator for socket ready

	private volatile boolean stop = false; // indicator for stop worker

	public final int checkRoundation; // sent log count, for speed adjustment
	public final long idealDuration;
	private long sleepTime; // sleep time after each log sending, for speed control

	/**
	 * Constructor
	 * 
	 * @param workerConfig worker configuration object
	 * @param loggerIP     syslog receiver ip
	 * @param loggerPort   syslog receiver port
	 * @param workerIP     assigned ip for worker
	 */
	public UdpWorker(WorkerConfig workerConfig, LogSampleManager logMan, String loggerIP, int loggerPort,
			String workerIP) {
		this.workerConfig = workerConfig;
		this.logMan = logMan;
		this.loggerIP = loggerIP;
		this.loggerPort = loggerPort;
		this.workerIP = workerIP;

		this.sampleName = workerConfig.getSampleFile();

		// estimate sleep time
		int eps = this.workerConfig.getEps();
		if (eps <= 0) {
			eps = 1;
		}
		this.checkRoundation = eps; // check and adjust speed roughly every eps/eps=1s
		this.sleepTime = 1000 / eps;
		// idea duration of each check cycle
		this.idealDuration = this.checkRoundation * 1000 / eps;
	}

	/**
	 * UDP Socket reconnect
	 */
	private void reconnectSocket() {
		CommonUtil.commonPrintLog(2, "UDPworker(" + this.workerIP + ") reconnecting...");
		boolean retry = true;
		// retry until success
		while (retry) {
			try {
				if (null != this.workerSocket) {
					this.workerSocket.close();
				}
				this.workerSocket = null;
				this.workerSocket = new DatagramSocket(null);
				this.workerSocket.bind(new InetSocketAddress(this.workerIP, 0));
				this.workerSocket.connect(new InetSocketAddress(this.loggerIP, this.loggerPort));
				retry = false;
			} catch (IOException e) {
				CommonUtil.commonPrintLog(2, "UDPworker(" + this.workerIP + ") fail to reconnect, retry after 5s");
				try {
					Thread.sleep(5000);
				} catch (InterruptedException e1) {
				}
			}
		}
		CommonUtil.commonPrintLog(0, "UDPworker(" + this.workerIP + ") reconnect succeeded");
	}

	@Override
	public void run() {
		try {
			// sample loading now conducted in LogMan

			// socket establishment
			try {
				this.workerSocket = new DatagramSocket(null);
				this.workerSocket.bind(new InetSocketAddress(this.workerIP, 0));
				this.workerSocket.connect(new InetSocketAddress(this.loggerIP, this.loggerPort));

			} catch (IOException e) {
				CommonUtil.commonPrintLog(2, "UDPworker(" + this.workerIP + ") fail to launch socket");
				e.printStackTrace();
				return;
			}
			this.socketUp = true;

			String sampleEncode = logMan.getLogSampleEncode(sampleName);
			InetAddress loggerIA = InetAddress.getByName(this.loggerIP);

			// log generation and sending
			if (0 == this.workerConfig.getQuantity()) {
				// infinite loop if sendCount=0
				int sendCount = 0;
				// start timer
				long startTime = System.currentTimeMillis();

				while (!this.stop) {
					// keep looping until triggered stop procedure

					// generate a log then send it
					String line = regexString(logMan.getSampleLine(this.sampleName));
					byte[] data = line.getBytes(sampleEncode);
					DatagramPacket dp = new DatagramPacket(data, data.length, loggerIA, this.loggerPort);
					try {
						this.workerSocket.send(dp);
					} catch (IOException e) {
						reconnectSocket();
					}

					// speed control
					// also serves as InterruptedException trigger for thread graceful shutdown
					Thread.sleep(this.sleepTime);
					sendCount++;

					// speed adjustment
					if (sendCount >= this.checkRoundation) {
						long endTime = System.currentTimeMillis();
						speedAlter(endTime - startTime);
						sendCount = 0;
						startTime = System.currentTimeMillis();
					}
				}
			} else if (0 < this.workerConfig.getQuantity()) {
				// start timer
				int sendCount = 0;
				long startTime = System.currentTimeMillis();

				int logSent = 0;
				int logToSend = this.workerConfig.getQuantity();

				while ((!this.stop) && logSent < logToSend) {
					String line = regexString(logMan.getSampleLine(this.sampleName));
					byte[] data = line.getBytes(sampleEncode);
					DatagramPacket dp = new DatagramPacket(data, data.length, loggerIA, this.loggerPort);
					try {
						this.workerSocket.send(dp);
					} catch (IOException e) {
						reconnectSocket();
					}

					Thread.sleep(this.sleepTime);
					logSent++;
					sendCount++;

					if (sendCount >= this.checkRoundation) {
						long endTime = System.currentTimeMillis();
						speedAlter(endTime - startTime);
						sendCount = 0;
						startTime = System.currentTimeMillis();
					}
				}
			}
			// cleanup and shutdown

		} catch (InterruptedException e) {
		} catch (UnknownHostException e) {
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
//		} catch (IOException e) {
//			String eMessage = e.getMessage();
//			if (null != eMessage) {
//				System.out.println(eMessage);
//			}
		} finally {
			// cleanup
			if (null != this.workerSocket) {
				this.workerSocket.close();
			}

		}
	}

	/**
	 * Shutdown worker
	 */
	public void halt() {
		this.stop = true;
	}

	/**
	 * Is socket ready
	 */
	public boolean socketIsUp() {
		return this.socketUp;
	}

	/**
	 * Speed adjustment
	 */
	private void speedAlter(long realDuration) {
		// compare actual duration with ideal duration of each cycle
		// ideal duration = logs sent / (EPS/1000)
		// when 5% lower than ideal = too fast, increase sleep time
		// when 5% higher than ideal = too slow, decrease sleep time
		if (realDuration < (this.idealDuration / 20 * 19)) {
			this.sleepTime += 1;
		} else if (realDuration > (this.idealDuration / 20 * 21)) {
			this.sleepTime -= 1;
		}
		if (this.sleepTime < 0) {
			this.sleepTime = 0;
		}

	}

	/**
	 * Prepare log sample
	 */
	private String regexString(String origin) {
		String ret = origin;
		// replace time in log sample to current time
		long timestampMillis = System.currentTimeMillis();
		Date laborDay = new Date();
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
		SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm:ss");
		SimpleDateFormat plainDate = new SimpleDateFormat("MMM d", Locale.US);
		ret = ret.replaceAll("[0-9]{4}-[0-9]{2}-[0-9]{2}", dateFormat.format(laborDay.getTime()));
		ret = ret.replaceAll("[0-9]{2}:[0-9]{2}:[0-9]{2}", timeFormat.format(laborDay.getTime()));
		ret = ret.replaceAll("(Jan|Feb|Mar|Apr|May|Jun|Jul|Aug|Sep|Oct|Nov|Dec) [0-9]{1,2}", plainDate.format(laborDay.getTime()));
		// all 10-digit numbers are treated as timestamp
		// should work for the next dozens of years
		ret = ret.replaceAll("[0-9]{10}", Long.toString(timestampMillis / 1000));
		return ret;
	}

}
