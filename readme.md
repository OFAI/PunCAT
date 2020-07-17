How to run (for now, and using IntelliJ... I'll try to make this less of a hassle later.)

- Download OpenJDK 14
- Download JavaFX 14 (https://gluonhq.com/products/javafx/)
- Open the project in IntelliJ and...
  - go to File > Project structure... > Libraries
  - clicking on the + sign at the top left and selecting Java, add the `lib` folder of JavaFX
  - make sure Maven dependencies are installed
  - go to Run > Edit configurations and set the following VM options:
    - `-Xms1g -Xmx1g --module-path PATH_HERE --add-modules javafx.controls,javafx.fxml`
    - (where PATH_HERE is the path to the JavaFX `lib` folder)
  - run by starting `src/main/java/gui/Main.java`

