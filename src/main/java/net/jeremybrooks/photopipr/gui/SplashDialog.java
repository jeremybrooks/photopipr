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
 * Created by JFormDesigner on Wed Nov 04 21:02:00 PST 2020
 */

package net.jeremybrooks.photopipr.gui;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import java.awt.BorderLayout;
import java.util.ResourceBundle;

/**
 * @author Jeremy Brooks
 */
public class SplashDialog extends JFrame {
  private static final long serialVersionUID = -7768897905959027790L;

  public SplashDialog() {
    initComponents();
//    lblVersion.setText(String.format("%s : %s",
//        Optional.ofNullable(MainWindow.class.getPackage().getImplementationTitle()).orElse("PhotoPipr"),
//        Optional.ofNullable(MainWindow.class.getPackage().getImplementationVersion()).orElse("0.0.0")));
  }

  public void updateStatus(String status) {
    SwingUtilities.invokeLater(() -> lblStatus.setText(status));
  }

  private void initComponents() {
    // JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents
    ResourceBundle bundle = ResourceBundle.getBundle("net.jeremybrooks.photopipr.splash");
    lblStatus = new JLabel();
    lblVersion = new JLabel();

    //======== this ========
    setUndecorated(true);
    var contentPane = getContentPane();
    contentPane.setLayout(new BorderLayout());

    //---- lblStatus ----
    lblStatus.setText(bundle.getString("SplashDialog.lblStatus.text_2"));
    contentPane.add(lblStatus, BorderLayout.SOUTH);

    //---- lblVersion ----
    lblVersion.setText(bundle.getString("SplashDialog.lblVersion.text_2"));
    lblVersion.setHorizontalAlignment(SwingConstants.CENTER);
    contentPane.add(lblVersion, BorderLayout.NORTH);
    setSize(540, 300);
    setLocationRelativeTo(getOwner());
    // JFormDesigner - End of component initialization  //GEN-END:initComponents
  }

  // JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables
  private JLabel lblStatus;
  private JLabel lblVersion;
  // JFormDesigner - End of variables declaration  //GEN-END:variables
}
