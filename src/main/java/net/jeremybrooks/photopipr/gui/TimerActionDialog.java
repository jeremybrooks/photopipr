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
 * Created by JFormDesigner on Wed Aug 31 17:09:09 PDT 2022
 */

package net.jeremybrooks.photopipr.gui;

import net.jeremybrooks.photopipr.PPConstants;
import net.jeremybrooks.photopipr.model.TimerAction;

import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;
import javax.swing.WindowConstants;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import java.awt.BorderLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.io.Serial;
import java.util.Arrays;
import java.util.ResourceBundle;

/**
 * @author Jeremy Brooks
 */
public class TimerActionDialog extends JDialog {

    @Serial
    private static final long serialVersionUID = 6237958304511495668L;
    private final WorkflowWindow workflowWindow;
    private final TimerAction timerAction;
    public TimerActionDialog(WorkflowWindow owner, TimerAction timerAction) {
        super(owner, true);
        this.workflowWindow = owner;
        this.timerAction = timerAction;
        initComponents();

        if (timerAction.getTimerMode().equals(PPConstants.TimerMode.TIMER.name())) {
            radioTimer.setSelected(true);
            hourSpinner.setValue(timerAction.getHours());
            minuteSpinner.setValue(timerAction.getMinutes());
            secondSpinner.setValue(timerAction.getSeconds());
        } else {
            radioAlarm.setSelected(true);
            hourSpinnerAlarm.setValue(timerAction.getHours());
            minuteSpinnerAlarm.setValue(timerAction.getMinutes());
        }
        updateSpinners();
    }

    private void cancel() {
        dispose();
    }

    private void ok() {
        if (radioTimer.isSelected()) {
            timerAction.setTimerMode(PPConstants.TimerMode.TIMER.name());
            timerAction.setHours((Integer) hourSpinner.getValue());
            timerAction.setMinutes((Integer) minuteSpinner.getValue());
            timerAction.setSeconds((Integer) secondSpinner.getValue());
        } else {
            timerAction.setTimerMode(PPConstants.TimerMode.ALARM.name());
            timerAction.setHours((Integer) hourSpinnerAlarm.getValue());
            timerAction.setMinutes((Integer) minuteSpinnerAlarm.getValue());
            timerAction.setSeconds(0);
        }
        workflowWindow.saveWorkflows();
        this.setVisible(false);
        this.dispose();
    }

    private void radioTimer() {
       updateSpinners();
    }

    private void radioAlarm() {
        updateSpinners();
    }

    private void updateSpinners() {
        Arrays.stream(timerPanel.getComponents()).forEach(c -> {
            if (!(c instanceof JRadioButton)) {
                c.setEnabled(radioTimer.isSelected());
            }
        });
        Arrays.stream(alarmPanel.getComponents()).forEach(c -> {
            if (!(c instanceof JRadioButton)) {
                c.setEnabled(radioAlarm.isSelected());
            }
        });
    }







