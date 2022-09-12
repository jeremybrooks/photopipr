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

import net.jeremybrooks.photopipr.PPConstants;
import net.jeremybrooks.photopipr.action.Action;
import net.jeremybrooks.photopipr.action.FinishAction;
import net.jeremybrooks.photopipr.action.TimerAction;

import javax.swing.JList;
import javax.swing.ListCellRenderer;
import java.awt.Component;

public class ActionListCellRenderer implements ListCellRenderer<Action> {
    @Override
    public Component getListCellRendererComponent(JList list, Action value, int index, boolean isSelected, boolean cellHasFocus) {
        ActionListCell cell = new ActionListCell();

        cell.setDescription(value.getDescription());
        cell.setStatus(value.getStatusMessage());

        if (value instanceof FinishAction) {
            cell.setIcon(ActionListCell.ICON_FINISH);
        } else if (value instanceof TimerAction) {
            cell.setIcon(ActionListCell.ICON_TIMER);
        } else {
            cell.setIcon(ActionListCell.ICON_UPLOAD);
        }

        if (value.getStatus() == Action.Status.RUNNING) {
            cell.setBackground(PPConstants.LIST_ACTIVE_BACKGROUND);
        } else {
            if (isSelected) {
                cell.setBackground(PPConstants.LIST_SELECTED_BACKGROUND);
            } else {
                if (index % 2 == 0) {
                    cell.setBackground(PPConstants.LIST_EVEN_NOT_SELECTED_BACKGROUND);
                } else {
                    cell.setBackground(PPConstants.LIST_ODD_NOT_SELECTED_BACKGROUND);
                }
            }
        }

        return cell;
    }
}
