![Java CI](https://github.com/L0615T1C5-216AC-9437/vpn/workflows/Java%20CI/badge.svg)
[![Discord](https://img.shields.io/discord/519293558599974912.svg)](http://cn-discord.ddns.net)  
### Description
Flags VPNs and Kicks people using VPNs.

How?    If more than 20 (unless changed) Unique UUIDs connect using the same IP, the IP is flagged as a VPN.

### How to Use
Install and Let it do its job.  
May take a while to gather enough information to become effectionve

### Downloading a Jar
1) go to [releases](https://github.com/L0615T1C5-216AC-9437/vpn/releases) and download latest version, (not recommended to use pre-releases)
2) follow [#Installing](https://github.com/L0615T1C5-216AC-9437/vpn#installing)

### Building a Jar
(windows)  
1) download src.
2) run gradlew.bat
3) go to the plugin folder in cmd. (example: `cd C:\user\one\desk\pluginfolder\`)
4) type `gradlew jar` and execute.
5) done, look for plugin.jar in pluginfolder\build\libs\

Note: Highly recommended to use Java 8.

### Installing

Simply place the output jar from the step above in your server's `config/mods` directory and restart the server.  
List your currently installed plugins/mods by running the `mods` command.

### Configuring

Install the plugin and go to the directory specified.  
Once there, edit the vpn.cn file using Notepad++ or your preferred text editor.  

threshold: How many Unique UUIDs tied to a IP before the IP is considered a VPN*  
kickMessage: Message players received when kicked for using VPNs.  

\* (Server Side): IPs are cleared from Database once a month. (Client Side): IPs change on a weekly basis at most. 
### Contact
Discord: L0615T1C5.216AC#9437  
Discord-Server: [join](http://cn-discord.ddns.net )
