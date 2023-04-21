# Connector Configuration

The configuration files are in **<InstallRoot>/volume/config** directory. 

The following configuration files need to be created to configure the connector:

- `connection.json`: Contains connection related configuration items to connect to MAS and SCADA historian.
- `mapping.json`: Contains mapping configuration to map columns from csv file into device type, device id, dimensions and metrics, and transform data extracted from SCADA historian and send to MAS. 

## Pre-requisite

* To create `connection.json` file, you need SCADA Historian and MAS connectivity URL and credentials. For details refer to corresponding sections.
* A CSV file with TAG Point data, typically exported from PI Builder (Microsoft Excel add-in), is needed to create mapping rules configuration file. Information required to fill in the CSV columns for each tag can be also found in PI System software, by right clicking in the desired attribute, and selecting its Properties.

## Configuration details:

For details on connection and mapping rules configuration items, refer to the following sections:

- [Connection Configuration](connection.md)
- [Mapping Rules Configuration](mapping.md)


