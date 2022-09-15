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
 * Created by JFormDesigner on Wed Mar 25 13:31:24 PDT 2020
 */

package net.jeremybrooks.photopipr.gui;

import com.github.scribejava.core.model.OAuth1RequestToken;
import net.jeremybrooks.jinx.OAuthAccessToken;
import net.jeremybrooks.photopipr.JinxFactory;
import net.jeremybrooks.photopipr.Main;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;
import javax.swing.UIManager;
import javax.swing.WindowConstants;
import javax.swing.border.EmptyBorder;
import java.awt.BorderLayout;
import java.awt.Cursor;
import java.awt.Desktop;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Window;
import java.io.OutputStream;
import java.io.Serial;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ResourceBundle;

/**
 * @author Jeremy Brooks
 */
public class LoginDialog extends JDialog {
  @Serial
  private static final long serialVersionUID = -4765985860757396689L;

  private static final Logger logger = LogManager.getLogger();
  private static final ResourceBundle bundle = ResourceBundle.getBundle("net.jeremybrooks.photopipr.login");
  private OAuth1RequestToken tempToken = null;
  private final Path authTokenPath;

  public LoginDialog(Window owner, Path authTokenPath) {
    super(owner);
    this.authTokenPath = authTokenPath;
    initComponents();
    lblCode.setVisible(false);
    txtCode.setVisible(false);
  }

  private void cancelButtonActionPerformed() {
    logger.info("User canceled authorization. Exiting.");
    System.exit(0);
  }

  private void okButtonActionPerformed() {
    if (txtCode.getText().trim().isEmpty()) {
      try {
        // sends user to the Flickr site to get an auth code
        // on success, shows the auth code label and text box
        // and changes the text on the Authorize button to say "Finish"
        new AuthUrlWorker().execute();
      } catch (Exception e1) {
        logger.error("Error getting URL.", e1);
        JOptionPane.showMessageDialog(this,
            String.format("%s\n\n%s",
                bundle.getString("LoginDialog.errorAuthUrl.message"),
                e1.getMessage()),
            bundle.getString("LoginDialog.errorAuthUrl.title"),
            JOptionPane.ERROR_MESSAGE);
      }
    } else {
      try {
        // exchanges the auth code for a token
        // on success, hides this window and shows the main window
        new LoginWorker().execute();
      } catch (Exception e2) {
        logger.error("Error getting auth token.", e2);
        JOptionPane.showMessageDialog(this,
            String.format("%s\n\n%s",
                bundle.getString("LoginDialog.errorToken.message"),
                e2.getMessage()),
            bundle.getString("LoginDialog.errorToken.title"),
            JOptionPane.ERROR_MESSAGE);
      }
    }
  }

  private class AuthUrlWorker extends SwingWorker<Void, Void> {
    private Cursor originalCursor;
    @Override
    protected Void doInBackground() throws Exception {
      originalCursor = getCursor();
      try {
        SwingUtilities.invokeLater(
            () -> {
              setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
              okButton.setEnabled(false);
              cancelButton.setEnabled(false);
            }
        );
        tempToken = JinxFactory.getInstance().getRequestToken();
        String url = JinxFactory.getInstance().getAuthenticationUrl(tempToken);
        Desktop.getDesktop().browse(new URI(url));
      } finally {
        SwingUtilities.invokeLater(
            () -> {
              okButton.setEnabled(true);
              cancelButton.setEnabled(true);
              setCursor(originalCursor);
            }
        );
      }
      return null;
    }

    @Override
    protected void done() {
      okButton.setText(bundle.getString("LoginDialog.okButton.text2"));
      lblCode.setVisible(true);
      txtCode.setVisible(true);
      txtCode.requestFocus();
    }
  }

  private class LoginWorker extends SwingWorker<Void, Void> {
    private Cursor originalCursor;

    @Override
    protected Void doInBackground() throws Exception {
      originalCursor = getCursor();
      try {
        SwingUtilities.invokeLater(
            () -> {
              setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
              okButton.setEnabled(false);
              cancelButton.setEnabled(false);
            }
        );
        OAuthAccessToken token = JinxFactory.getInstance().getAccessToken(tempToken, txtCode.getText().trim());
        JinxFactory.getInstance().setAccessToken(token);
        try (OutputStream out = Files.newOutputStream(authTokenPath)) {
          token.store(out);
        }
      } finally {
        SwingUtilities.invokeLater(
            () -> {
              okButton.setEnabled(true);
              cancelButton.setEnabled(true);
              setCursor(originalCursor);
            }
        );
      }
      return null;
    }

