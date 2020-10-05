**Executable package using [launch4j](http://launch4j.sourceforge.net/)**

1. Build a JAR package: `mvn compile package -P jar`
2. Open puncat_launch4j.xml in launch4j (blue folder icon, upper left corner)
3. Click on the gear icon to build the executable file
4. Unpack the [Windows version of JDK 15](https://jdk.java.net/15/) to a folder called `jre` next to the executable
5. Don't forget to place a resourcepaths.json file next to the executable as well