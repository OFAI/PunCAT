/*
  Copyright 2020 Máté Lajkó

  This file is part of PunCAT.

  PunCAT is free software: you can redistribute it and/or modify it
  under the terms of the GNU General Public License as published by
  the Free Software Foundation, either version 3 of the License, or
  (at your option) any later version.

  PunCAT is distributed in the hope that it will be useful, but
  WITHOUT ANY WARRANTY; without even the implied warranty of
  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
  General Public License for more details.

  You should have received a copy of the GNU General Public License
  along with PunCAT.  If not, see <https://www.gnu.org/licenses/>.
 */

package at.ofai.punderstanding.puncat.logic;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;


public class ResourcePaths {
    private static final String resourceJsonFileName = "resourcepaths.json";
    public static Path germanet2ipaPath;
    public static Path wordnet2ipaPath;
    public static Path germaNetLocation;
    public static Path nounFreq;
    public static Path verbFreq;
    public static Path adjFreq;
    public static String resourcePath;
    private static boolean thisIsAJarFile;


    public static void init() {
        thisIsAJarFile = getCodeSourceURI().toString().endsWith(".jar");

        if (resourcePath == null) {
            Path codePath = Paths.get(getCodeSourceURI());
            if (thisIsAJarFile) {
                codePath = codePath.getParent();
            }
            resourcePath = Paths.get(codePath.toString(), resourceJsonFileName).toString();
        }

        InputStream tokenerStream;
        try {
            tokenerStream = new FileInputStream(resourcePath);
        } catch (FileNotFoundException e) {
            throw new RuntimeException("no resourcepaths.json found at " + resourcePath, e);
        }
        var tokener = new JSONTokener(tokenerStream);
        JSONObject jsonFile = new JSONObject(tokener);
        JSONObject paths;
        if (thisIsAJarFile) {
            try {
                paths = jsonFile.getJSONObject("jar");
            } catch (JSONException e) {
                throw new RuntimeException(e);
            }
        } else {
            try {
                paths = jsonFile.getJSONObject("normal");
            } catch (JSONException e) {
                throw new RuntimeException(e);
            }

        }
        germanet2ipaPath = parsePath(paths.get("germanet2ipaPath").toString());
        wordnet2ipaPath = parsePath(paths.get("wordnet2ipaPath").toString());
        germaNetLocation = parsePath(paths.get("germaNetLocation").toString());
        nounFreq = parsePath(paths.get("nounFreq").toString());
        verbFreq = parsePath(paths.get("verbFreq").toString());
        adjFreq = parsePath(paths.get("adjFreq").toString());
    }

    private static URI getCodeSourceURI() {
        try {
            return ResourcePaths.class.getProtectionDomain().getCodeSource().getLocation().toURI();
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }

    private static Path parsePath(String pathString) {
        var path = Paths.get(pathString);
        Path absolutePath;
        if (!path.isAbsolute()) {
            Path codePath;
            if (thisIsAJarFile) {
                codePath = Paths.get(getCodeSourceURI()).getParent();
            } else {
                codePath = Paths.get(getCodeSourceURI());
            }
            absolutePath = Paths.get(codePath.toString(), pathString);
        } else {
            absolutePath = path;
        }
        if (absolutePath.toFile().exists()) {
            return absolutePath;
        } else {
            throw new RuntimeException(absolutePath + " does not exist", new FileNotFoundException());
        }
    }
}
