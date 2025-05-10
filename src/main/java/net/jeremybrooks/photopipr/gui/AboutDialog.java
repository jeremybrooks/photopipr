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
 * Created by JFormDesigner on Mon Sep 19 12:26:41 PDT 2022
 */

package net.jeremybrooks.photopipr.gui;

import org.apache.commons.io.IOUtils;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;
import javax.swing.border.EmptyBorder;
import java.awt.BorderLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.Window;
import java.io.Serial;
import java.nio.charset.StandardCharsets;
import java.util.ResourceBundle;

/**
 * @author Jeremy Brooks
 */
public class AboutDialog extends JDialog {
    @Serial
    private static final long serialVersionUID = -4377410026215301297L;

    public AboutDialog(Window owner) {
        super(owner);
        initComponents();
        String license;
        try {
            license = IOUtils.resourceToString("/LICENSE", StandardCharsets.UTF_8);
        } catch (Exception e) {
            license = "GNU GPL v3";
        }
        txtLicense.setText(license);
        txtLicense.setCaretPosition(1);
    }

    private void ok() {
        setVisible(false);
        dispose();
    }

    private void initComponents() {
        // JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents
        ResourceBundle bundle = ResourceBundle.getBundle("net.jeremybrooks.photopipr.about");
        dialogPane = new JPanel();
        tabbedPane1 = new JTabbedPane();
        panelAbout = new JPanel();
        scrollPane2 = new JScrollPane();
        textArea1 = new JTextArea();
        panelLicense = new JPanel();
        scrollPane1 = new JScrollPane();
        txtLicense = new JTextArea();
        buttonBar = new JPanel();
        okButton = new JButton();

        //======== this ========
        setModal(true);
        setTitle("About PhotoPipr");
        var contentPane = getContentPane();
        contentPane.setLayout(new BorderLayout());

        //======== dialogPane ========
        {
            dialogPane.setBorder(new EmptyBorder(12, 12, 12, 12));
            dialogPane.setLayout(new BorderLayout());

            //======== tabbedPane1 ========
            {

                //======== panelAbout ========
                {
                    panelAbout.setLayout(new GridLayout());

                    //======== scrollPane2 ========
                    {

                        //---- textArea1 ----
                        textArea1.setEditable(false);
                        textArea1.setWrapStyleWord(true);
                        textArea1.setLineWrap(true);
                        textArea1.setText(bundle.getString("AboutDialog.textArea1.text"));
                        scrollPane2.setViewportView(textArea1);
                    }
                    panelAbout.add(scrollPane2);
                }
                tabbedPane1.addTab(bundle.getString("AboutDialog.panelAbout.tab.title"), panelAbout);

                //======== panelLicense ========
                {
                    panelLicense.setLayout(new GridLayout());

                    //======== scrollPane1 ========
                    {

                        //---- txtLicense ----
                        txtLicense.setEditable(false);
                        txtLicense.setLineWrap(true);
                        txtLicense.setWrapStyleWord(true);
                        txtLicense.setText(bundle.getString("AboutDialog.txtLicense.text"));
                        scrollPane1.setViewportView(txtLicense);
                    }
                    panelLicense.add(scrollPane1);
                }
                tabbedPane1.addTab(bundle.getString("AboutDialog.panelLicense.tab.title"), panelLicense);
            }
            dialogPane.add(tabbedPane1, BorderLayout.CENTER);

            //======== buttonBar ========
            {
                buttonBar.setBorder(new EmptyBorder(12, 0, 0, 0));
                buttonBar.setLayout(new GridBagLayout());
                ((GridBagLayout)buttonBar.getLayout()).columnWidths = new int[] {0, 80};
                ((GridBagLayout)buttonBar.getLayout()).columnWeights = new double[] {1.0, 0.0};

                //---- okButton ----
                okButton.setText(bundle.getString("AboutDialog.okButton.text"));
                okButton.addActionListener(e -> ok());
                buttonBar.add(okButton, new GridBagConstraints(1, 0, 1, 1, 0.0, 0.0,
                    GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                    new Insets(0, 0, 0, 0), 0, 0));
            }
            dialogPane.add(buttonBar, BorderLayout.PAGE_END);
        }
        contentPane.add(dialogPane, BorderLayout.CENTER);
        setSize(600, 400);
        setLocationRelativeTo(getOwner());
        // JFormDesigner - End of component initialization  //GEN-END:initComponents
    }

    // JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables
    private JPanel dialogPane;
    private JTabbedPane tabbedPane1;
    private JPanel panelAbout;
    private JScrollPane scrollPane2;
    private JTextArea textArea1;
    private JPanel panelLicense;
    private JScrollPane scrollPane1;
    private JTextArea txtLicense;
    private JPanel buttonBar;
    private JButton okButton;
    // JFormDesigner - End of variables declaration  //GEN-END:variables
}
