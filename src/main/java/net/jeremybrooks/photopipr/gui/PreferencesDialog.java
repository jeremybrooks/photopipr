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

/*
 * Created by JFormDesigner on Mon Sep 19 14:17:57 PDT 2022
 */

package net.jeremybrooks.photopipr.gui;

import net.jeremybrooks.photopipr.ConfigurationManager;
import net.jeremybrooks.photopipr.JinxFactory;
import net.jeremybrooks.photopipr.PPConstants;
import net.jeremybrooks.photopipr.model.Configuration;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import java.awt.BorderLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Window;
import java.io.Serial;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ResourceBundle;

/**
 * @author Jeremy Brooks
 */
public class PreferencesDialog extends JDialog {
    @Serial
    private static final long serialVersionUID = 2065639447214486470L;
    private static final Logger logger = LogManager.getLogger();


    public PreferencesDialog(Window owner) {
        super(owner);
        initComponents();
        Configuration config = ConfigurationManager.getConfig();
        cbxVerboseLogging.setSelected(config.isEnableVerboseLogging());
    }

    private void ok() {
        ConfigurationManager.getConfig().setEnableVerboseLogging(cbxVerboseLogging.isSelected());
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

    private void initComponents() {
        // JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents
        ResourceBundle bundle = ResourceBundle.getBundle("net.jeremybrooks.photopipr.preferences");
        dialogPane = new JPanel();
        contentPanel = new JPanel();
        cbxVerboseLogging = new JCheckBox();
        lblUser = new JLabel();
        btnDeleteToken = new JButton();
        buttonBar = new JPanel();
        okButton = new JButton();

        //======== this ========
        setModal(true);
        setTitle(bundle.getString("PreferencesDialog.this.title"));
        var contentPane = getContentPane();
        contentPane.setLayout(new BorderLayout());

        //======== dialogPane ========
        {
            dialogPane.setBorder(new EmptyBorder(12, 12, 12, 12));
            dialogPane.setLayout(new BorderLayout());

            //======== contentPanel ========
            {
                contentPanel.setLayout(new GridBagLayout());
                ((GridBagLayout)contentPanel.getLayout()).columnWidths = new int[] {0, 0};
                ((GridBagLayout)contentPanel.getLayout()).rowHeights = new int[] {0, 0, 0, 0};
                ((GridBagLayout)contentPanel.getLayout()).columnWeights = new double[] {0.0, 1.0E-4};
                ((GridBagLayout)contentPanel.getLayout()).rowWeights = new double[] {0.0, 0.0, 0.0, 1.0E-4};

                //---- cbxVerboseLogging ----
                cbxVerboseLogging.setText(bundle.getString("PreferencesDialog.cbxVerboseLogging.text"));
                cbxVerboseLogging.addActionListener(e -> cbxVerboseLogging());
                contentPanel.add(cbxVerboseLogging, new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0,
                    GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                    new Insets(0, 0, 5, 0), 0, 0));

                //---- lblUser ----
                lblUser.setText(bundle.getString("PreferencesDialog.lblUser.text"));
                lblUser.setText(String.format(bundle.getString("PreferencesDialog.lblUser.text"),
                JinxFactory.getInstance().getUsername()));
                contentPanel.add(lblUser, new GridBagConstraints(0, 1, 1, 1, 0.0, 0.0,
                    GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                    new Insets(0, 0, 5, 0), 0, 0));

                //---- btnDeleteToken ----
                btnDeleteToken.setText(bundle.getString("PreferencesDialog.btnDeleteToken.text"));
                btnDeleteToken.addActionListener(e -> btnDeleteToken());
                contentPanel.add(btnDeleteToken, new GridBagConstraints(0, 2, 1, 1, 0.0, 0.0,
                    GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                    new Insets(0, 0, 0, 0), 0, 0));
            }
            dialogPane.add(contentPanel, BorderLayout.CENTER);

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
        }
        contentPane.add(dialogPane, BorderLayout.CENTER);
        pack();
        setLocationRelativeTo(getOwner());
        // JFormDesigner - End of component initialization  //GEN-END:initComponents
    }

    // JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables
    private JPanel dialogPane;
    private JPanel contentPanel;
    private JCheckBox cbxVerboseLogging;
    private JLabel lblUser;
    private JButton btnDeleteToken;
    private JPanel buttonBar;
    private JButton okButton;
    // JFormDesigner - End of variables declaration  //GEN-END:variables
}