    @Override
    public void done() {
      JOptionPane.showMessageDialog(null,
          bundle.getString("LoginDialog.success.message"),
          bundle.getString("LoginDialog.success.title"),
          JOptionPane.INFORMATION_MESSAGE);
      setVisible(false);
      dispose();
      Main.showMainWindow();
    }
  }

  private void initComponents() {
    // JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents
    ResourceBundle bundle = ResourceBundle.getBundle("net.jeremybrooks.photopipr.login");
    dialogPane = new JPanel();
    contentPanel = new JPanel();
    scrollPane1 = new JScrollPane();
    textArea1 = new JTextArea();
    lblCode = new JLabel();
    txtCode = new JTextField();
    buttonBar = new JPanel();
    cancelButton = new JButton();
    okButton = new JButton();

    //======== this ========
    setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
    setModal(true);
    var contentPane = getContentPane();
    contentPane.setLayout(new BorderLayout());

    //======== dialogPane ========
    {
        dialogPane.setBorder(new EmptyBorder(12, 12, 12, 12));
        dialogPane.setLayout(new BorderLayout());

        //======== contentPanel ========
        {
            contentPanel.setLayout(new GridBagLayout());
            ((GridBagLayout)contentPanel.getLayout()).columnWidths = new int[] {0, 0, 0};
            ((GridBagLayout)contentPanel.getLayout()).rowHeights = new int[] {0, 0, 0};
            ((GridBagLayout)contentPanel.getLayout()).columnWeights = new double[] {0.0, 0.0, 1.0E-4};
            ((GridBagLayout)contentPanel.getLayout()).rowWeights = new double[] {0.0, 0.0, 1.0E-4};

            //======== scrollPane1 ========
            {

                //---- textArea1 ----
                textArea1.setText(bundle.getString("LoginDialog.textArea"));
                textArea1.setWrapStyleWord(true);
                textArea1.setLineWrap(true);
                textArea1.setEditable(false);
                textArea1.setBackground(UIManager.getColor("Button.background"));
                scrollPane1.setViewportView(textArea1);
            }
            contentPanel.add(scrollPane1, new GridBagConstraints(0, 0, 2, 1, 1.0, 1.0,
                GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                new Insets(5, 5, 10, 5), 0, 0));

            //---- lblCode ----
            lblCode.setText(bundle.getString("LoginDialog.lblCode.text"));
            contentPanel.add(lblCode, new GridBagConstraints(0, 1, 1, 1, 0.0, 0.0,
                GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                new Insets(0, 0, 0, 5), 0, 0));
            contentPanel.add(txtCode, new GridBagConstraints(1, 1, 1, 1, 0.0, 0.0,
                GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                new Insets(0, 0, 0, 0), 0, 0));
        }
        dialogPane.add(contentPanel, BorderLayout.CENTER);

        //======== buttonBar ========
        {
            buttonBar.setBorder(new EmptyBorder(12, 0, 0, 0));
            buttonBar.setLayout(new GridBagLayout());
            ((GridBagLayout)buttonBar.getLayout()).columnWidths = new int[] {0, 85, 80};
            ((GridBagLayout)buttonBar.getLayout()).columnWeights = new double[] {1.0, 0.0, 0.0};

            //---- cancelButton ----
            cancelButton.setText(bundle.getString("LoginDialog.cancelButton.text"));
            cancelButton.addActionListener(e -> cancelButtonActionPerformed());
            buttonBar.add(cancelButton, new GridBagConstraints(1, 0, 1, 1, 0.0, 0.0,
                GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                new Insets(0, 0, 0, 5), 0, 0));

            //---- okButton ----
            okButton.setText(bundle.getString("LoginDialog.okButton.text"));
            okButton.addActionListener(e -> okButtonActionPerformed());
            buttonBar.add(okButton, new GridBagConstraints(2, 0, 1, 1, 0.0, 0.0,
                GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                new Insets(0, 0, 0, 0), 0, 0));
        }
        dialogPane.add(buttonBar, BorderLayout.PAGE_END);
    }
    contentPane.add(dialogPane, BorderLayout.CENTER);
    setSize(465, 275);
    setLocationRelativeTo(getOwner());
    // JFormDesigner - End of component initialization  //GEN-END:initComponents
  }

  // JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables
  private JPanel dialogPane;
  private JPanel contentPanel;
  private JScrollPane scrollPane1;
  private JTextArea textArea1;
  private JLabel lblCode;
  private JTextField txtCode;
  private JPanel buttonBar;
  private JButton cancelButton;
  private JButton okButton;
  // JFormDesigner - End of variables declaration  //GEN-END:variables
}
