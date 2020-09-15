package at.ofai.punderstanding.puncat.logging;

import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.message.ObjectMessage;


public class InteractionLogger {
    Logger interactionLogger;
    Logger simpleLogger;

    public InteractionLogger() {
        if (System.getProperty(LoggerValues.LOGGING_DISABLED) == null) {
            this.interactionLogger = LogManager.getLogger("json_logger");
            this.simpleLogger = LogManager.getLogger("simple_logger");

        }
    }

    public void logThis(Map<String, Object> msg) {
        if (System.getProperty(LoggerValues.LOGGING_DISABLED) == null) {
            this.interactionLogger.info(new ObjectMessage(msg));
            this.simpleLogger.info(msg);
        }
    }
}
