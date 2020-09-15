import at.ofai.punderstanding.puncat.logging.LoggerValues;


public class SharedConfig {
    public static void configureEnvironment() {
        System.setProperty("puncatlogfilename", "test");
        System.setProperty(LoggerValues.LOGGING_DISABLED, "true");
    }
}