    private void initComponents() {
        // JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents
        ResourceBundle bundle = ResourceBundle.getBundle("net.jeremybrooks.photopipr.timeraction");
        dialogPane = new JPanel();
        contentPanel = new JPanel();
        timerPanel = new JPanel();
        radioTimer = new JRadioButton();
        hourSpinner = new JSpinner();
        label4 = new JLabel();
        minuteSpinner = new JSpinner();
        label5 = new JLabel();
        secondSpinner = new JSpinner();
        label2 = new JLabel();
        alarmPanel = new JPanel();
        radioAlarm = new JRadioButton();
        hourSpinnerAlarm = new JSpinner();
        label1 = new JLabel();
        minuteSpinnerAlarm = new JSpinner();
        label3 = new JLabel();
        buttonBar = new JPanel();
        okButton = new JButton();
        cancelButton = new JButton();

        //======== this ========
        setModal(true);
        setTitle("Timer Action");
        setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
        var contentPane = getContentPane();
        contentPane.setLayout(new BorderLayout());

        //======== dialogPane ========
        {
            dialogPane.setBorder(new EmptyBorder(12, 12, 12, 12));
            dialogPane.setLayout(new BorderLayout());

            //======== contentPanel ========
            {
                contentPanel.setLayout(new GridLayout(2, 0));

                //======== timerPanel ========
                {
                    timerPanel.setBorder(new TitledBorder("Timer"));
                    timerPanel.setLayout(new GridBagLayout());
                    ((GridBagLayout)timerPanel.getLayout()).columnWidths = new int[] {0, 0, 0, 0, 0, 0, 0};
                    ((GridBagLayout)timerPanel.getLayout()).rowHeights = new int[] {0, 0, 0};
                    ((GridBagLayout)timerPanel.getLayout()).columnWeights = new double[] {0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 1.0E-4};
                    ((GridBagLayout)timerPanel.getLayout()).rowWeights = new double[] {0.0, 0.0, 1.0E-4};

                    //---- radioTimer ----
                    radioTimer.setText(bundle.getString("TimerActionDialog.radioTimer.text"));
                    radioTimer.setSelected(true);
                    radioTimer.addActionListener(e -> radioTimer());
                    timerPanel.add(radioTimer, new GridBagConstraints(0, 0, 6, 1, 0.0, 0.0,
                        GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                        new Insets(0, 0, 5, 0), 0, 0));

                    //---- hourSpinner ----
                    hourSpinner.setModel(new SpinnerNumberModel(0, 0, 23, 1));
                    timerPanel.add(hourSpinner, new GridBagConstraints(0, 1, 1, 1, 0.0, 0.0,
                        GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                        new Insets(0, 0, 0, 5), 0, 0));

                    //---- label4 ----
                    label4.setText(bundle.getString("TimerActionDialog.label4.text"));
                    timerPanel.add(label4, new GridBagConstraints(1, 1, 1, 1, 0.0, 0.0,
                        GridBagConstraints.CENTER, GridBagConstraints.VERTICAL,
                        new Insets(0, 0, 0, 5), 0, 0));

                    //---- minuteSpinner ----
                    minuteSpinner.setModel(new SpinnerNumberModel(0, 0, 59, 1));
                    timerPanel.add(minuteSpinner, new GridBagConstraints(2, 1, 1, 1, 0.0, 0.0,
                        GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                        new Insets(0, 0, 0, 5), 0, 0));

                    //---- label5 ----
                    label5.setText(bundle.getString("TimerActionDialog.label5.text"));
                    timerPanel.add(label5, new GridBagConstraints(3, 1, 1, 1, 0.0, 0.0,
                        GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                        new Insets(0, 0, 0, 5), 0, 0));

                    //---- secondSpinner ----
                    secondSpinner.setModel(new SpinnerNumberModel(0, 0, 59, 1));
                    timerPanel.add(secondSpinner, new GridBagConstraints(4, 1, 1, 1, 0.0, 0.0,
                        GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                        new Insets(0, 0, 0, 5), 0, 0));

                    //---- label2 ----
                    label2.setText(bundle.getString("TimerActionDialog.label2.text"));
                    timerPanel.add(label2, new GridBagConstraints(5, 1, 1, 1, 1.0, 0.0,
                        GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                        new Insets(0, 0, 0, 0), 0, 0));
                }
                contentPanel.add(timerPanel);

                //======== alarmPanel ========
                {
                    alarmPanel.setBorder(new TitledBorder(bundle.getString("TimerActionDialog.alarmPanel.border")));
                    alarmPanel.setLayout(new GridBagLayout());
                    ((GridBagLayout)alarmPanel.getLayout()).columnWidths = new int[] {0, 0, 0, 0, 0};
                    ((GridBagLayout)alarmPanel.getLayout()).rowHeights = new int[] {0, 0, 0};
                    ((GridBagLayout)alarmPanel.getLayout()).columnWeights = new double[] {0.0, 0.0, 0.0, 0.0, 1.0E-4};
                    ((GridBagLayout)alarmPanel.getLayout()).rowWeights = new double[] {0.0, 0.0, 1.0E-4};

                    //---- radioAlarm ----
                    radioAlarm.setText(bundle.getString("TimerActionDialog.radioAlarm.text"));
                    radioAlarm.addActionListener(e -> radioAlarm());
                    alarmPanel.add(radioAlarm, new GridBagConstraints(0, 0, 3, 1, 0.0, 0.0,
                        GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                        new Insets(0, 0, 5, 5), 0, 0));

                    //---- hourSpinnerAlarm ----
                    hourSpinnerAlarm.setModel(new SpinnerNumberModel(0, 0, 23, 1));
                    alarmPanel.add(hourSpinnerAlarm, new GridBagConstraints(0, 1, 1, 1, 0.0, 0.0,
                        GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                        new Insets(0, 0, 0, 5), 0, 0));

                    //---- label1 ----
                    label1.setText(bundle.getString("TimerActionDialog.label1.text"));
                    alarmPanel.add(label1, new GridBagConstraints(1, 1, 1, 1, 0.0, 0.0,
                        GridBagConstraints.CENTER, GridBagConstraints.VERTICAL,
                        new Insets(0, 0, 0, 5), 0, 0));

                    //---- minuteSpinnerAlarm ----
                    minuteSpinnerAlarm.setModel(new SpinnerNumberModel(0, 0, 59, 1));
                    alarmPanel.add(minuteSpinnerAlarm, new GridBagConstraints(2, 1, 1, 1, 0.0, 0.0,
                        GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                        new Insets(0, 0, 0, 5), 0, 0));

                    //---- label3 ----
                    label3.setText(bundle.getString("TimerActionDialog.label3.text"));
                    alarmPanel.add(label3, new GridBagConstraints(3, 1, 1, 1, 1.0, 0.0,
                        GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                        new Insets(0, 0, 0, 0), 0, 0));
                }
                contentPanel.add(alarmPanel);
            }
            dialogPane.add(contentPanel, BorderLayout.CENTER);

            //======== buttonBar ========
            {
                buttonBar.setBorder(new EmptyBorder(12, 0, 0, 0));
                buttonBar.setLayout(new GridBagLayout());
                ((GridBagLayout)buttonBar.getLayout()).columnWidths = new int[] {0, 85, 80};
                ((GridBagLayout)buttonBar.getLayout()).columnWeights = new double[] {1.0, 0.0, 0.0};

                //---- okButton ----
                okButton.setText(bundle.getString("TimerActionDialog.okButton.text"));
                okButton.addActionListener(e -> ok());
                buttonBar.add(okButton, new GridBagConstraints(1, 0, 1, 1, 0.0, 0.0,
                    GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                    new Insets(0, 0, 0, 5), 0, 0));

                //---- cancelButton ----
                cancelButton.setText(bundle.getString("TimerActionDialog.cancelButton.text"));
                cancelButton.addActionListener(e -> cancel());
                buttonBar.add(cancelButton, new GridBagConstraints(2, 0, 1, 1, 0.0, 0.0,
                    GridBagConstraints.CENTER, GridBagConstraints.VERTICAL,
                    new Insets(0, 0, 0, 0), 0, 0));
            }
            dialogPane.add(buttonBar, BorderLayout.SOUTH);
        }
        contentPane.add(dialogPane, BorderLayout.CENTER);
        pack();
        setLocationRelativeTo(getOwner());

        //---- buttonGroup1 ----
        var buttonGroup1 = new ButtonGroup();
        buttonGroup1.add(radioTimer);
        buttonGroup1.add(radioAlarm);
        // JFormDesigner - End of component initialization  //GEN-END:initComponents
    }

    // JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables
    private JPanel dialogPane;
    private JPanel contentPanel;
    private JPanel timerPanel;
    private JRadioButton radioTimer;
    private JSpinner hourSpinner;
    private JLabel label4;
    private JSpinner minuteSpinner;
    private JLabel label5;
    private JSpinner secondSpinner;
    private JLabel label2;
    private JPanel alarmPanel;
    private JRadioButton radioAlarm;
    private JSpinner hourSpinnerAlarm;
    private JLabel label1;
    private JSpinner minuteSpinnerAlarm;
    private JLabel label3;
    private JPanel buttonBar;
    private JButton okButton;
    private JButton cancelButton;
    // JFormDesigner - End of variables declaration  //GEN-END:variables
}
