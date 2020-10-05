# Creating an executable package using [launch4j](http://launch4j.sourceforge.net/)

On the development machine, build a JAR package with `mvn compile package -P jar`.  Then use launch4j to create a Windows executable by running `launch4j launch4j/puncat_launch4j.xml`.

Copy the executable and the necessary resources (GermaNet, etc.) to the Windows machine.  Put the executable and the `resourcepaths.json` file in the same folder.  Also in the same folder, unpack the [Windows version of JDK 15](https://jdk.java.net/15/) to a folder called `jre`.  The executable can then be run.
