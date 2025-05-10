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

/*
 * Created by JFormDesigner on Mon Sep 19 14:17:57 PDT 2022
 */

package net.jeremybrooks.photopipr.gui;

import net.jeremybrooks.photopipr.ConfigurationManager;
import net.jeremybrooks.photopipr.JinxFactory;
import net.jeremybrooks.photopipr.PPConstants;
import net.jeremybrooks.photopipr.helper.DesktopAlert;
import net.jeremybrooks.photopipr.model.Configuration;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.border.EmptyBorder;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Window;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.Serial;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.ResourceBundle;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

/**
 * @author Jeremy Brooks
 */
public class PreferencesDialog extends JDialog {
    @Serial
    private static final long serialVersionUID = 2065639447214486470L;
    private static final Logger logger = LogManager.getLogger();

    private final ResourceBundle bundle = ResourceBundle.getBundle("net.jeremybrooks.photopipr.preferences");


    public PreferencesDialog(Window owner) {
        super(owner);
        initComponents();
        Configuration config = ConfigurationManager.getConfig();
        cbxVerboseLogging.setSelected(config.isEnableVerboseLogging());
        cbxDesktopAlert.setSelected(config.isEnableDesktopAlerts());
        cbxDesktopAlerts();
    }

    private void ok() {
        Configuration config = ConfigurationManager.getConfig();
        config.setEnableVerboseLogging(cbxVerboseLogging.isSelected());
        config.setEnableDesktopAlerts(cbxDesktopAlert.isSelected());
        ConfigurationManager.saveConfiguration();
        setVisible(false);
        dispose();
    }

    private void btnDeleteToken() {
        ResourceBundle bundle = ResourceBundle.getBundle("net.jeremybrooks.photopipr.preferences");
        int option = JOptionPane.showConfirmDialog(this,
                bundle.getString("PreferencesDialog.deleteToken.message"),
                bundle.getString("PreferencesDialog.deleteToken.title"),
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE);
        if (option == JOptionPane.YES_OPTION) {
            Path token = Paths.get(PPConstants.PHOTOPIPR_HOME, PPConstants.PHOTOPIPR_AUTH_TOKEN);
            try {
                Files.delete(token);
                logger.info("Flickr auth token has been deleted.");
                System.exit(0);
            } catch (Exception e) {
                logger.warn("Error deleting Flickr token.", e);
                System.exit(1);
            }
        }
    }

    private void cbxVerboseLogging() {
        JinxFactory.getInstance().setVerboseLogging(cbxVerboseLogging.isSelected());
    }

    private void btnZipLogs() {
        File zipfile = Paths.get(System.getProperty("user.home"), "Desktop",
                        "PhotoPiprLogs_" + Instant.now().truncatedTo(ChronoUnit.SECONDS) + ".zip")
                .toFile();
        AtomicBoolean error = new AtomicBoolean(false);
        try (ZipOutputStream out = new ZipOutputStream(new FileOutputStream(zipfile))) {
            Path logDir = Paths.get(System.getProperty("user.home"), ".photopipr", "logs");
            Files.list(logDir).forEach(path -> {
                ZipEntry e = new ZipEntry("logs/" + path.getFileName().toString());
                try {
                    out.putNextEntry(e);
                    byte[] data = Files.readAllBytes(path);
                    out.write(data, 0, data.length);
                    out.closeEntry();
                } catch (IOException ioe) {
                    logger.error("Error adding a zip entry.", ioe);
                    error.set(true);
                }
            });
        } catch (Exception e) {
            logger.error("Error creating zip file.", e);
            error.set(true);
        }
        if (error.get()) {
            JOptionPane.showMessageDialog(this,
                    bundle.getString("PreferencesDialog.ziperror.message"),
                    bundle.getString("PreferencesDialog.ziperror.title"),
                    JOptionPane.ERROR_MESSAGE);
        } else {
            JOptionPane.showMessageDialog(this,
                    String.format(bundle.getString("PreferencesDialog.zipok.message"), zipfile),
                    bundle.getString("PreferencesDialog.zipok.title"),
                    JOptionPane.INFORMATION_MESSAGE);
        }

    }

