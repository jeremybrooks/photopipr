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

package net.jeremybrooks.photopipr.gui;

import net.jeremybrooks.jinx.response.groups.Groups;

import javax.swing.ListModel;
import javax.swing.event.ListDataEvent;
import javax.swing.event.ListDataListener;
import java.io.Serial;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;

/**
 * This is a model that sorts items by the Group Name.
 */
public class GroupListModel implements ListModel<Groups.Group> {
    @Serial
    private static final long serialVersionUID = -1663484177547622986L;

    private List<Groups.Group> items;
    private final List<Groups.Group> allItems;
    private final List<ListDataListener> listeners;
    private final Comparator<Groups.Group> groupComparator = Comparator.comparing(group -> group.getName().toLowerCase());

    public GroupListModel() {
        this(null);
    }
    public GroupListModel(List<Groups.Group> groupList) {
        if (groupList == null) {
            allItems = new ArrayList<>();
        } else {
            allItems = groupList;
        }
        items = new ArrayList<>(allItems);
        listeners = new ArrayList<>();
    }


    public void addItem(Groups.Group item) {
        if (!items.contains(item)) {
            items.add(item);
            items.sort(groupComparator);
            notifyListeners();
        }
    }

    public void addItems(Collection<Groups.Group> newItems) {
        newItems.forEach(group -> {
            if (!items.contains(group)) {
                items.add(group);
            }
        });
        items.sort(groupComparator);
        notifyListeners();
    }

    public void removeItemAt(int index) {
        items.remove(index);
        notifyListeners();
    }

    public void removeItem(Groups.Group group) {
        items.remove(group);
        notifyListeners();
    }

    public void removeItems(List<Groups.Group> groups) {
        groups.forEach(group -> items.remove(group));
        notifyListeners();
    }

    public void clear() {
        items.clear();
        notifyListeners();
    }

    public void setFilterText(String filterText) {
        items.clear();
        if (filterText.isEmpty()) {
            items.addAll(allItems);
        } else {
            items.addAll(allItems.stream()
                    .filter(group -> group.getName().toLowerCase().contains(filterText.toLowerCase()))
                    .toList());
        }
        notifyListeners();
    }

    void notifyListeners() {
        ListDataEvent le = new ListDataEvent(this,
                ListDataEvent.CONTENTS_CHANGED, 0, getSize());
        listeners.forEach(listDataListener -> listDataListener.contentsChanged(le));
    }

    @Override
    public int getSize() {
        return items.size();
    }

    @Override
    public Groups.Group getElementAt(int index) {
        return items.get(index);
    }

    @Override
    public void addListDataListener(ListDataListener l) {
        listeners.add(l);
    }

    @Override
    public void removeListDataListener(ListDataListener l) {
        listeners.remove(l);
    }
}
