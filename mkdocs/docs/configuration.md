# Connector Configuration

The configuration files are in **&lt;InstallRoot&gt;/volume/config** directory. 

The following configuration files need to be created to configure the connector:

- `connection.json`: Contains the connection related items to connect to MAS Monitor and SCADA historian.
- `mapping.json`: Contains mapping configuration to map columns from mapping.csv file into device type, device id, dimensions and metrics, and transform data extracted data from SCADA historian and send to MAS.
- `CSV file`: CSV file with the Tag Points data that is used by the `mapping.json` to map the assets and the data into MAS Monitor.

## Pre-requisite

* To create `connection.json` file, you need SCADA Historian and MAS connectivity URL and credentials. For details refer to corresponding sections.
* The `CSV file` can be created using PI Builder or created manually following the expected format.

## Configuration details:

For details on the format and content of each configuration file, refer to the following sections:

- [Connection Configuration](connection.md)
- [Mapping Rules Configuration](mapping.md)


