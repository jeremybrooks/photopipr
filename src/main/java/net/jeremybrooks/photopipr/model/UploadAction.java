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

package net.jeremybrooks.photopipr.model;

import net.jeremybrooks.photopipr.PPConstants;

import java.util.ArrayList;
import java.util.List;

public class UploadAction implements Action {

  /* Configuration parameters */
  private String sourcePath = System.getProperty("user.home") + "/Pictures";
  private int quantity = 1;
  private int interval = 1;
  private String selectionOrder = PPConstants.SelectionOrder.RANDOM.name();
  private boolean makePrivate = false;
  private String safetyLevel = "safe";
  private transient boolean hasErrors = false;

  /* Group parameters */
  private List<GroupRule> groupRules = new ArrayList<>();

  /* Post upload parameters */
  private String postUploadAction = PPConstants.UPLOAD_DONE_ACTION_DELETE;
  private String movePath = "";

  private String directoryCreateStrategy = PPConstants.DirectoryCreateStrategy.NO_NEW_DIRECTORIES.name();

  private String dateFormat = "yyyy-MM-dd";

  private transient Status status = Status.IDLE;
  private transient String statusMessage = " ";

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

  public String getSelectionOrder() {
    return selectionOrder;
  }

  public void setSelectionOrder(String selectionOrder) {
    this.selectionOrder = selectionOrder;
  }

  public String getDirectoryCreateStrategy() {
    return directoryCreateStrategy;
  }

  public void setDirectoryCreateStrategy(String directoryCreateStrategy) {
    this.directoryCreateStrategy = directoryCreateStrategy;
  }
}
