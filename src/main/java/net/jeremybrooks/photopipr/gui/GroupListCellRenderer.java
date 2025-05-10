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

package net.jeremybrooks.photopipr.gui;


import net.jeremybrooks.jinx.response.groups.Groups;
import net.jeremybrooks.photopipr.PPConstants;

import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.ListCellRenderer;
import java.awt.Component;
import java.io.Serial;

public class GroupListCellRenderer extends JLabel implements ListCellRenderer<Groups.Group> {

    @Serial
    private static final long serialVersionUID = -7460828001703079954L;

    @Override
    public Component getListCellRendererComponent(JList<? extends Groups.Group> list,
                                                  Groups.Group value, int index, boolean isSelected,
                                                  boolean cellHasFocus) {
        setOpaque(true);
        setText(value.getName());

        if (isSelected) {
            setBackground(PPConstants.LIST_SELECTED_BACKGROUND);
        } else {
            setBackground(PPConstants.LIST_ODD_NOT_SELECTED_BACKGROUND);
        }

        return this;
    }
}
