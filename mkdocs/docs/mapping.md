# Mapping Rules Configuration

In MAS Data Lake, the device data for different device types are stored in their own table. 
The tables are named as `IOT_<deviceType>`. The configuration items that control the transformation of
extracted data from SCADA historian for a specific device type, are defined in `mapping.json`.  
The location of this configuration file is `<InstallRoot>/volume/config/mapping.json`.

## Configuration Object

Provide data configuration object in a json formated file:
```
{
    "serviceName": "String",
    "description": "String",
    "serviceType": "String",
    "csvFileName": "String",
    "deviceTypes": [
        {
            "type": "String",
            "tagpathFilters": [
                "String"
            ],
            "discardFilters": [
                "String"
            ]
        }
    ],
    "metrics": {
        "name": "String",
        "value": "String",
        "unit": "String",
        "type": "String",
        "decimalAccuracy": "String",
        "label": "String"
    },
    "dimensions": {
       "tagpath": "String",
       "tagid": "String",
       "site": "String",
       "categories": "String"
    },
    "mappingFormat": Integer,
    "createEntityType": Bool
}
```

Where:

* `serviceName`: Defines the name of the service.
* `description`: Small description to identify the file.
* `csvFileName`: Name of the CSV file stored in `<InstallRoot>/volume/config/` that contains the desired tags information.
* `serviceType`: Type of service to be run. Can be *osipi* or *ignition*.
* `deviceTypes`: Specifies device types and corresponding tagpath patterns.
    * `type`: Specifies the device type name where the device will be created from.
    * `tagpathFilters`: List of tagpath patterns that indicate which tags are mapped into the specified device type.
    * `discardFilters`: List of tagpath patterns that inidicate which tags are **NOT** mapped into the specified device type.
* `metrics`: This object defines the mapping rule for device metrics data items.
    * `name`: Column name in CSV file used to map metric "Name". 
    * `value`: Metric value is extracted from PI Archive database at runtime. Set this to an empty string in this configuration object.
    * `unit`: Column name in CSV file used to map metric "Unit".
    * `type`: Column name in CSV file used to map metric "Type".
    * `label`: Column name in CSV file used to map metric "Label".
    * `decimalAccuracy`: Column name in CSV file used to map metric "Decimal Accuracy".
* `dimensions`: This object defines the mapping rule for device dimension data.
    * `tagpath`: One or more column name(s) in the CSV file used to create "tag path"
    * `tagid`: Column name in the CSV file used to map "tag id"
    * `site`: Column name in the CSV file used to map "site name".
    * `categories`: Column name in the CSV file used to map "Categories".
* `mappingFormat`: Decides how the tags are built from the CSV file. If set to *1* (Default), tagpath is can be created using different columns (use when CSV is exported from PI Builder), while if set to *2*, tagpaths need to be contained in a single column.
* `createEntityType`: Whether or not to enable the creation of Entity types. Can be either *true* or *false* (Default).


## Sample CSV file (pidemo.csv) used to define mapping.json configuration file

| Parent | Name | ObjecType | Error | UniqueID | ParentUniqueID | Description | Categories | AttributeDefaultUOM | AttributeType | AttributeValue | AttributeDataReference | AttributeDisplayDigits |
|---|---|---|---|---|---|---|---|---|---|---|---|---|
| DistillExample.4820Column | Accumulator Level | Attribute |   | 5ef1040f-b6af-59f7-352c-0442281fb1f9 |   | Liquid level in reflux accumulator | Process Parameters | % | Double |  | PI Point | -5 |

Below is the textualized version of the table above. One can copy it to a csv file to obtain the example pidemo.csv file.

```
Parent,Name,ObjectType,Error,UniqueID,ParentUniqueID,Description,Categories,AttributeDefaultUOM,AttributeType,AttributeValue,AttributeDataReference,AttributeDisplayDigits
DistillExample.4820Column,Accumulator Level,Attribute,,5ef1040f-b6af-59f7-352c-0442281fb1f9,,Liquid level in reflux accumulator,Process Parameters,%,Double,,PI Point,-5
```

## Sample `mapping.json` Configuration File for 

```
{
    "serviceName": "Service1",
    "description": "For example purposes"
    "csvFileName": "pidemo.csv",
    "serviceType": "osipi",
    "deviceTypes": [
        {
            "type": "PIDemoType",
            "tagpathFilters": [
                "DistillExample.*"
            ],
            "discardFilters":[
                "%Sacramento Plant%",
                "HydroExample.*"
            ]
        }
    ],
    "metrics": {
        "name": "${Name}",
        "value": "",
        "unit": "${AttributeDefaultUOM}",
        "type": "${AttributeType}",
        "decimalAccuracy": "${AttributeDisplayDigits}",
        "label": "${Description}"
    },
    "dimensions": {
       "tagpath": "${Parent},${Name}",
       "tagid": "${UniqueID}",
       "site": "IBMAustin",
       "categories": "${Categories}"
    },
    "mappingFormat": 1,
    "createEntityType": true
}
```

NOTES:

* To map a column name, specify the column name within curly brackets `{}`, For example "unit": "${AttributeDefaultUOM}. Column names are case sensitive. The "unit" in `pidemo.csv` example will map to *"lb/h"*.
* You can use comma-separated list of column name(s) to map a metric or dimension data. For example `"${Parent},${Name}"` mapping rule is used to specify tagpath. The tagpath in this example will map to "DistillExample.4820Column.Accumulator Level". A dot `.` will be used as a field-separator.
* DO not use curly brackets `{}` to specify a fixed value. For example `"site": "IBMAustin"`.

