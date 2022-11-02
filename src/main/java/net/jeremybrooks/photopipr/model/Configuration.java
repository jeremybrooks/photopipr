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

package net.jeremybrooks.photopipr.model;

public class Configuration {
    private int windowPositionX = 100;
    private int windowPositionY = 100;
    private int windowWidth = 600;
    private int windowHeight = 400;
    private boolean enableVerboseLogging = false;
    private boolean enableDesktopAlerts = false;

    public int getWindowPositionX() {
        return windowPositionX;
    }

    public void setWindowPositionX(int windowPositionX) {
        this.windowPositionX = windowPositionX;
    }

    public int getWindowPositionY() {
        return windowPositionY;
    }

    public void setWindowPositionY(int windowPositionY) {
        this.windowPositionY = windowPositionY;
    }

    public int getWindowWidth() {
        return windowWidth;
    }

    public void setWindowWidth(int windowWidth) {
        this.windowWidth = windowWidth;
    }

    public int getWindowHeight() {
        return windowHeight;
    }

    public void setWindowHeight(int windowHeight) {
        this.windowHeight = windowHeight;
    }

    public boolean isEnableVerboseLogging() {
        return enableVerboseLogging;
    }

    public void setEnableVerboseLogging(boolean enableVerboseLogging) {
        this.enableVerboseLogging = enableVerboseLogging;
    }

    public boolean isEnableDesktopAlerts() {
        return enableDesktopAlerts;
    }

    public void setEnableDesktopAlerts(boolean enableDesktopAlerts) {
        this.enableDesktopAlerts = enableDesktopAlerts;
    }

}
