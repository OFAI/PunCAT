@ECHO OFF

rem Copyright 2019 Dirk Lemmermann
rem Copyright 2019 Michael Paus
rem Copyright 2020 Christian Studer
rem Copyright 2020 VladDrakul
rem Copyright 2020 Máté Lajkó
rem
rem Licensed under the Apache License, Version 2.0 (the "License");
rem you may not use this file except in compliance with the License.
rem You may obtain a copy of the License at
rem
rem   http://www.apache.org/licenses/LICENSE-2.0
rem
rem Unless required by applicable law or agreed to in writing, software
rem distributed under the License is distributed on an "AS IS" BASIS,
rem WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
rem See the License for the specific language governing permissions and
rem limitations under the License.

rem ------ ENVIRONMENT --------------------------------------------------------
rem The script depends on various environment variables to exist in order to
rem run properly. The java version we want to use, the location of the java
rem binaries (java home), and the project version as defined inside the pom.xml
rem file, e.g. 1.0-SNAPSHOT.
rem
rem PROJECT_VERSION: version used in pom.xml, e.g. 1.0-SNAPSHOT
rem APP_VERSION: the application version, e.g. 1.0.0, shown in "about" dialog

set JAVA_VERSION=15
set MAIN_JAR=puncat-%PROJECT_VERSION%.jar

rem Set desired installer type: "app-image" "msi" "exe".
set INSTALLER_TYPE=msi

rem ------ SETUP DIRECTORIES AND FILES ----------------------------------------
rem Remove previously generated java runtime and installers. Copy all required
rem jar files into the input/libs folder.

IF EXIST target\java-runtime rmdir /S /Q  target\java-runtime
IF EXIST target\installer rmdir /S /Q target\installer

xcopy /S /Q target\libs\* target\installer\input\libs\
copy target\%MAIN_JAR% target\installer\input\libs\
xcopy /S /Q target\%PUNCAT_RESOURCES%\* target\installer\input\libs\puncat_resources\

rem ------ REQUIRED MODULES ---------------------------------------------------
rem Use jlink to detect all modules that are required to run the application.
rem Starting point for the jdep analysis is the set of jars being used by the
rem application.

echo detecting required modules

"%JAVA_HOME%\bin\jdeps" ^
  -q ^
  --multi-release %JAVA_VERSION% ^
  --ignore-missing-deps ^
  --class-path "target\installer\input\libs\*" ^
  --print-module-deps target\classes\at\ofai\punderstanding\puncat\Main.class > temp.txt

set /p detected_modules=<temp.txt

echo detected modules: %detected_modules%

rem ------ MANUAL MODULES -----------------------------------------------------
set manual_modules=jdk.localedata,jdk.management,java.naming
echo manual modules: %manual_modules%

rem ------ RUNTIME IMAGE ------------------------------------------------------
rem Use the jlink tool to create a runtime image for our application. We are
rem doing this is a separate step instead of letting jlink do the work as part
rem of the jpackage tool. This approach allows for finer configuration and also
rem works with dependencies that are not fully modularized, yet.

echo creating java runtime image

call "%JAVA_HOME%\bin\jlink" ^
  --no-header-files ^
  --strip-native-commands ^
  --no-man-pages ^
  --compress=2 ^
  --strip-debug ^
  --add-modules %detected_modules%,%manual_modules% ^
  --output target/java-runtime

IF EXIST temp.txt del temp.txt

rem ------ workaround for missing modules -------------------------------------
echo workaround for missing modules
rem xcopy /q /y %JAVA_HOME%\lib\modules target\java-runtime\lib
xcopy /S /Q /Y %JAVA_HOME%\bin\* target\java-runtime\bin
xcopy /S /Q /Y %JAVA_HOME%\conf\* target\java-runtime\conf
xcopy /S /Q /Y %JAVA_HOME%\legal\* target\java-runtime\legal
xcopy /S /Q /Y %JAVA_HOME%\lib\* target\java-runtime\lib
xcopy /S /Q /Y %JAVA_HOME%\release target\java-runtime

rem ------ PACKAGING ----------------------------------------------------------
rem In the end we will find the package inside the target/installer directory.
rem Add the --win-console flag to enable console output.

call "%JAVA_HOME%\bin\jpackage" ^
  --type %INSTALLER_TYPE% ^
  --dest target/installer ^
  --input target/installer/input/libs ^
  --name PunCAT ^
  --main-class at.ofai.punderstanding.puncat.JarLauncher ^
  --main-jar %MAIN_JAR% ^
  --runtime-image target/java-runtime ^
  --java-options -Xms1024m ^
  --java-options -Xmx2048m ^
  --java-options -Dfile.encoding=UTF-8 ^
  --icon launch4j/icon.ico ^
  --app-version %APP_VERSION% ^
  --vendor "OFAI" ^
  --copyright "Copyright © 2020 OFAI" ^
  --win-dir-chooser ^
  --win-shortcut ^
  --win-menu ^
  --win-menu-group "OFAI" ^
  --win-upgrade-uuid 0fe69768-30ac-4c2c-9354-f478128d5140
