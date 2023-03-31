# Installing MAS SCADA Historian Connector

The installation process includes downloading the installation package from IBM Passport Advantage, 
and completing the initial setup on an on-premise host system or in cloud. The on-premise host system
or the cloud instance must have access to SCADA Historian.

## System requirements

Before you begin the connector installation, make sure that your system fulfills the following requirements.

<ul>
  <li> The connector is tested on the following operating environments.
    <ul><li> Windows 2016 server or higher </li>
    <li> Windows 10 </li>
    <li> Ubuntu 18.08 </li>
    <li> macOS BigSur </li></ul></li>
  <li> Memory: 8 GB </li>
  <li> Disk space: 10 GB of free disk space for install package, data and logs </li>
  <li> Java Runtime Environment: Java 11. Note that the connector installer will download and install OpenJDK 11 if it can not find Java 11 on the host system. </li>
</ul>

## MAS data connector installation steps

### On Windows system:

You need Powershell on your Windows system. For information on how to install it, check: <br>
[How to install Powershell on Windows](https://docs.microsoft.com/en-us/powershell/scripting/install/installing-powershell-core-on-windows?view=powershell-7)?

Open a Powershell terminal with admin privileges and run the following commands to download the install script `install.ps1` from the connector repository to the desired directory, and run it.
```
% cd <desired_directory>
% Invoke-WebRequest -Uri "https://raw.githubusercontent.com/ibm-watson-iot/mas-scada-historian-connector/master/bin/install.ps1" -OutFile ".\install.ps1"
% powershell.exe -ExecutionPolicy Bypass .\install.ps1
```

To configure connector tasks, run the following commands:
```
% cd C:\ibm\masshc\bin\
% powershell.exe -ExecutionPolicy Bypass .\configTask.ps1
```

### On macOS or Linux systems

Use one the following options to get the project source on your system:

* Use a Web browser to download the Connector zip file from the GitHub repository and save it in the `/tmp` directory. To unzip it, open a shell prompt and run the following commands:
```
$ cd /tmp
$ unzip mas-scada-historian-connector-master.zip
```
* Open a shell prompt and use the following curl command to download the Connector zip file from the GitHub project into the `/tmp` directory and unzip it
```
$ curl https://github.com/ibm-watson-iot/mas-scada-historian-connector/archive/master.zip -L -o /tmp/mas-scada-historian-connector-master.zip
$ cd /tmp
$ unzip mas-scada-historian-connector-master.zip
```
* Use git command to clone the Connector GitHub repository into the `/tmp` directory:
```
$ cd /tmp
$ git clone https://github.com/ibm-watson-iot/mas-scada-historian-connector
```

To install the connector, open a shell prompt, and run the install script:
```
$ cd /tmp/mas-scada-historian-connector
$ ./bin/install.sh
```

