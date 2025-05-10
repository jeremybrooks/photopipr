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

package net.jeremybrooks.photopipr;

import java.awt.Color;
import java.util.List;

/**
 * Constants used throughout the PhotoPipr project.
 */
public class PPConstants {
  public enum TagMode {
    ALL, ANY
  }

  /**
   * Specify how photos are selected from a directory.
   */
  public enum SelectionOrder {
    /**
     * Select photos randomly.
     */
    RANDOM,
    /**
     * Select photos in date descending order (oldest first).
     */
    DATE_DESC,
    /**
     * Select photos in date ascending order (newest first).
     */
    DATE_ASC,

    /**
     * Select photos in alphabetic ascending order (A-Z).
     */
    ALPHA_ASC,
    /**
     * Select photos in alphabetic descending order (Z-A).
     */
    ALPHA_DESC
  }

  /**
   * Specify how a timer works.
   */
  public enum TimerMode {
    /**
     * A timer waits for a period of time, for example 1 hour.
     */
    TIMER,

    /**
     * An alarm waits until a specific time, for example 6:00 AM.
     */
    ALARM
  }

  /**
   *
   */
  public enum DirectoryCreateStrategy {
    NO_NEW_DIRECTORIES,
    DATE_TAKEN,
    DATE_UPLOADED
  }

  public static final String FLICKR_API_KEY_PROPERTY = "FLICKR_KEY";
  public static final String FLICKR_API_SECRET_PROPERTY = "FLICKR_SECRET";
  public static final String PHOTOPIPR_HOME = System.getProperty("user.home") + "/.photopipr";
  public static final String PHOTOPIPR_AUTH_TOKEN = "photopipr_auth_token";
  public static final String WORKFLOWS_FILENAME = "PhotoPiprWorkflows.json";
  public static final String CONFIGURATION_FILENAME = "PhotoPiprConfig.json";
  public static final String FINISH_ACTION_STOP = "Stop";
  public static final String FINISH_ACTION_REPEAT = "Repeat";
  public static final String FINISH_ACTION_EXIT = "Exit";

  public static final String UPLOAD_DONE_ACTION_DELETE = "Delete";
  public static final String UPLOAD_DONE_ACTION_MOVE = "Move";

  public static final List<String> SUPPORTED_FILE_TYPES = List.of(
          "jpg", "jpeg", "png", "gif",
          "mp4", "avi", "wmv", "mov", "3gp", "m2ts", "ogg", "ogv");

  public static final Color LIST_SELECTED_BACKGROUND = new Color(47, 101, 202);
  public static final Color LIST_ODD_NOT_SELECTED_BACKGROUND = Color.white;
  public static final Color LIST_EVEN_NOT_SELECTED_BACKGROUND = new Color(192, 192, 192);
  public static final Color LIST_ACTIVE_BACKGROUND = new Color(102, 204, 0);
}
