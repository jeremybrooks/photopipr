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

package net.jeremybrooks.photopipr.action;

import net.jeremybrooks.photopipr.PPConstants;
import net.jeremybrooks.photopipr.model.GroupRule;

import java.util.ArrayList;
import java.util.List;

import static net.jeremybrooks.photopipr.action.Action.Status.WAITING;

public class UploadAction implements Action {

  /* Configuration parameters */
  private String sourcePath = System.getProperty("user.home") + "/Pictures";
  private int quantity = 1;
  private int interval = 1;
  private int selectionOrderIndex;
  private boolean makePrivate = false;
  private String safetyLevel = "safe";
  private transient boolean hasErrors = false;

  /* Group parameters */
  private List<GroupRule> groupRules = new ArrayList<>();

  /* Post upload parameters */
  private String postUploadAction = PPConstants.UPLOAD_DONE_ACTION_DELETE;
  private String movePath = "";
  private boolean createFolders = false;
  private String dateFormat = "yyyy-MM-dd";

  private transient Status status = WAITING;
  private transient String statusMessage;

  public String getDescription() {
    if (interval > 0) {
      return String.format("Upload %d photos from %s. Wait %d minute%s between uploads.",
              quantity, sourcePath, interval, interval == 1 ? "" : "s");
    } else {
      return String.format("Upload %d photos from %s.", quantity, sourcePath);
    }
  }

  @Override
  public Status getStatus() {
    return status;
  }

  @Override
  public void setStatus(Status status) {
    this.status = status;
  }

  @Override
  public String toString() {
    return getDescription();
  }

  public String getSourcePath() {
    return sourcePath;
  }

  public void setSourcePath(String sourcePath) {
    this.sourcePath = sourcePath;
  }

  public int getQuantity() {
    return quantity;
  }

  public void setQuantity(int quantity) {
    this.quantity = quantity;
  }

  public int getInterval() {
    return interval;
  }

  public void setInterval(int interval) {
    this.interval = interval;
  }

  public String getMovePath() {
    return movePath;
  }

  public void setMovePath(String movePath) {
    this.movePath = movePath;
  }


  public String getDateFormat() {
    return dateFormat;
  }

  public void setDateFormat(String dateFormat) {
    this.dateFormat = dateFormat;
  }

  public List<GroupRule> getGroupRules() {
    return groupRules;
  }

  public void setGroupRules(List<GroupRule> groupRules) {
    this.groupRules = groupRules;
  }

  public String getPostUploadAction() {
    return postUploadAction;
  }

  public void setPostUploadAction(String postUploadAction) {
    this.postUploadAction = postUploadAction;
  }

  public int getSelectionOrderIndex() {
    return selectionOrderIndex;
  }

  public void setSelectionOrderIndex(int selectionOrderIndex) {
    this.selectionOrderIndex = selectionOrderIndex;
  }

  public boolean isCreateFolders() {
    return createFolders;
  }

  public void setCreateFolders(boolean createFolders) {
    this.createFolders = createFolders;
  }

  @Override
  public String getStatusMessage() {
    return statusMessage;
  }

  @Override
  public void setStatusMessage(String statusMessage) {
    this.statusMessage = statusMessage;
  }

  public boolean isMakePrivate() {
    return makePrivate;
  }

  public void setMakePrivate(boolean makePrivate) {
    this.makePrivate = makePrivate;
  }

  public boolean isHasErrors() {
    return hasErrors;
  }

  public void setHasErrors(boolean hasErrors) {
    this.hasErrors = hasErrors;
  }

  public String getSafetyLevel() {
    return safetyLevel;
  }

  public void setSafetyLevel(String safetyLevel) {
    this.safetyLevel = safetyLevel;
  }
}
