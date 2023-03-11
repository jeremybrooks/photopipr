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

package net.jeremybrooks.photopipr;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;

public class FlickrUtil {
    /**
     * Given a group id, get the URL to the Flickr page.
     *
     * @param groupId the group id.
     * @return URL pointing to the group page on Flickr.
     * @throws MalformedURLException if the URL is incorrect.
     */
    public static URL getGroupUrl(String groupId) throws MalformedURLException {
        return new URL(String.format("https://flickr.com/groups/%s", groupId));
    }

    /**
     * Given a group id, get the URI to the Flickr page.
     *
     * @param groupId the group id.
     * @return URI pointing to the group page on Flickr.
     * @throws MalformedURLException if the URL is incorrect.
     * @throws URISyntaxException if the URI is incorrect.
     */
    public static URI getGroupUri(String groupId) throws MalformedURLException, URISyntaxException {
        return getGroupUrl(groupId).toURI();
    }
}
