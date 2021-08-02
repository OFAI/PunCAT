# Building, installing, and running PunCAT

As described below, you can build and run PunCAT directly with Maven,
or you can create a Java JAR that can be run from any system with a
Java runtime environment (JRE), or you can create a package for
Microsoft Windows that will allow PunCAT to be installed and run like
any other native application (without the need to install a JRE in
advance).  In any case, please make sure that you've first installed
the prerequisite tools and resources listed in the following section.

## Prerequisites

Build tools:

- [JDK 15](https://openjdk.java.net/projects/jdk/15/) or a compatible
  Java development environment
- [Apache Maven](https://maven.apache.org/)

External GermaNet resources:

- [GermaNet 15.0](http://www.sfs.uni-tuebingen.de/GermaNet/).  Copy
  the `GN_V150_XML` directory from the GermaNet distribution into
  PunCAT's [`src/main/resources`](src/main/resources) directory.
  (Note that GermaNet is a proprietary resource for which you must
  obtain a licence in advance of downloading.)
- The [Release 15.0 ILI
  mappings](https://uni-tuebingen.de/en/faculties/faculty-of-humanities/departments/modern-languages/department-of-linguistics/chairs/general-and-computational-linguistics/ressources/lexica/germanet/description/interlingual-index/)
  for GermaNet.  Copy the `interLingualIndex_DE-EN.xml` file from the
  ILI mappings distribution into PunCAT's
  [`src/main/resources/GN_V150_XML`](src/main/resources/GN_V150_XML)
  directory.
- The [Frequency Lists Release
  15.0](https://uni-tuebingen.de/en/faculties/faculty-of-humanities/departments/modern-languages/department-of-linguistics/chairs/general-and-computational-linguistics/ressources/lexica/germanet/applications-tools/)
  for GermaNet.  Copy the `GN_V150-FreqLists` directory from the
  Frequency Lists distribution into PunCAT's
  [`src/main/resources`](src/main/resources) directory.

You may, if you wish, install these GermaNet resources somewhere other
than PunCAT's [`src/main/resources`](src/main/resources) directory, in
which case you should update the corresponding paths in
[`src/main/resources/resourcepaths.json`](src/main/resources/resourcepaths.json).

PunCAT may work with versions of GermaNet higher than 15.0, but this
has not been tested.  If you use a more recent version, you must
supply your own IPA transcriptions for the words in
[`src/main/resources/ipa/germanet2ipaLowercase.csv`](src/main/resources/ipa/germanet2ipaLowercase.csv).
(To produce this file, we used the GermaNet API to extract a list of
all words in GermaNet and then ran it through
[Phonetisaurus](https://github.com/AdolfVonKleist/Phonetisaurus) to
get the transcriptions. You may wish to do something similar, though
you could of course use a different grapheme-to-phoneme tool.)

## Running directly with Maven

Run `mvn clean javafx:run` from PunCAT's main directory.

## Creating and running a JAR

Run `mvn compile package -P jar` from PunCAT's main directory.  The
JAR, named `puncat.jar`, will be output in the `shade` directory.  You
can copy this JAR to any machine with a Java runtime enivornment and
run it as follows: `java -jar puncat.jar -Xms1g -Xmx2g`

## Creating a native Microsoft Windows installer

The following instructions require a machine running Microsoft
Windows.  You should start from a pristine copy of the PunCAT source,
since the presence of extra files may interfere with the installer.

1. Download [JDK 15](https://jdk.java.net/15/), extract the zip file,
   and set the `JAVA_HOME` environment variable to point to its
   location. For example: `setx JAVA_HOME "C:\jdk-15"`.
2. Download and install [Apache Maven](https://maven.apache.org/).
3. Download and install [WiX](https://wixtoolset.org/releases/).  Note
   that it may be necessary to first install and/or enable the .NET
   Framework 3.5 (which in Windows 10 can be done from the Control
   Panel under "Programs and Features".)
4. Look through the `jpackage` call in
   [`build_app.bat`](build_app.bat), near the end of the file, to see
   if there is anything you'd like to customize (like the Start Menu
   group or the company name). You can disable the console window that
   appears when launching the application by removing the
   `--win-console` flag.
5. Run `mvn clean install -P jpackage`.

Following these steps, you will find installer in the
`target/installer` directory.  This installer can be copied to and run
from any other Microsoft Windows machine.  After running the
installer, PunCAT can be launched from the Start menu or desktop icon,
just like a native Windows application.
