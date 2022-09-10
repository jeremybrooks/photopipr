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
 * Created by JFormDesigner on Fri Sep 09 10:04:24 PDT 2022
 */

package net.jeremybrooks.photopipr.gui;

import net.jeremybrooks.photopipr.action.Action;
import net.jeremybrooks.photopipr.action.UploadAction;

import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.ListCellRenderer;
import java.awt.Color;
import java.awt.Component;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.io.Serial;

/**
 * @author Jeremy Brooks
 */
public class ActionListItemRenderer extends JPanel implements ListCellRenderer<Action> {
    @Serial
    private static final long serialVersionUID = -3341073154520983031L;

    public ActionListItemRenderer() {
        initComponents();
    }

    private void initComponents() {
        // JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents
        lblIcon = new JLabel();
        lblDescription = new JLabel();
        lblStatus = new JLabel();

        //======== this ========
        setLayout(new GridBagLayout());
        ((GridBagLayout)getLayout()).columnWidths = new int[] {0, 0, 0};
        ((GridBagLayout)getLayout()).rowHeights = new int[] {0, 0, 0};
        ((GridBagLayout)getLayout()).columnWeights = new double[] {0.0, 0.0, 1.0E-4};
        ((GridBagLayout)getLayout()).rowWeights = new double[] {0.0, 0.0, 1.0E-4};

        //---- lblIcon ----
        lblIcon.setText("text");
        add(lblIcon, new GridBagConstraints(0, 0, 1, 2, 0.0, 0.0,
            GridBagConstraints.CENTER, GridBagConstraints.BOTH,
            new Insets(3, 3, 3, 8), 0, 0));

        //---- lblDescription ----
        lblDescription.setText("text");
        add(lblDescription, new GridBagConstraints(1, 0, 1, 1, 1.0, 0.0,
            GridBagConstraints.CENTER, GridBagConstraints.BOTH,
            new Insets(3, 3, 8, 3), 0, 0));

        //---- lblStatus ----
        lblStatus.setText("text");
        add(lblStatus, new GridBagConstraints(1, 1, 1, 1, 0.0, 0.0,
            GridBagConstraints.CENTER, GridBagConstraints.BOTH,
            new Insets(3, 3, 3, 3), 0, 0));
        // JFormDesigner - End of component initialization  //GEN-END:initComponents
    }

    // JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables
    private JLabel lblIcon;
    private JLabel lblDescription;
    private JLabel lblStatus;

    @Override
    public Component getListCellRendererComponent(JList<? extends Action> list, Action value, int index, boolean isSelected, boolean cellHasFocus) {
        setOpaque(true);
        lblDescription.setText(value.getDescription());
        switch (value.getStatus()) {
            case WAITING -> {
                lblIcon.setText("W");
                lblStatus.setText("");
            }
            case FINISHED  -> {
                lblIcon.setText("F");
                if (value instanceof UploadAction ua && ua.isHasErrors()) {
                    lblStatus.setText("There were errors during upload. Check the logs.");
                } else {
                    lblStatus.setText("Done");
                }
            }
            case RUNNING -> {
                lblIcon.setText("R");
                lblStatus.setText(value.getStatusMessage());
            }
        }
        if (isSelected) {
            setBackground(Color.blue);
            setForeground(Color.white);
        } else {
            setBackground(Color.white);
            setForeground(Color.black);
        }
        return this;
    }
    // JFormDesigner - End of variables declaration  //GEN-END:variables
}
