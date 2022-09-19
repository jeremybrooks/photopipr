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

import java.awt.Color;
import java.util.List;

public class PPConstants {
  public enum TagMode {
    ALL, ANY
  }
  public enum SelectionOrder {
    RANDOM,
    DATE_DESC,
    DATE_ASC,
    ALPHA_ASC,
    ALPHA_DESC
  }

  public enum TimerMode {
    TIMER, ALARM
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
