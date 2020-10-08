**How to build & run: (requires JDK 15 and Maven)**

- Run `mvn clean javafx:run` from the project directory

Some external resources are required to properly run the application:
- GermaNet
- The [interlingual index](https://uni-tuebingen.de/en/faculties/faculty-of-humanities/departments/modern-languages/department-of-linguistics/chairs/general-and-computational-linguistics/ressources/lexica/germanet/description/interlingual-index/) for GermaNet
- [Frequency lists](https://uni-tuebingen.de/en/faculties/faculty-of-humanities/departments/modern-languages/department-of-linguistics/chairs/general-and-computational-linguistics/ressources/lexica/germanet/applications-tools/) for GermaNet
- IPA transcriptions

These resources can be found here: https://jobim.ofai.at/gitlab/mate.lajko/puncat_resources



**Creating a Windows executable package using JPackage**

(Note: JPackage requires using Windows for this.)

1. Download [JDK 15](https://jdk.java.net/15/), extract the zip file, and set the `JAVA_HOME` environmental variable to point to its location. For example, on the command line: `setx JAVA_HOME "C:\jdk-15"`.
2. Download and install [Apache Maven](http://maven.apache.org/).
3. Download and install [WiX](https://wixtoolset.org/releases/).  Note that it may be necessary to first install and/or enable the .NET Framework 3.5 (which in Windows 10 can be done from the Control Panel under "Programs and Features".)
4. Look through the `jpackage` call in `build_app.bat`, near the end of the file, to see if there is anything you'd like to customize (like the Start Menu group or the company name). You can disable the console window that appears when launching the application by removing the `--win-console` flag.
5. Run `mvn clean install -P jpackage`.
6. You can find the installer in `target/installer/`.


**Building a JAR file:**

1. Run `mvn compile package -P jar` from the project directory
3. You can find the JAR file in the shade directory
2. Run the JAR file: `java -jar puncat.jar -Xms1g -Xmx2g`



**Setting up a development environment:**

Detailed steps for both IntelliJ and Eclipse can also be found [here](https://openjfx.io/openjfx-docs).

**Eclipse**:

1. Click **File** > **Import…**
2. Select **Maven** > **Existing Maven Projects**
3. Browse to the root directory of the repository
4. Click **Finish**
5. Click **Run** > **Run Configurations…**
6. Select **Maven Build**, then click on **New launch configuration** in the upper left corner
7. Select the project root folder as the **Base directory**
8. Set **Goals** to `clean javafx:run`
9. Click **Apply**

**IntelliJ:**

1. Click **Open or Import** on the welcome screen
2. Browse to the root directory of the repository
3. Click **Run** > **Edit Configurations…**
4. Click **Maven** on the right sidebar
5. Browse to **PunCAT** > **Plugins** > **javafx** > **javafx:run**
6. Right click **javafx:run** and select **Create [puncat javafx:run]**
7. It is probably a good idea to add the goal `clean` to the very beginning of the **Command line** field
8. Click **OK**
