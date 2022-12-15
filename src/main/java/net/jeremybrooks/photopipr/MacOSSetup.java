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
