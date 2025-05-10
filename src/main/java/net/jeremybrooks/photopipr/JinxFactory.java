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

import com.github.scribejava.core.model.OAuth1RequestToken;
import net.jeremybrooks.jinx.Jinx;
import net.jeremybrooks.jinx.JinxConstants;
import net.jeremybrooks.jinx.JinxException;
import net.jeremybrooks.jinx.OAuthAccessToken;
import net.jeremybrooks.jinx.api.GroupsApi;
import net.jeremybrooks.jinx.api.GroupsPoolsApi;
import net.jeremybrooks.jinx.api.OAuthApi;
import net.jeremybrooks.jinx.api.PeopleApi;
import net.jeremybrooks.jinx.api.PhotosApi;
import net.jeremybrooks.jinx.api.PhotosUploadApi;
import net.jeremybrooks.jinx.logger.JinxLogger;
import net.jeremybrooks.jinx.logger.LogInterface;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.util.Properties;

/**
 * @author Jeremy Brooks
 */
public class JinxFactory {

  private static Jinx jinx;
  private static JinxFactory instance;
  private PhotosApi photosApi;
  private PhotosUploadApi photosUploadApi;
  private OAuthApi oAuthApi;
  private GroupsPoolsApi groupsPoolsApi;

  private GroupsApi groupsApi;

  private static final Logger logger = LogManager.getLogger();

  private JinxFactory() throws IOException {
    Properties properties = new Properties();
    properties.load(JinxFactory.class.getResourceAsStream("/net/jeremybrooks/photopipr/secret/private.properties"));

    jinx = new Jinx(properties.getProperty(PPConstants.FLICKR_API_KEY_PROPERTY),
        properties.getProperty(PPConstants.FLICKR_API_SECRET_PROPERTY),
        JinxConstants.OAuthPermissions.write);
    JinxLogger.setLogger(new JinxLog4jLogger());
    jinx.setVerboseLogging(ConfigurationManager.getConfig().isEnableVerboseLogging());
  }

  public static JinxFactory getInstance() {
    if (instance == null) {
      try {
        instance = new JinxFactory();
      } catch (IOException ioe) {
        logger.warn("Unable to load secrets.", ioe);
        throw new IllegalStateException("There was an error while trying to initialize the Jinx library. Check the logs.");
      }
    }
    return instance;
  }

  public void setVerboseLogging(boolean verboseLogging) {
    logger.info("Setting verbose logging to {}", verboseLogging);
    jinx.setVerboseLogging(verboseLogging);
  }

  public void setAccessToken(OAuthAccessToken token) {
    jinx.setoAuthAccessToken(token);
  }


  public OAuth1RequestToken getRequestToken() throws Exception {
    return JinxFactory.jinx.getRequestToken();
  }

  public String getAuthenticationUrl(OAuth1RequestToken requestToken) throws JinxException {
    return JinxFactory.jinx.getAuthorizationUrl(requestToken);
  }

  public OAuthAccessToken getAccessToken(OAuth1RequestToken requestToken, String verificationCode) throws JinxException {
    return JinxFactory.jinx.getAccessToken(requestToken, verificationCode);
  }

  public PhotosApi getPhotosApi() {
    if (photosApi == null) {
      photosApi = new PhotosApi(jinx);
    }
    return photosApi;
  }

  public PhotosUploadApi getPhotosUploadApi() {
    if (photosUploadApi == null) {
      photosUploadApi = new PhotosUploadApi(jinx);
    }
    return photosUploadApi;
  }

  public GroupsPoolsApi getGroupsPoolsApi() {
    if (groupsPoolsApi == null) {
      groupsPoolsApi = new GroupsPoolsApi(jinx);
    }
    return groupsPoolsApi;
  }

  public GroupsApi getGroupsApi() {
    if (groupsApi == null) {
      groupsApi = new GroupsApi(jinx);
    }
    return groupsApi;
  }

  public PeopleApi getPeopleApi() {
    return new PeopleApi(jinx);
  }

  public String getNsid() {
    return jinx.getoAuthAccessToken() == null ? null : jinx.getoAuthAccessToken().getNsid();
  }
  public String getUsername() {
    return jinx.getoAuthAccessToken() == null ? "not authorized" : jinx.getoAuthAccessToken().getUsername();
  }

    public static class JinxLog4jLogger implements LogInterface {
    private static final Logger logger = LogManager.getLogger();

    @Override
    public void log(String message) {
      logger.debug(message);
    }


    @Override
    public void log(String message, Throwable t) {
      logger.debug(message, t);
    }
  }
}
