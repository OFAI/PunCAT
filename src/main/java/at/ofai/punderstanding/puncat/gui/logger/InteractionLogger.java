package at.ofai.punderstanding.puncat.gui.logger;

import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.message.ObjectMessage;


public class InteractionLogger {
    Logger interactionLogger;

    public InteractionLogger() {
        if (System.getProperty(LoggerValues.LOGGING_DISABLED) == null) {
            this.interactionLogger = LogManager.getLogger("json_logger");
        }
    }

    public void logThis(Map<String, Object> msg) {
        if (System.getProperty(LoggerValues.LOGGING_DISABLED) == null) {
            this.interactionLogger.info(new ObjectMessage(msg));
        }
    }
}
