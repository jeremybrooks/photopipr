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

package net.jeremybrooks.photopipr;

import net.jeremybrooks.photopipr.gui.AboutDialog;
import net.jeremybrooks.photopipr.gui.PreferencesDialog;
import net.jeremybrooks.photopipr.gui.WorkflowWindow;

import java.awt.Desktop;

/**
 * A class to set up desktop support when running on macOS.
 */
public class MacOSSetup {

    /**
     * Set up the macOS specific desktop behavior.
     *
     * <p>To set this up, call it as follows:</p>
     *
     * {@code Class.forName("net.jeremybrooks.photopipr.MacOSSetup").getDeclaredConstructor().newInstance();}
     */
    public MacOSSetup() {
        Desktop.getDesktop().setAboutHandler(ae ->
                new AboutDialog(WorkflowWindow.getInstance()).setVisible(true));

        Desktop.getDesktop().setQuitHandler((qe, qr) ->
            WorkflowWindow.getInstance().confirmAndExit(qr));

        Desktop.getDesktop().setPreferencesHandler(pe ->
                new PreferencesDialog(WorkflowWindow.getInstance()).setVisible(true));
    }
}
