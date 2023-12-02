# jsyslog-bench

A stresser/generator/simulator for syslog server performance benchmark

## Introduction

`jsyslog-bench` was initially created when I was doing some performance benchmark on a syslog server.
It aims at generating (or say sending) a syslog flow with designated log generation speed to the server, so that I can measure how much logs the server can handle.
Later it was also used for stress testing the syslog server.

**WARNING**:
Despite `jsyslob-bench` was once used in production by me, it is NOT a production ready tool.
It was neither well-designed nor nicely-implemented, and the way it worked in a benchmark was quite janky.
I updated dependencies in the original code (v0.6), but there will NOT be any further fix or feature. 

## Features

- **Log refurbishing**: simply capture some log samples, and `jsyslog-bench` will "refurbish" the datetime/timestamp in it before sending it out -- just like a freshly generated log
- **Speed designation**: the speed of generating & sending logs (EPS) can be designated, so a constant load of syslog can be generated
- **Multi-source mock**: `jsyslog-bench` can simulate multiple devices, useful when doing multi-source tests.

## Build

```shell
mvn package
```

**Note**:
`jsyslog-bench` was initially intended to build for Java 8, but it should work on later version of Java.
The dependencies were updated in v0.7 due to known vulnerabilities, but there shouldn't be breaking changes.

## Usage

### Start

```shell
java -jar jsyslog-bench.jar -c config.json
```

### Graceful shutdown

```shell
telnet 127.0.0.1 9876
STOP
```

### Force shutdown

Well, just `kill` or `KeyboardInterrupt` the process.

## Configuration File

```json
{
  "loggerAddr": "192.168.1.100",
  "loggerPort": 514,
  "telnetPort": 9876,
  "workerConfig": [
    {
      "ip": [
        "192.168.1.1",
        "192.168.1.2"
      ],
      "quantity": 1,
      "eps": 1,
      "threads": 1,
      "protocol": "udp",
      "sampleEncode": "utf-8",
      "sampleFile": "./sample1.log"
    },
    {
      "ip": [
        "192.168.1.3",
        "192.168.1.4"
      ],
      "quantity": 1,
      "eps": 1,
      "threads": 1,
      "protocol": "udp",
      "sampleEncode": "utf-8",
      "sampleFile": "./sample2.log"
    }
  ]
}
```

- loggerAddr: The IP address of syslog server
- loggerPort: The port where syslog server receives logs
- telnetPort: Local Telnet port where `jsyslog-bench` listens for STOP
- workerConfig.ip: The list of source IP addresses
  - The IP addresses should be assigned to NICs before running `jsyslog-bench`
  - For each IP address in the list, a new device will be simulated
- workerConfig.quantity: Positive integer for finite log generation, 0 for infinite loop(useful for stress test)
- workerConfig.eps: Positive integer between 1 and 400
  - Use multiple threads if an EPS over 400, use multiple threads
- workerConfig.threads: Number of threads created for each "device"
  - For each device/source IP address, the actual EPS = eps x threads
- workerConfig.protocol: Only `udp` was supported now
- workerConfig.sampleEncode: Encoding of the log sample, default `utf-8`
- workerConfig.sampleFile: Path to the log sample file

**Note**:
Append multiple `workerConfig` in order to simulate different types of devices.