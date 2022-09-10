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

package net.jeremybrooks.photopipr;

import net.jeremybrooks.jinx.OAuthAccessToken;
import net.jeremybrooks.jinx.response.groups.Groups;
import net.jeremybrooks.photopipr.gui.LoginDialog;
import net.jeremybrooks.photopipr.gui.SplashDialog;
import net.jeremybrooks.photopipr.gui.WorkflowWindow;
import net.jeremybrooks.photopipr.model.Workflow;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * Application entry point for PhotoPipr.
 *
 * <p>You must have a valid Flickr API key, which you can get here:
 * <a href="https://www.flickr.com/services/apps/create/apply/">href="https://www.flickr.com/services/apps/create/apply/</a></p>
 *
 * @author Jeremy Brooks
 */
public class Main {

  private static final Logger logger = LogManager.getLogger();
  private static final SplashDialog splashDialog = new SplashDialog();
  private static List<Workflow> workflows;

  /* This is where the authorization token will be saved */
  public static final Path APP_HOME = Paths.get(System.getProperty("user.home"), "/.photopipr");
  private static final Path AUTH_TOKEN_FILE = Paths.get(APP_HOME.toString(), "/photopipr_auth_token");

  public static void main(String... args) {
    SwingUtilities.invokeLater(() -> splashDialog.setVisible(true));

    if (!Files.exists(APP_HOME)) {
      try {
        Files.createDirectories(APP_HOME);
      } catch (Exception e) {
        errExit("Could not create directories.", e);
      }
    }
    try {
      workflows = ConfigurationManager.loadWorkflows();
    } catch (Exception e) {
      logger.error("Error loading workflows.", e);
      int option = JOptionPane.showConfirmDialog(splashDialog,
              """
                      Error loading workflow configuration.
                      If you continue, any existing workflows will be lost.
                      
                      Would you like to continue?
                      """,
              "Could not load workflows",
              JOptionPane.YES_NO_OPTION,
              JOptionPane.QUESTION_MESSAGE);
      if (option == JOptionPane.NO_OPTION) {
        errExit("Workflow load error.", e);
      } else {
        workflows = new ArrayList<>();
      }
    }
    initJinx();
  }

  private static void initJinx() {
    try {
      JinxFactory.getInstance();
    } catch (Exception e) {
      errExit("Could not initialize JinxFactory.", e);
    }

    loadAuthToken();
  }

  /*
   * Attempt to load a previously saved auth token
   */
  private static void loadAuthToken() {
    OAuthAccessToken oAuthAccessToken = new OAuthAccessToken();
    try (InputStream in = Files.newInputStream(AUTH_TOKEN_FILE)) {
      oAuthAccessToken.load(in);
      JinxFactory.getInstance().setAccessToken(oAuthAccessToken);
      loadGroups();
    } catch (Exception e) {
      logger.info("Could not load auth token, requesting authorization...");
      try {
        SwingUtilities.invokeLater(() -> new LoginDialog(null, AUTH_TOKEN_FILE).setVisible(true));
      } catch (Exception e2) {
        errExit("Authorization failed", e2);
      }
    }
  }

  public static void loadGroups() {
    splashDialog.updateStatus("Loading groups....");
    SwingUtilities.invokeLater(() -> {
      Groups.Group[] groups = new Groups.Group[0];
      try {
        logger.info("Loading groups...");
        Groups groupList = JinxFactory.getInstance().getPeopleApi().getGroups(JinxFactory.getInstance().getNsid(), null);
        logger.info("Got {} groups", groupList.getGroupList().size());
        groupList.getGroupList().sort(Comparator.comparing(group -> group.getName().toLowerCase()));
        groups = groupList.getGroupList().toArray(new Groups.Group[0]);
      } catch (Exception e) {
        logger.error("Could not load groups.", e);
        JOptionPane.showMessageDialog(splashDialog,
                """
                        Could not load groups from Flickr.
                        Adding photos to groups during upload
                        may experience problems.""",
            "Group Load Error",
            JOptionPane.INFORMATION_MESSAGE);
      } finally {
        showMainWindow(groups);
      }
    });
  }

  public static void showMainWindow(Groups.Group[] groupArray) {
    SwingUtilities.invokeLater(() -> splashDialog.setVisible(false));
    SwingUtilities.invokeLater(() -> new WorkflowWindow(workflows, groupArray).setVisible(true));
  }







  private static void errExit(String message, Exception cause) {
    logger.fatal("Fatal error: {}", message, cause);
    JOptionPane.showMessageDialog(splashDialog,
        message, "Fatal Error", JOptionPane.ERROR_MESSAGE);
    System.exit(1);
  }
}
