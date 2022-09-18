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
 * Created by JFormDesigner on Thu Apr 02 20:48:47 PDT 2020
 */

package net.jeremybrooks.photopipr.gui;

import net.jeremybrooks.photopipr.model.FinishAction;

import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.WindowConstants;
import javax.swing.border.EmptyBorder;
import java.awt.BorderLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.io.Serial;
import java.util.ResourceBundle;

import static net.jeremybrooks.photopipr.PPConstants.*;

/**
 * @author Jeremy Brooks
 */
public class FinishActionDialog extends JDialog {
  @Serial
  private static final long serialVersionUID = 5682503740928275811L;

  private final FinishAction action;
  private final WorkflowWindow workflowWindow;

  public FinishActionDialog(WorkflowWindow owner, FinishAction action) {
    super(owner);
    this.workflowWindow = owner;
    this.action = action;
    initComponents();
    radioExit.setSelected(action.getFinishMode().equals(FINISH_ACTION_EXIT));
    radioRepeat.setSelected(action.getFinishMode().equals(FINISH_ACTION_REPEAT));
    radioStop.setSelected(action.getFinishMode().equals(FINISH_ACTION_STOP));
  }

  private void cancelButtonActionPerformed(ActionEvent e) {
    setVisible(false);
    dispose();
  }

  private void okButtonActionPerformed(ActionEvent e) {
      if (radioExit.isSelected()) {
          action.setFinishMode(FINISH_ACTION_EXIT);
      } else if (radioRepeat.isSelected()) {
          action.setFinishMode(FINISH_ACTION_REPEAT);
      } else {
          action.setFinishMode(FINISH_ACTION_STOP);
      }
      workflowWindow.saveWorkflows();
      setVisible(false);
      dispose();
  }

  private void initComponents() {
    // JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents
    ResourceBundle bundle = ResourceBundle.getBundle("net.jeremybrooks.photopipr.finish");
    dialogPane = new JPanel();
    contentPanel = new JPanel();
    label1 = new JLabel();
    panel1 = new JPanel();
    radioStop = new JRadioButton();
    radioRepeat = new JRadioButton();
    radioExit = new JRadioButton();
    buttonBar = new JPanel();
    okButton = new JButton();
    cancelButton = new JButton();

    //======== this ========
    setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
    setTitle(bundle.getString("FinishActionDialog.this.title"));
    setModal(true);
    var contentPane = getContentPane();
    contentPane.setLayout(new BorderLayout());

    //======== dialogPane ========
    {
        dialogPane.setBorder(new EmptyBorder(12, 12, 12, 12));
        dialogPane.setLayout(new BorderLayout());

        //======== contentPanel ========
        {
            contentPanel.setLayout(new BorderLayout());

            //---- label1 ----
            label1.setText(bundle.getString("FinishActionDialog.label1.text"));
            contentPanel.add(label1, BorderLayout.NORTH);

            //======== panel1 ========
            {
                panel1.setLayout(new GridBagLayout());
                ((GridBagLayout)panel1.getLayout()).columnWidths = new int[] {0, 0};
                ((GridBagLayout)panel1.getLayout()).rowHeights = new int[] {0, 0, 0, 0};
                ((GridBagLayout)panel1.getLayout()).columnWeights = new double[] {1.0, 1.0E-4};
                ((GridBagLayout)panel1.getLayout()).rowWeights = new double[] {0.0, 0.0, 0.0, 1.0E-4};

                //---- radioStop ----
                radioStop.setText(bundle.getString("FinishActionDialog.radioStop.text"));
                radioStop.setSelected(true);
                panel1.add(radioStop, new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0,
                    GridBagConstraints.WEST, GridBagConstraints.VERTICAL,
                    new Insets(0, 5, 0, 0), 0, 5));

                //---- radioRepeat ----
                radioRepeat.setText(bundle.getString("FinishActionDialog.radioRepeat.text"));
                panel1.add(radioRepeat, new GridBagConstraints(0, 1, 1, 1, 0.0, 0.0,
                    GridBagConstraints.WEST, GridBagConstraints.VERTICAL,
                    new Insets(0, 5, 0, 0), 0, 5));

                //---- radioExit ----
                radioExit.setText(bundle.getString("FinishActionDialog.radioExit.text"));
                panel1.add(radioExit, new GridBagConstraints(0, 2, 1, 1, 0.0, 0.0,
                    GridBagConstraints.WEST, GridBagConstraints.VERTICAL,
                    new Insets(0, 5, 0, 0), 0, 5));
            }
            contentPanel.add(panel1, BorderLayout.CENTER);
        }
        dialogPane.add(contentPanel, BorderLayout.CENTER);

        //======== buttonBar ========
        {
            buttonBar.setBorder(new EmptyBorder(12, 0, 0, 0));
            buttonBar.setLayout(new GridBagLayout());
            ((GridBagLayout)buttonBar.getLayout()).columnWidths = new int[] {0, 85, 80};
            ((GridBagLayout)buttonBar.getLayout()).columnWeights = new double[] {1.0, 0.0, 0.0};

            //---- okButton ----
            okButton.setText(bundle.getString("FinishActionDialog.okButton.text"));
            okButton.addActionListener(e -> okButtonActionPerformed(e));
            buttonBar.add(okButton, new GridBagConstraints(1, 0, 1, 1, 0.0, 0.0,
                GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                new Insets(0, 0, 0, 5), 0, 0));

            //---- cancelButton ----
            cancelButton.setText(bundle.getString("FinishActionDialog.cancelButton.text"));
            cancelButton.addActionListener(e -> cancelButtonActionPerformed(e));
            buttonBar.add(cancelButton, new GridBagConstraints(2, 0, 1, 1, 0.0, 0.0,
                GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                new Insets(0, 0, 0, 0), 0, 0));
        }
        dialogPane.add(buttonBar, BorderLayout.SOUTH);
    }
    contentPane.add(dialogPane, BorderLayout.CENTER);
    setSize(400, 300);
    setLocationRelativeTo(getOwner());

    //---- buttonGroup1 ----
    var buttonGroup1 = new ButtonGroup();
    buttonGroup1.add(radioStop);
    buttonGroup1.add(radioRepeat);
    buttonGroup1.add(radioExit);
    // JFormDesigner - End of component initialization  //GEN-END:initComponents
  }

  // JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables
  private JPanel dialogPane;
  private JPanel contentPanel;
  private JLabel label1;
  private JPanel panel1;
  private JRadioButton radioStop;
  private JRadioButton radioRepeat;
  private JRadioButton radioExit;
  private JPanel buttonBar;
  private JButton okButton;
  private JButton cancelButton;
  // JFormDesigner - End of variables declaration  //GEN-END:variables
}
