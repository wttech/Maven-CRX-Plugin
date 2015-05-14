![Cognifide logo](http://www.cognifide.com/~/media/cognifide2014/avada-assets/cognifide_logo.png)
# Maven CRX Plugin 

## Change of artifactId
With plugin version 2.0.0 we have changed artifactId from `maven-crx-plugin` to `crx-maven-plugin` according to [Plugin Naming Convention](https://maven.apache.org/guides/plugin/guide-java-plugin-development.html#Important_Notice:_Plugin_Naming_Convention_and_Apache_Maven_Trademark).

## Purpose
This is a plugin designed for Maven 2.x+ based builds to automate CRX compliant packages deployment. 
It allows to set up upload and install commands as a step in a build process speeding up entire development cycle.

On instances with CRX 2.3 or later, it also simplifies package deployment process by providing a goal (crx:activate) for one-step installation of packages on all publish instances (provided that replication agents are properly configured).

## Features
* uploads a selected artifact to a running CRX instance
* installs previously uploaded artifact in a running CRX instance 
* activates (replicates and installs) previously uploaded artifact on all publish instances (requires CRX 2.3 or later)

## Prerequisites
* CQ/AEM instance with CRX 2.1 or later version
* Maven 2.x, 3.x

## Installation
Maven CRX Plugin is available from Maven Central Repo. However if you want to check out the newest development version, do the following:

Checkout the source code:

    cd [folder of your choice]
    git clone git://github.com/Cognifide/Maven-CRX-Plugin.git
    cd Maven-CRX-Plugin

Compile and install:

    mvn clean install

## Usage
Set up POM file (check documentation for more configuration options):

```xml
    (...)
    <plugin>
      <groupId>com.cognifide.maven.plugins</groupId>
      <artifactId>maven-crx-plugin</artifactId>
      <version>1.0.3</version>
      <configuration>
        <url>${crx.url}</url>
        <user>${crx.username}</user>
        <password>${crx.password}</password>
      </configuration>
    </plugin>
    (...)
```
Now you can invoke one of the Maven CRX Plugin goals:
* to upload artifacts to CRX instance use

        mvn ... crx:upload

* to upload and install artifacts on CRX instance use

        mvn ... crx:install

* to upload, install and replicate artifacts use (this will works only on CRX 2.3 or later)

        mvn ... crx:activate

## More documentation
* [Maven-CRX-Plugin Wiki](https://github.com/Cognifide/Maven-CRX-Plugin/wiki)
* [Cognifide.com](http://cognifide.com)
* [Maven](http://maven.apache.org)
* [CRX API](http://dev.day.com/content/docs/en/crx/current/how_to/package_manager.html#Package%20Manager%20HTTP%20Service%20API)

## Commercial Support
Technical support can be made available if needed. Please [contact us](mailto:crx-plugin-support@cognifide.com) for more details.

We can:
* prioritize your feature request,
* tailor the product to your needs,
* provide a training for your engineers,
* support your development teams.