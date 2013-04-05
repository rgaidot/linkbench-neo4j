Neo4j Connector for Facebook LinkBench
======================================

This project is an initiative to build a [Neo4j Graph Database](http://www.neo4j.org) connector to
[Facebook LinkBench](https://github.com/facebook/linkbench). Contributions are very welcome.

Everything in this repo is released under the Apache License, Version 2.0.

Project Status
--------------

Please note that this project is in a very early stage. Some modifications of LinkBench were needed to get this connector
written, so this version only works with [this fork](https://github.com/bachmanm/linkbench) of LinkBench. For more details,
read [this blog post](http://bachman.cz). Eventually, when changes are pulled into LinkBench, the connector will
be integrated into the LinkBench repository as well.

Compiling the Project
---------------------

First of all, you need to clone the forked LinkBench repository and install the artifacts into your local maven repo.
The following sequence of commands should achieve that, assuming you have Maven installed.

    git clone https://github.com/bachmanm/linkbench
    cd linkbench
    mvn clean install -DskipTests -DjvmArgs="-Xmx2g -Xms1g"

Sure, you don't have to skip tests, but the LinkBench tests require a working MySQL installation and some preliminary
setup, which is explained in the their README.md. In fact, before you start benchmarking Neo4j, you should read it anyway
in order to understand how LinkBench works.

When you have LinkBench compiled and installed, do a similar thing with this repo:

    cd ..
    git clone https://github.com/bachmanm/linkbench-neo4j
    cd linkbench-neo4j
    mvn clean package -DjvmArgs="-Xmx2g -Xms1g"

Then benchmark away by following [the instructions here](https://github.com/bachmanm/linkbench/blob/master/README.md)!

Benchmarking Neo4j
------------------

For now, only a single embedded (i.e. in-JVM) instance of Neo4j is supported. It can be configured in

    LinkConfigNeo4j.properties

Make sure you understand what the Neo4j memory configurations mean. A good place to start
is the [Neo4j Documentation](http://docs.neo4j.org/chunked/milestone/embedded-configuration.html).

Have fun!

