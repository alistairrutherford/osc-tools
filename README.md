osc-tools
=========

OSC (Open Sound Control) libraries and tools written in Java

osc-common 
-----------
Domain Model of OSC objects with Unit tests based on samples from the source site.

osc-network 
-----------
NETTY based client and server components.

osc-router 
-----------
(JavaFX based UI tool which allows you to route OSC messages from endpoint to endpoint [OSC to MIDI currently only supported].

This is a work in progress. it uses the java-fx-maven plugin from here https://github.com/zonski/javafx-maven-plugin/wiki

You will need to follow the instruction on the plugin page to ensure JavaFX is visible to the build.

![User interface](https://github.com/alistairrutherford/images/raw/master/oscrouterfx.png)
