package at.ofai.punderstanding.puncat.logic;

import java.net.URISyntaxException;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.json.JSONObject;
import org.json.JSONTokener;


public class ResourcePaths {
    public static Path germanet2ipaPath;
    public static Path wordnet2ipaPath;

    public static Path germaNetLocation;
    public static Path nounFreq;
    public static Path verbFreq;
    public static Path adjFreq;

    static {
        JSONTokener tokener;
        if (ResourcePaths.class.getProtectionDomain().getCodeSource().getLocation().toString().endsWith(".jar")) {
            tokener = new JSONTokener(ResourcePaths.class.getResourceAsStream("/resourcepaths_jar.json"));
        } else {
            tokener = new JSONTokener(ResourcePaths.class.getResourceAsStream("/resourcepaths.json"));
        }
        JSONObject paths = new JSONObject(tokener);

        germanet2ipaPath = parsePath(paths.get("germanet2ipaPath").toString());
        wordnet2ipaPath = parsePath(paths.get("wordnet2ipaPath").toString());
        germaNetLocation = parsePath(paths.get("germaNetLocation").toString());
        nounFreq = parsePath(paths.get("nounFreq").toString());
        verbFreq = parsePath(paths.get("verbFreq").toString());
        adjFreq = parsePath(paths.get("adjFreq").toString());
    }

    private static Path parsePath(String pathString) {
        var path = Paths.get(pathString);
        if (!path.isAbsolute()) {
            try {
                return Paths.get(ResourcePaths.class.getResource(pathString).toURI());
            } catch (URISyntaxException e) {
                throw new RuntimeException(e);
            }

        } else return path;
    }
}
