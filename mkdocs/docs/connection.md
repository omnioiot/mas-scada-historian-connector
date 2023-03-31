# Connection Configuration

The `connection.json` is a JSON file that allows you to define the Connector connection-related items to connect to SCADA historian database and MAS. The location of this file is `<InstallRoot>/ibm/masshc/volume/config/connection.json`. 

## Connection Configuration Object

Provide connection configuration object in a json formated file:

```
{
    "id": "String",
    "historian": {
        "type": "String",
        "jdbcUrl": "String",
        "user": "String",
        "password": "String",
        "serverTimezone": "String",
        "startDate": "String",
        "dbType": "String",
        "schema": "String",
        "database": "String",
        "extractInterval": Integer,
        "extractIntervalHistorical": Integer
    },
    "iotp": {
        "url": "String",
        "orgId": "String",
        "host": "String",
        "port": integer,
        "schemaName": "String",
        "tenantId": "String",
        "apiKey": "String",
        "apiToken": "String",
        "asHost": "String",
        "asKey": "String",
        "asToken": "String",
        "apiVersion": Integer,
        "publishProtocol": "String",
        "trustServerCert": Integer,
        "mamUserEmail": "String",
        "publishProtocol": Integer,        
        "iotClientType": Integer
    },
    "isSAASEnv": Integer,
}
```

Where:

* `id`: Description to identify the connection json file. It's also the name of the cache file being created in `<InstallRoot>/ibm/masshc/volume/data/tagcache` by the connector.
* `historian`: This configuration object is required. The configuration items specified in this object are used
to connect to SCADA historian to extract device data and send to MAS Monitor.
    ** Required Items: **
    * `type`: Historian type. The valid options are "osipi" or "ignition".
    * `jdbcUrl`: JDBC URL to connect to the historian database. Example *"jdbc:pisql://10.208.72.125/Data Source=pidemo; Integrated Security=SSPI;"*.
    * `user`: User name to connect to historian.
    * `password`: Password to connect to historian.
    * `serverTimezone`: Timezone of historian database server. It needs to be a Time zone ID (Example *"American/Chicago"* or *"PST"*). Refer to <br> [this document](https://www.ibm.com/docs/en/was/9.0.5?topic=ctzs-time-zone-ids-that-can-be-specified-usertimezone-property) for valid inputs.
    * `startDate`: Extract device data from the specified date. Valid format is *"YYYY-MM-DD HH:MM:SS"*
    * `dbType`: Type of database server configured as SCADA hostorian. If not specified, database type is taken as MYSQL. Feasible values are *"pisql"* (OSIPI historian) or *mssql* (which defaults to MYSQL). 
    * `schema`: Schema name. It is used to create the SQL query. `From <schema>.<database> where time ...`. Example for OSI PI: *"piarchive"*.
    * `database`: Database name. It is also used to create the SQL query. `From <schema>.<database> where time ...`. Example for OSI PI: *"picomp2"*.
    * `extractInterval`: Specifies the timewindow (in seconds) of each SQL query. The maximum and default value is *120*.
    * `extractIntervalHistorical`: Specifies the timewindow (in seconds) of each SQL query when historical data (past data) is requested. The default value is *1800*.
* `iotp`: This configuration object is required and contains the essential information to configure the connector for connecting to MAS and send device data to it. To fill in this object, you need credentials from MAS Monitor API and IoT tool API.
    ** Required Items: **
    * `url`: Specifies MAS Monitor IoT tool API URL used to configure MAS.
    * `orgId`: Specifies a six character organization Id assigned to your IoT Platform service. It is usually the first element in the `url` item.
    * `host`: Specifies IoT tool host for connecting to MAS to send device data.
    * `port`: Specifies port to connect to MAS.
    * `apiKey`: Specifies IoT tool API Key to configure device types, devices, interfaces and send MQTT messages.
    * `apiToken`: Specifies IoT tool API Token to configure device types, devices, interfaces and send MQTT messages.
    * `schemaName`: Specifies the database schema to configure dimensions.
    * `tenantId`: Specifies tenant ID.
    * `asHost`: Specifies MAS Monitor host to configure dimensions.
    * `asKey`: Specifies MAS Monitor API key to configure dimensions.
    * `asToken`: Specifies MAS Monitor API token to configure dimensions.
    * `apiVersion`: Specifies Monitor API version. Valid options are *1* and *2* (default).
    * `trustServerCert`: Specifies whether to trust all SSL certificates or not. If set to *1*, all certificates are trusted.
    * `mamUserEmail`: Email address used to create the REST client if `apiVersion` is set to *2* and `isSAASEnv` is set to *0*.
    * `publishProtocol`: Specifies the protocol/method used to publish. Default value is *"mqtt"*. Other valid values are *"kafka"* and *"dbupload"* but they are not currently supported.
    * `iotClientType`: Defines the IoT MQTT client type. Default value is *1* (Device Client), but it can also take *2* (Gateway client) or *3* (Application client). It affects the publisher.

* `isSAASEnv`: Decides the initialization of te REST client. If set to *0*, REST client uses the Monitor API credentials, while for any other value it will use the IoT tool credentials.


## Sample `connection.json` Configuration File

```
{
    "id": "Connection JSON file of ABC Corp.",
    "historian": {
        "type": "osipi",
        "jdbcUrl": "jdbc:pisql://10.208.72.125/Data Source=pidemo; Integrated Security=SSPI;",
        "user": "Administrator",
        "password": "xxxxxxxxxx",
        "serverTimezone": "American/Chicago",
        "startDate": "2021-12-05 05:00:00",
        "dbType": "pisql",
        "schema": "piarchive",
        "database": "picomp2",
        "extractInterval": 120,
        "extractIntervalHistorical": 1800
    },
    "iotp": {
        "url": "https://tenant1.iot.monitordemo.ibmmam.com/api/v0002",
        "orgId": "tenant1",
        "host": "tenant1.messaging.iot.monitordemo.ibmmam.com",
        "port": 443,
        "schemaName": "BLUADMIN",
        "apiKey": "a-xxxxxx-tavok0xsxt",
        "apiToken": "cNyH_XXXXXX-p2ppVl",
        "tenantId": "tenant1",
        "asHost": "tenant1.api.monitor.monitordemo3.ibmmam.com",
        "asKey": "xxxxxxxxxxxxxxxxxx",
        "asToken": "xxxxxxxxxxxxxxxxxxxx",
        "apiVersion": 2,
        "trustServerCert": 1,
        "mamUserEmail": "example@example.com",
        "publishProtocol": "mqtt",
        "iotClientType": 1
    },
    "isSAASEnv": 0
}
```

