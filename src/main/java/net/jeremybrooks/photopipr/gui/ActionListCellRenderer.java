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

import net.jeremybrooks.jinx.JinxConstants;
import net.jeremybrooks.photopipr.PPConstants;
import net.jeremybrooks.photopipr.action.Action;
import net.jeremybrooks.photopipr.action.FinishAction;
import net.jeremybrooks.photopipr.action.TimerAction;
import net.jeremybrooks.photopipr.action.UploadAction;

import javax.swing.ImageIcon;
import javax.swing.JList;
import javax.swing.ListCellRenderer;
import java.awt.Component;

public class ActionListCellRenderer implements ListCellRenderer<Action> {
    private static final ImageIcon ICON_SAFE = new ImageIcon(ActionListCellRenderer.class
            .getResource("/net/jeremybrooks/photopipr/icons/BlueCircle.png"));
    private static final ImageIcon ICON_MODERATE = new ImageIcon(ActionListCellRenderer.class
            .getResource("/net/jeremybrooks/photopipr/icons/YellowCircle.png"));
    private static final ImageIcon ICON_RESTRICTED = new ImageIcon(ActionListCellRenderer.class
            .getResource("/net/jeremybrooks/photopipr/icons/RedCircle.png"));
    private static final ImageIcon ICON_DELETE = new ImageIcon(ActionListCellRenderer.class
            .getResource("/net/jeremybrooks/photopipr/icons/Delete.png"));
    private static final ImageIcon ICON_MOVE = new ImageIcon(ActionListCellRenderer.class
            .getResource("/net/jeremybrooks/photopipr/icons/Folder.png"));
    private static final ImageIcon ICON_PUBLIC = new ImageIcon(ActionListCellRenderer.class
            .getResource("/net/jeremybrooks/photopipr/icons/UnLock.png"));
    private static final ImageIcon ICON_PRIVATE = new ImageIcon(ActionListCellRenderer.class
            .getResource("/net/jeremybrooks/photopipr/icons/Lock.png"));
    private static final ImageIcon ICON_PENDING = new ImageIcon(ActionListCellRenderer.class
            .getResource("/net/jeremybrooks/photopipr/icons/Hourglass.png"));
    private static final ImageIcon ICON_RUNNING = new ImageIcon(ActionListCellRenderer.class
            .getResource("/net/jeremybrooks/photopipr/icons/Gearwheel.png"));
    private static final ImageIcon ICON_ERROR = new ImageIcon(ActionListCellRenderer.class
            .getResource("/net/jeremybrooks/photopipr/icons/Caution.png"));
    private static final ImageIcon ICON_IDLE = new ImageIcon(ActionListCellRenderer.class
            .getResource("/net/jeremybrooks/photopipr/icons/Check.png"));


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

        switch (value.getStatus()) {
            case IDLE -> cell.setLblStatusIcon(ICON_IDLE);
            case PENDING -> cell.setLblStatusIcon(ICON_PENDING);
            case RUNNING -> cell.setLblStatusIcon(ICON_RUNNING);
        }

        if (value instanceof UploadAction ua) {
            if (ua.getSafetyLevel().equals(JinxConstants.SafetyLevel.moderate.name())) {
               cell.setLblSafetyIcon(ICON_MODERATE);
            } else if (ua.getSafetyLevel().equals(JinxConstants.SafetyLevel.restricted.name())) {
                cell.setLblSafetyIcon(ICON_RESTRICTED);
            } else {
                cell.setLblSafetyIcon(ICON_SAFE);
            }
            if (ua.getPostUploadAction().equals(PPConstants.UPLOAD_DONE_ACTION_DELETE)) {
                cell.setLblPostUploadIcon(ICON_DELETE);
            } else {
                cell.setLblPostUploadIcon(ICON_MOVE);
            }
            if (ua.isMakePrivate()) {
                cell.setLblPrivateIcon(ICON_PRIVATE);
            } else {
                cell.setLblPrivateIcon(ICON_PUBLIC);
            }
            if (ua.isHasErrors()) {
                cell.setLblStatusIcon(ICON_ERROR);
            }
        }

        if (value.getStatus() == Action.Status.RUNNING) {
            cell.setBackground(PPConstants.LIST_ACTIVE_BACKGROUND);
            cell.setLblStatusIcon(ICON_RUNNING);
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
