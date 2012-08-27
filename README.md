About Maven-CRX-Plugin
----------------------

It is a plugin designed for Maven 2.x+ based builds to automate deployment of CRX compliant packages. 
It allows setting up upload and install commands as a step in a build process speeding up entire development cycle.
Additionally, it simplifies package deployment process by providing a goal (crx:activate) for one-step installation 
of packages on all publish instances.

### Installation

Checkout the source code:

    cd [folder of your choice]
    git clone git://github.com/Cognifide/Maven-CRX-Plugin.git
    cd Maven-CRX-Plugin

Compile and install:

    mvn clean package install

### Usage

Set up POM file (check documentation for more configuration options):

    (...)
    <plugin>
      <groupId>com.cognifide.maven.plugins</groupId>
      <artifactId>maven-crx-plugin</artifactId>
      <version>1.0.3</version>
      <configuration>
        <url>http://crx-machine:5402</url>
        <user>${crx.username}</user>
        <password>${crx.password}</password>
      </configuration>
    </plugin>
    (...)

Invoke goal:

    mvn ... crx:install

or (only CQ 5.5):
	
    mvn ... crx:activate

More documentation
------------------
* [Maven-CRX-Plugin Wiki](https://github.com/Cognifide/Maven-CRX-Plugin/wiki)
* [Cognifide.com](http://cognifide.com)
* [Maven](http://maven.apache.org)
* [CRX](http://www.day.com/day/en/products/crx.html)
* [CRX API](http://dev.day.com/content/docs/en/crx/current/how_to/package_manager.html#Package%20Manager%20HTTP%20Service%20API)
