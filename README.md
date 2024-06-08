# AdvancedBanX 3.0.6

Bukkit & BungeeCord Plugin Bundle <br>
Check out our [Spigot-Page](https://www.spigotmc.org/resources/advancedbanx.117067/) for more  information!

![Minecraft Version 1.7-1.20](https://img.shields.io/badge/supports%20minecraft%20versions-1.7--1.20-brightgreen.svg)
![license GPL-3.0](https://img.shields.io/badge/license-GPL--3.0-lightgrey.svg)

_Coded by Leoko_ 
<br>
_Updated and Maintained by 2vY (hlpdev)_

## Description
AdvancedBanX is an updated and modernized version of the original AdvancedBan
plugin made by Leoko. It is an All-In-One punishment system with warns, 
tempwarns, mutes, tempmutes, bans, tempbans, ipbans, and kicks. There is 
also player history so you can see players' past punishments. The plugin 
has configurable time & message layouts which automatically calculate and 
increase the punishment time for certain reasons. AdvancedBanX provides a 
full message file so you can change and translate all messages. There is 
also a detailed main configuration file with a lot of useful settings. At 
the moment, AdvancedBanX supports Bukkit (as well as spigot/paper) and 
Bungeecord. It also supports MySQL and Local File saving as it's storage 
platform.

## API
The API requires the AdvancedBanX plugin to be installed on the server. When making an addon, make sure to make it clear that the main AdvancedBanX plugin is also required!

#### Maven:
```xml
<repositories>
    ...

    <repository>
        <id>hnt8</id>
        <url>https://java.hnt8.net</url>
    </repository>
    
    ...
</repositories>

<dependencies>
    ...
    
    <dependency>
        <groupId>net.hnt8.advancedban</groupId>
        <artifactId>AdvancedBanX</artifactId>
        <version>3.0.6</version>
        <scope>provided</scope>
    </dependency>

    ...
</dependencies>
```

## Goals
* Sponge Support
* Velocity Support


[//]: # (## API)

[//]: # (To use the API you need to add AdvancedBan to your project and declare it as a dependency in the plugin.yml.)

[//]: # ()
[//]: # (Add AdvancedBan to you project by adding the AdvancedBan.jar to your build-path or as a:)

[//]: # (#### Maven dependency in your pom.xml)

[//]: # ()
[//]: # (Example Usage from Jitpack:)

[//]: # (```xml)

[//]: # (<repositories>)

[//]: # (  <repository>)

[//]: # (    <id>jitpack.io</id>)

[//]: # (    <url>https://jitpack.io</url>)

[//]: # (  </repository>)

[//]: # (</repositories>)

[//]: # (...)

[//]: # (<dependency>)

[//]: # (  <groupId>com.github.DevLeoko</groupId>)

[//]: # (  <artifactId>AdvancedBan</artifactId>)

[//]: # (  <version>v2.3.0</version>)

[//]: # (</dependency>)

[//]: # (```)

[//]: # (Note: Jitpack also supports dependencies for gradle!)

[//]: # ()
[//]: # ([AdvancedBan on Jitpack]&#40;https://jitpack.io/#DevLeoko/AdvancedBan&#41;)

[//]: # ()
[//]: # ()
[//]: # (You can use this API for both Spigot and Bungeecord plugins.)

[//]: # (Check out the [Java Docs]&#40;https://devleoko.github.io/AdvancedBan/&#41; to get started.)
