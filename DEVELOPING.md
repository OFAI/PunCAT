# Setting up a development environment

If you wish to modify or further develop PunCAT, it can be imported
into your favourite IDE the same way you would any other Maven
project.  However, some further configuration may be necessary to get
PunCAT to launch from within the IDE.  The following sections give a
brief overview of what is required for Eclipse and IntelliJ; further
details can be found in the [JavaFX
docs](https://openjfx.io/openjfx-docs).

## Eclipse

1. Click **File** > **Import…**
2. Select **Maven** > **Existing Maven Projects**
3. Browse to the root directory of the repository
4. Click **Finish**
5. Click **Run** > **Run Configurations…**
6. Select **Maven Build**, then click on **New launch configuration**
   in the upper left corner
7. Select the project root folder as the **Base directory**
8. Set **Goals** to `clean javafx:run`
9. Click **Apply**

## IntelliJ

1. Click **Open or Import** on the welcome screen
2. Browse to the root directory of the repository
3. Click **Run** > **Edit Configurations…**
4. Click **Maven** on the right sidebar
5. Browse to **PunCAT** > **Plugins** > **javafx** > **javafx:run**
6. Right click **javafx:run** and select **Create [puncat javafx:run]**
7. It is probably a good idea to add the goal `clean` to the very
   beginning of the **Command line** field
8. Click **OK**
