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

package net.jeremybrooks.photopipr.gui;

import net.jeremybrooks.photopipr.model.Workflow;

import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.ListCellRenderer;
import java.awt.Color;
import java.awt.Component;
import java.io.Serial;

public class WorkflowComboBoxRenderer extends JLabel implements ListCellRenderer<Workflow> {

    @Serial
    private static final long serialVersionUID = -7926824351665345245L;

    @Override
    public Component getListCellRendererComponent(JList<? extends Workflow> list,
                                                  Workflow value, int index, boolean isSelected,
                                                  boolean cellHasFocus) {

        setOpaque(true);
        if (null != value) {
            setText(value.getName());
            if (isSelected) {
                setBackground(Color.blue);
                setForeground(Color.white);
            } else {
                setBackground(Color.white);
                setForeground(Color.black);
            }
        }
        return this;
    }
}
