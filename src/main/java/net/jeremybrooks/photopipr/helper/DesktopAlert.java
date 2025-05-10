/*
 *  PhotoPipr is Copyright 2017-2025 by Jeremy Brooks
 *
 *  This file is part of PhotoPipr.
 *
 *   PhotoPipr is free software: you can redistribute it and/or modify
 *   it under the terms of the GNU General Public License as published by
 *   the Free Software Foundation, either version 3 of the License, or
 *   (at your option) any later version.
 *
 *   PhotoPipr is distributed in the hope that it will be useful,
 *   but WITHOUT ANY WARRANTY; without even the implied warranty of
 *   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *   GNU General Public License for more details.
 *
 *   You should have received a copy of the GNU General Public License
 *   along with PhotoPipr.  If not, see <http://www.gnu.org/licenses/>.
 */

package net.jeremybrooks.photopipr.helper;

import net.jeremybrooks.photopipr.ConfigurationManager;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;

/**
 * Show a desktop alert on macOS systems.
 *
 * <p>This class uses osascript to display a notification on the desktop. If the user is
 * on a non-mac OS, calling the methods in this class will have no effect.</p>
 *
 * <p>To use this class:</p>
 * <pre>
 *     {@code
 *     new DesktopAlert()
 *         .withMessage("This is a test message sent at " + new Date() + ".")
 *         .withTitle("Test Message")
 *         .withSubtitle("from PhotoPipr")
 *         .showAlert();
 *     }
 * </pre>
 */
public class DesktopAlert {
    private static final Logger logger = LogManager.getLogger();
    private String message;
    private String title;
    private String subtitle;

    /**
     * Specify the message to display.
     *
     * @param message the message text to display.
     */
    public DesktopAlert withMessage(String message) {
        this.message = message;
        return this;
    }

    /**
     * Specify the title to display.
     *
     * @param title the title text to display.
     * @return this instance of DesktopAlert for chaining.
     */
    public DesktopAlert withTitle(String title) {
        this.title = title;
        return this;
    }

    /**
     * Specify the subtitle to display.
     *
     * @param subtitle the subtitle text to display.
     * @return this instance of DesktopAlert for chaining.
     */
    public DesktopAlert withSubtitle(String subtitle) {
        this.subtitle = subtitle;
        return this;
    }

    /**
     * Show the desktop alert.
     *
     * <p>If running on macOS, and if the user has enabled desktop alerts, this
     * method will exec the osascript utility to display a desktop alert with the
     * desired message.</p>
     */
    public void showAlert() {
        if (System.getProperty("os.name").toLowerCase().contains("mac")) {
            if (ConfigurationManager.getConfig().isEnableDesktopAlerts()) {
                String cmd = String.format("display notification \"%s\" with title \"%s\" subtitle \"%s\"",
                        message, title, subtitle);
                try {
                    Runtime.getRuntime().exec(new String[]{"osascript", "-e", cmd});
                } catch (IOException ioe) {
                    logger.warn("Error showing alert for message {}, title {}, subtitle {}",
                            message, title, subtitle, ioe);
                }
            }
        }
    }
}
