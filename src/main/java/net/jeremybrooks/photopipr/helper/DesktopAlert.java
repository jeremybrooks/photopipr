/*
 *  PhotoPipr is Copyright 2017-2022 by Jeremy Brooks
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

public class DesktopAlert {
    private static final Logger logger = LogManager.getLogger();
    private String message;
    private String title;
    private String subtitle;

    public DesktopAlert withMessage(String message) {
        this.message = message;
        return this;
    }

    public DesktopAlert withTitle(String title) {
        this.title = title;
        return this;
    }

    public DesktopAlert withSubtitle(String subtitle) {
        this.subtitle = subtitle;
        return this;
    }

    public void showAlert() {
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
