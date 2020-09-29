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
