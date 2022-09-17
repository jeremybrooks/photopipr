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

package net.jeremybrooks.photopipr.model;

import java.util.ArrayList;
import java.util.List;

public class GroupRule {
  private String tagMode;
  private List<String> tags;
  private List<FlickrGroup> groups;

  public List<FlickrGroup> getGroups() {
    return groups;
  }
  public void setGroups(List<FlickrGroup> groups) {
    this.groups = groups;
  }


  public void addGroup(String groupId, String groupName) {
    if (groups == null) {
      groups = new ArrayList<>();
    }
    groups.add(new FlickrGroup(groupId, groupName));
  }
  public String getTagMode() {
    return tagMode;
  }

  public void setTagMode(String tagMode) {
    this.tagMode = tagMode;
  }

  public List<String> getTags() {
    return tags;
  }

  public void setTags(List<String> tags) {
    this.tags = tags;
  }

  public class FlickrGroup {
    private String groupName;
    private String groupId;
    FlickrGroup(String groupId, String groupName) {
      this.groupId = groupId;
      this.groupName = groupName;
    }

    public String getGroupName() {
      return groupName;
    }

    public void setGroupName(String groupName) {
      this.groupName = groupName;
    }

    public String getGroupId() {
      return groupId;
    }

    public void setGroupId(String groupId) {
      this.groupId = groupId;
    }
  }
}
