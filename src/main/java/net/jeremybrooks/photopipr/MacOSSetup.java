package net.jeremybrooks.photopipr;

import net.jeremybrooks.photopipr.gui.WorkflowWindow;

import java.awt.Desktop;

public class MacOSSetup {

    public MacOSSetup() {
        //todo
//        Desktop.getDesktop().setAboutHandler(ae -> new AboutDialog(WorkflowWindow.getInstance()).setVisible(true));

        Desktop.getDesktop().setQuitHandler((qe, qr) -> {
            WorkflowWindow.getInstance().confirmAndExit(qr);
        });

        //todo
//        Desktop.getDesktop().setPreferencesHandler(pe -> new SettingsDialog(WorkflowWindow.getInstance()).setVisible(true));
    }
}
