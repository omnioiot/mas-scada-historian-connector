# Overview

IBM MAS SCADA Historian connector application performs the following tasks:

- Registers the required information into MAS Monitor for device type and device creation, guided by
the configuration files.
- Extracts tag data from SCADA historian.
- Transforms tag data into MQTT events and send events to MAS Monitor. The device events can be used by IBM MAS Monitor for visualization and AI-driven analytics. 

Supported SCADA Historians:

- AVEVA PI historian (former OSIsoft PI historian)
- Ignition Historian

## High-Level Architecture

![MASHistorianConnector](overview.png)

1. AVEVA (former OSIsoft) data historian (PI Data Archive) stores sensor data collected from PLCs.
2. MAS SCADA Historian connector extracts PI Point data from PI Data Archive.
3. MAS SCADA Historian connector sends PI Point data to IBM MAS Monitor using MQTT protocol.
4. MAS Monitor is used for PI Point data visualization and AI-driven analytics.

