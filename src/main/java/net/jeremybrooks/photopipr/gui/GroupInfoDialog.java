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
 * Created by JFormDesigner on Fri Mar 10 09:40:05 PST 2023
 */

package net.jeremybrooks.photopipr.gui;

import net.jeremybrooks.jinx.response.groups.GroupInfo;
import net.jeremybrooks.photopipr.FlickrUtil;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JEditorPane;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.WindowConstants;
import javax.swing.border.EmptyBorder;
import javax.swing.event.HyperlinkEvent;
import java.awt.BorderLayout;
import java.awt.Desktop;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Window;
import java.io.Serial;
import java.util.ResourceBundle;

/**
 * @author jeremyb
 */
public class GroupInfoDialog extends JDialog {
    @Serial
    private static final long serialVersionUID = -3376378548722182700L;
    private static final Logger logger = LogManager.getLogger();
    private final GroupInfo groupInfo;

    public GroupInfoDialog(Window owner, GroupInfo groupInfo) {
        super(owner);
        this.groupInfo = groupInfo;
        initComponents();
        txtDescription.setText(groupInfo.getDescription().replaceAll("\n", "<br>"));
        txtDescription.setCaretPosition(0);
        lblGroupName.setText(groupInfo.getName());
        lblGroupId.setText(groupInfo.getGroupId());
    }

    private void txtDescriptionHyperlinkUpdate(HyperlinkEvent e) {
        if (e.getEventType() == HyperlinkEvent.EventType.ACTIVATED) {
            try {
                Desktop.getDesktop().browse(e.getURL().toURI());
            } catch (Exception ex) {
                logger.error("Could not open URL {}", e.getURL(), ex);
            }
        }
    }

    private void ok() {
        setVisible(false);
        dispose();
    }

    private void openBrowser() {
        try {
            Desktop.getDesktop().browse(FlickrUtil.getGroupUri(groupInfo.getGroupId()));
        } catch (Exception e) {
            logger.warn("Could not open page for groupId {}", groupInfo.getGroupId(), e);
        }
    }

    private void initComponents() {
        // JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents  @formatter:off
        ResourceBundle bundle = ResourceBundle.getBundle("net.jeremybrooks.photopipr.groupinfodialog");
        dialogPane = new JPanel();
        contentPanel = new JPanel();
        label1 = new JLabel();
        lblGroupName = new JLabel();
        label3 = new JLabel();
        lblGroupId = new JLabel();
        scrollPane1 = new JScrollPane();
        txtDescription = new JEditorPane();
        buttonBar = new JPanel();
        browserButton = new JButton();
        okButton = new JButton();

        //======== this ========
        setModal(true);
        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Group Info");
        var contentPane = getContentPane();
        contentPane.setLayout(new BorderLayout());

        //======== dialogPane ========
        {
            dialogPane.setBorder(new EmptyBorder(12, 12, 12, 12));
            dialogPane.setLayout(new BorderLayout());

            //======== contentPanel ========
            {
                contentPanel.setLayout(new GridBagLayout());
                ((GridBagLayout)contentPanel.getLayout()).columnWidths = new int[] {0, 0, 0};
                ((GridBagLayout)contentPanel.getLayout()).rowHeights = new int[] {0, 0, 0, 0};
                ((GridBagLayout)contentPanel.getLayout()).columnWeights = new double[] {0.0, 0.0, 1.0E-4};
                ((GridBagLayout)contentPanel.getLayout()).rowWeights = new double[] {0.0, 0.0, 0.0, 1.0E-4};

                //---- label1 ----
                label1.setText(bundle.getString("GroupInfoDialog.label1.text"));
                contentPanel.add(label1, new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0,
                    GridBagConstraints.EAST, GridBagConstraints.VERTICAL,
                    new Insets(0, 0, 5, 5), 0, 0));

                //---- lblGroupName ----
                lblGroupName.setText(bundle.getString("GroupInfoDialog.lblGroupName.text"));
                contentPanel.add(lblGroupName, new GridBagConstraints(1, 0, 1, 1, 0.0, 0.0,
                    GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                    new Insets(0, 0, 5, 0), 0, 0));

                //---- label3 ----
                label3.setText(bundle.getString("GroupInfoDialog.label3.text"));
                contentPanel.add(label3, new GridBagConstraints(0, 1, 1, 1, 0.0, 0.0,
                    GridBagConstraints.EAST, GridBagConstraints.VERTICAL,
                    new Insets(0, 0, 5, 5), 0, 0));

                //---- lblGroupId ----
                lblGroupId.setText(bundle.getString("GroupInfoDialog.lblGroupId.text"));
                contentPanel.add(lblGroupId, new GridBagConstraints(1, 1, 1, 1, 0.0, 0.0,
                    GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                    new Insets(0, 0, 5, 0), 0, 0));

                //======== scrollPane1 ========
                {

                    //---- txtDescription ----
                    txtDescription.setContentType("text/html");
                    txtDescription.setEditable(false);
                    txtDescription.addHyperlinkListener(e -> txtDescriptionHyperlinkUpdate(e));
                    scrollPane1.setViewportView(txtDescription);
                }
                contentPanel.add(scrollPane1, new GridBagConstraints(0, 2, 2, 1, 1.0, 1.0,
                    GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                    new Insets(0, 0, 0, 0), 0, 0));
            }
            dialogPane.add(contentPanel, BorderLayout.CENTER);

            //======== buttonBar ========
            {
                buttonBar.setBorder(new EmptyBorder(12, 0, 0, 0));
                buttonBar.setLayout(new GridBagLayout());
                ((GridBagLayout)buttonBar.getLayout()).columnWidths = new int[] {0, 0, 80};
                ((GridBagLayout)buttonBar.getLayout()).columnWeights = new double[] {1.0, 0.0, 0.0};

                //---- browserButton ----
                browserButton.setText(bundle.getString("GroupInfoDialog.browserButton.text"));
                browserButton.addActionListener(e -> openBrowser());
                buttonBar.add(browserButton, new GridBagConstraints(1, 0, 1, 1, 0.0, 0.0,
                    GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                    new Insets(0, 0, 0, 5), 0, 0));

                //---- okButton ----
                okButton.setText(bundle.getString("GroupInfoDialog.okButton.text"));
                okButton.addActionListener(e -> ok());
                buttonBar.add(okButton, new GridBagConstraints(2, 0, 1, 1, 0.0, 0.0,
                    GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                    new Insets(0, 0, 0, 0), 0, 0));
            }
            dialogPane.add(buttonBar, BorderLayout.SOUTH);
        }
        contentPane.add(dialogPane, BorderLayout.CENTER);
        setSize(500, 300);
        setLocationRelativeTo(getOwner());
        // JFormDesigner - End of component initialization  //GEN-END:initComponents  @formatter:on
    }


    // JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables  @formatter:off
    private JPanel dialogPane;
    private JPanel contentPanel;
    private JLabel label1;
    private JLabel lblGroupName;
    private JLabel label3;
    private JLabel lblGroupId;
    private JScrollPane scrollPane1;
    private JEditorPane txtDescription;
    private JPanel buttonBar;
    private JButton browserButton;
    private JButton okButton;
    // JFormDesigner - End of variables declaration  //GEN-END:variables  @formatter:on
}
