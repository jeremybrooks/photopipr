/*
 *  PhotoPipr is Copyright 2017-2023 by Jeremy Brooks
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

public interface Action {
  /**
   * The status enum defines the current state of an Action.
   *
   * IDLE - The action is not running and is not waiting to run
   * PENDING - The action is in a workflow that is running, and it is waiting to run
   * RUNNING - The action is currently running
   */
  enum Status {
    IDLE,
    PENDING,
    RUNNING
  }
  String getDescription();
  Status getStatus();
  void setStatus(Status status);

  String getStatusMessage();
  void setStatusMessage(String statusMessage);
}
