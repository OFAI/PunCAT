package at.ofai.punderstanding.puncat.logic;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.net.URISyntaxException;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;


public class ResourcePaths {
    public static Path germanet2ipaPath;
    public static Path wordnet2ipaPath;

    public static Path germaNetLocation;
    public static Path nounFreq;
    public static Path verbFreq;
    public static Path adjFreq;

    public static String resourceJson = "/resourcepaths.json";


    public static void init() {
        JSONTokener tokener;
        try {
            if (Paths.get(resourceJson).isAbsolute()) {
                tokener = new JSONTokener(new FileInputStream(resourceJson));
            } else {
                tokener = new JSONTokener(ResourcePaths.class.getResourceAsStream(resourceJson));
            }
        } catch (NullPointerException | FileNotFoundException e) {
            throw new RuntimeException(resourceJson + " not found");
        }

        JSONObject jsonFile = new JSONObject(tokener);
        JSONObject paths;
        if (ResourcePaths.class.getProtectionDomain().getCodeSource().getLocation().toString().endsWith(".jar")) {
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

    private static Path parsePath(String pathString) {
        var path = Paths.get(pathString);
        Path absolutePath;
        if (!path.isAbsolute()) {
            try {
                absolutePath = Paths.get(ResourcePaths.class.getResource(pathString).toURI());
            } catch (URISyntaxException e) {
                throw new RuntimeException(e);
            }
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