    private void cbxDesktopAlerts() {
        Configuration config = ConfigurationManager.getConfig();
        config.setEnableDesktopAlerts(cbxDesktopAlert.isSelected());
        ConfigurationManager.saveConfiguration();
        btnSendTest.setEnabled(cbxDesktopAlert.isSelected());
    }

    private void btnSendTest() {
        new DesktopAlert().withMessage("This is a test message sent at " + new Date() + ".")
                .withTitle("Test Message")
                .withSubtitle("from PhotoPipr")
                .showAlert();
        JOptionPane.showMessageDialog(this,
                bundle.getString("PreferencesDialog.alertsent.message"),
                bundle.getString("PreferencesDialog.alertsent.title"),
                JOptionPane.INFORMATION_MESSAGE);
    }

   private void initComponents() {
        // JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents
        ResourceBundle bundle = ResourceBundle.getBundle("net.jeremybrooks.photopipr.preferences");
        dialogPane = new JPanel();
        buttonBar = new JPanel();
        okButton = new JButton();
        tabbedPane1 = new JTabbedPane();
        panel1 = new JPanel();
        lblUser = new JLabel();
        panel3 = new JPanel();
        btnDeleteToken = new JButton();
        panel2 = new JPanel();
        cbxVerboseLogging = new JCheckBox();
        panel4 = new JPanel();
        btnZipLogs = new JButton();
        panel5 = new JPanel();
        cbxDesktopAlert = new JCheckBox();
        btnSendTest = new JButton();

        //======== this ========
        setModal(true);
        setTitle(bundle.getString("PreferencesDialog.this.title"));
        var contentPane = getContentPane();
        contentPane.setLayout(new BorderLayout());

        //======== dialogPane ========
        {
            dialogPane.setBorder(new EmptyBorder(12, 12, 12, 12));
            dialogPane.setLayout(new BorderLayout());

            //======== buttonBar ========
            {
                buttonBar.setBorder(new EmptyBorder(12, 0, 0, 0));
                buttonBar.setLayout(new GridBagLayout());
                ((GridBagLayout)buttonBar.getLayout()).columnWidths = new int[] {0, 80};
                ((GridBagLayout)buttonBar.getLayout()).columnWeights = new double[] {1.0, 0.0};

                //---- okButton ----
                okButton.setText(bundle.getString("PreferencesDialog.okButton.text"));
                okButton.addActionListener(e -> ok());
                buttonBar.add(okButton, new GridBagConstraints(1, 0, 1, 1, 0.0, 0.0,
                    GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                    new Insets(0, 0, 0, 0), 0, 0));
            }
            dialogPane.add(buttonBar, BorderLayout.SOUTH);

            //======== tabbedPane1 ========
            {

                //======== panel1 ========
                {
                    panel1.setBorder(null);
                    panel1.setLayout(new GridBagLayout());
                    ((GridBagLayout)panel1.getLayout()).columnWidths = new int[] {0, 0};
                    ((GridBagLayout)panel1.getLayout()).rowHeights = new int[] {0, 0, 0};
                    ((GridBagLayout)panel1.getLayout()).columnWeights = new double[] {0.0, 1.0E-4};
                    ((GridBagLayout)panel1.getLayout()).rowWeights = new double[] {0.0, 0.0, 1.0E-4};

                    //---- lblUser ----
                    lblUser.setText(bundle.getString("PreferencesDialog.lblUser.text"));
                    lblUser.setText(String.format(bundle.getString("PreferencesDialog.lblUser.text"),
                    JinxFactory.getInstance().getUsername()));
                    panel1.add(lblUser, new GridBagConstraints(0, 0, 1, 1, 1.0, 0.0,
                        GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                        new Insets(5, 5, 10, 5), 0, 0));

                    //======== panel3 ========
                    {
                        panel3.setLayout(new FlowLayout(FlowLayout.RIGHT));

                        //---- btnDeleteToken ----
                        btnDeleteToken.setText(bundle.getString("PreferencesDialog.btnDeleteToken.text"));
                        btnDeleteToken.addActionListener(e -> btnDeleteToken());
                        panel3.add(btnDeleteToken);
                    }
                    panel1.add(panel3, new GridBagConstraints(0, 1, 1, 1, 0.0, 0.0,
                        GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                        new Insets(0, 0, 0, 0), 0, 0));
                }
                tabbedPane1.addTab("Flickr Authorization", panel1);

                //======== panel2 ========
                {
                    panel2.setBorder(null);
                    panel2.setLayout(new GridBagLayout());
                    ((GridBagLayout)panel2.getLayout()).columnWidths = new int[] {0, 0};
                    ((GridBagLayout)panel2.getLayout()).rowHeights = new int[] {0, 0, 0};
                    ((GridBagLayout)panel2.getLayout()).columnWeights = new double[] {0.0, 1.0E-4};
                    ((GridBagLayout)panel2.getLayout()).rowWeights = new double[] {0.0, 0.0, 1.0E-4};

                    //---- cbxVerboseLogging ----
                    cbxVerboseLogging.setText(bundle.getString("PreferencesDialog.cbxVerboseLogging.text"));
                    cbxVerboseLogging.addActionListener(e -> cbxVerboseLogging());
                    panel2.add(cbxVerboseLogging, new GridBagConstraints(0, 0, 1, 1, 1.0, 0.0,
                        GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                        new Insets(5, 5, 10, 5), 0, 0));

                    //======== panel4 ========
                    {
                        panel4.setLayout(new FlowLayout(FlowLayout.RIGHT));

                        //---- btnZipLogs ----
                        btnZipLogs.setText(bundle.getString("PreferencesDialog.btnZipLogs.text"));
                        btnZipLogs.addActionListener(e -> btnZipLogs());
                        panel4.add(btnZipLogs);
                    }
                    panel2.add(panel4, new GridBagConstraints(0, 1, 1, 1, 0.0, 0.0,
                        GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                        new Insets(0, 0, 0, 0), 0, 0));
                }
                tabbedPane1.addTab("Logging", panel2);

                //======== panel5 ========
                {
                    panel5.setLayout(new GridBagLayout());
                    ((GridBagLayout)panel5.getLayout()).columnWidths = new int[] {0, 0, 0};
                    ((GridBagLayout)panel5.getLayout()).rowHeights = new int[] {0, 0};
                    ((GridBagLayout)panel5.getLayout()).columnWeights = new double[] {0.0, 0.0, 1.0E-4};
                    ((GridBagLayout)panel5.getLayout()).rowWeights = new double[] {0.0, 1.0E-4};

                    //---- cbxDesktopAlert ----
                    cbxDesktopAlert.setText(bundle.getString("PreferencesDialog.cbxDesktopAlert.text"));
                    cbxDesktopAlert.addActionListener(e -> cbxDesktopAlerts());
                    panel5.add(cbxDesktopAlert, new GridBagConstraints(0, 0, 1, 1, 1.0, 0.0,
                        GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                        new Insets(5, 5, 5, 10), 0, 0));

                    //---- btnSendTest ----
                    btnSendTest.setText(bundle.getString("PreferencesDialog.btnSendTest.text"));
                    btnSendTest.addActionListener(e -> btnSendTest());
                    panel5.add(btnSendTest, new GridBagConstraints(1, 0, 1, 1, 0.0, 0.0,
                        GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                        new Insets(0, 0, 0, 0), 0, 0));
                }
                tabbedPane1.addTab("Alerts", panel5);
            }
            dialogPane.add(tabbedPane1, BorderLayout.NORTH);
        }
        contentPane.add(dialogPane, BorderLayout.CENTER);
        setSize(400, 300);
        setLocationRelativeTo(getOwner());
        // JFormDesigner - End of component initialization  //GEN-END:initComponents
    }

    // JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables
    private JPanel dialogPane;
    private JPanel buttonBar;
    private JButton okButton;
    private JTabbedPane tabbedPane1;
    private JPanel panel1;
    private JLabel lblUser;
    private JPanel panel3;
    private JButton btnDeleteToken;
    private JPanel panel2;
    private JCheckBox cbxVerboseLogging;
    private JPanel panel4;
    private JButton btnZipLogs;
    private JPanel panel5;
    private JCheckBox cbxDesktopAlert;
    private JButton btnSendTest;
    // JFormDesigner - End of variables declaration  //GEN-END:variables
}
