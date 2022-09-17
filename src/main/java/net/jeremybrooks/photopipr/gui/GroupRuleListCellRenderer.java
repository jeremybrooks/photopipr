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
import net.jeremybrooks.photopipr.model.GroupRule;

import javax.swing.JList;
import javax.swing.ListCellRenderer;
import java.awt.Component;

public class GroupRuleListCellRenderer implements ListCellRenderer<GroupRule> {

    @Override
    public Component getListCellRendererComponent(JList<? extends GroupRule> list, GroupRule value, int index, boolean isSelected, boolean cellHasFocus) {
        GroupRuleListCell cell = new GroupRuleListCell();
        cell.setOpaque(true);

        if (isSelected) {
            cell.setBackground(PPConstants.LIST_SELECTED_BACKGROUND);
        } else {
            if (index % 2 == 0) {
                cell.setBackground(PPConstants.LIST_EVEN_NOT_SELECTED_BACKGROUND);
            } else {
                cell.setBackground(PPConstants.LIST_ODD_NOT_SELECTED_BACKGROUND);
            }
        }

        cell.setTags(String.format("%s of %s",
                value.getTagMode(),
                value.getTags().toString()));

        cell.setGroups(String.format("Add photo to %d group%s",
                value.getGroups().size(),
                value.getGroups().size() == 1 ? "" : "s"));

        return cell;
    }
}
