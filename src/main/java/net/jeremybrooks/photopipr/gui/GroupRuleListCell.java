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
 * Created by JFormDesigner on Fri Sep 16 13:44:31 PDT 2022
 */

package net.jeremybrooks.photopipr.gui;

import javax.swing.JLabel;
import javax.swing.JPanel;
import java.awt.GridLayout;
import java.io.Serial;

/**
 * @author Jeremy Brooks
 */
public class GroupRuleListCell extends JPanel {
    @Serial
    private static final long serialVersionUID = -848390512611420355L;

    public GroupRuleListCell() {
        initComponents();
    }

    public void setTags(String tags) {
        lblTags.setText(tags);
    }

    public void setGroups(String groups) {
        lblGroups.setText(groups);
    }

    private void initComponents() {
        // JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents
        lblTags = new JLabel();
        lblGroups = new JLabel();

        //======== this ========
        setLayout(new GridLayout(2, 0));

        //---- lblTags ----
        lblTags.setText("text");
        add(lblTags);

        //---- lblGroups ----
        lblGroups.setText("text");
        add(lblGroups);
        // JFormDesigner - End of component initialization  //GEN-END:initComponents
    }

    // JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables
    private JLabel lblTags;
    private JLabel lblGroups;
    // JFormDesigner - End of variables declaration  //GEN-END:variables
}
