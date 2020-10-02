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

import org.json.JSONObject;
import org.json.JSONTokener;


public class ResourcePaths {
    private static final String resourceJsonFileName = "resourcepaths.json";
    public static Path germanet2ipa;
    public static Path wordnet2ipa;
    public static Path germanet;
    public static Path nounfreq;
    public static Path verbfreq;
    public static Path adjfreq;
    public static String resourcePath;


    public static void init() {
        boolean thisIsAJarFile = Paths.get(getCodeSourceURI()).toFile().isFile();

        var codePath = Paths.get(getCodeSourceURI());
        if (thisIsAJarFile) {
            codePath = codePath.getParent();
        }

        if (resourcePath == null) {
            resourcePath = Paths.get(codePath.toString(), resourceJsonFileName).toString();
        } else {
            if (!Paths.get(resourcePath).isAbsolute()) {
                resourcePath = Paths.get(codePath.toString(), resourcePath).toString();
            }
        }

        InputStream tokenerStream;
        try {
            tokenerStream = new FileInputStream(resourcePath);
        } catch (FileNotFoundException e) {
            throw new RuntimeException("no resourcepaths.json found at " + resourcePath, e);
        }
        var tokener = new JSONTokener(tokenerStream);
        JSONObject paths = new JSONObject(tokener);

        germanet2ipa = parsePath(paths.get("germanet2ipa").toString());
        wordnet2ipa = parsePath(paths.get("wordnet2ipa").toString());
        germanet = parsePath(paths.get("germanet").toString());
        nounfreq = parsePath(paths.get("nounfreq").toString());
        verbfreq = parsePath(paths.get("verbfreq").toString());
        adjfreq = parsePath(paths.get("adjfreq").toString());
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
        if (path.isAbsolute()) {
            absolutePath = path;
        } else {
            absolutePath = Paths.get(Paths.get(resourcePath).getParent().toString(), pathString);
        }
        if (absolutePath.toFile().exists()) {
            return absolutePath;
        } else {
            throw new RuntimeException(absolutePath + " does not exist", new FileNotFoundException());
        }
    }
}
