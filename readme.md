**How to build & run: (requires JDK 14 and Maven)**

- Run `mvn clean javafx:run` from the project directory

Some external resources are required to properly run the application:
- GermaNet
- The [interlingual index](https://uni-tuebingen.de/en/faculties/faculty-of-humanities/departments/modern-languages/department-of-linguistics/chairs/general-and-computational-linguistics/ressources/lexica/germanet/description/interlingual-index/) for GermaNet
- [Frequency lists](https://uni-tuebingen.de/en/faculties/faculty-of-humanities/departments/modern-languages/department-of-linguistics/chairs/general-and-computational-linguistics/ressources/lexica/germanet/applications-tools/) for GermaNet

The path to these resources can be specified in `src/main/java/logic/ResourcePaths.java`.



**Setting up a development environment**

Detailed steps for both IntelliJ and Eclipse can be found [here](https://openjfx.io/openjfx-docs). (The project was set up by following the non-modular IntelliJ steps.) In short, open the project in your IDE, then set up a run configuration to launch the `Main` class – either using Maven (the command is `mvn javafx:run`) or the IDE's built in runner (in this case the VM options are: `-Xms1g -Xmx2g --module-path path_to_openjfx_sdk --add-modules javafx.controls,javafx.fxml`).

The GermaNet API recommends using the following JVM options: `-Xms1g -Xmx1g`.  If you are using IntelliJ with the Maven Runner to launch PunCAT, you can set these flags in `Run > Edit configurations > (your Maven configuration) > Runner > VM Options`.

It is also possible to launch the application through IntelliJ without Maven Runner. [Download](https://gluonhq.com/products/javafx/) the JavaFX 15 SDK, then specify the SDK location and the appropriate run configuration as described [here (Non-modular from IDE)](https://openjfx.io/openjfx-docs/#IDE-Intellij).