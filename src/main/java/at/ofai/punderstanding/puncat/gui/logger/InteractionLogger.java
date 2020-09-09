package at.ofai.punderstanding.puncat.gui.logger;

import java.util.Arrays;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.message.ObjectMessage;


public class InteractionLogger {
    private static final Logger jsonLogger = LogManager.getLogger("json_logger");

    public static void logThis(Map<String, Object> msg) {
        jsonLogger.warn(new ObjectMessage(msg));
    }
}
