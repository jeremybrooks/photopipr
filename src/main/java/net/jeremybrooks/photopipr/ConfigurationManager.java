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

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import net.jeremybrooks.photopipr.action.Action;
import net.jeremybrooks.photopipr.model.Configuration;
import net.jeremybrooks.photopipr.model.Workflow;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class ConfigurationManager {

    private static final Logger logger = LogManager.getLogger();
    private static Configuration configuration;
    private static final Path configPath = Paths.get(String.valueOf(Main.APP_HOME), PPConstants.CONFIGURATION_FILENAME);

    public static Configuration getConfig() {
        if (configuration == null) {
            loadConfiguration();
        }
        return configuration;
    }

    public static void saveConfiguration() {
        try {
            Files.writeString(configPath,
                    new GsonBuilder().setPrettyPrinting().create().toJson(configuration));
        } catch (IOException e) {
            logger.warn("Could not save configuration.", e);
        }
    }


    private static void loadConfiguration() {
        if (Files.exists(configPath)) {
            try (InputStreamReader in = new InputStreamReader(Files.newInputStream(configPath), StandardCharsets.UTF_8)) {
                configuration = new Gson().fromJson(in, Configuration.class);
                logger.info("Loaded configuration from file.");
            } catch (Exception e) {
                configuration = new Configuration();
                logger.warn("Error loading configuration from file, will use default configuration.", e);
            }
        } else {
            configuration = new Configuration();
            logger.info("No configuration found. Using default configuration.");
        }
    }

    public static List<Workflow> loadWorkflows() throws IOException {
        Gson gson = new GsonBuilder()
                .registerTypeAdapter(Action.class, new AbstractActionAdapter())
                .create();
        List<Workflow> workflows;
        Path workflowPath = Paths.get(String.valueOf(Main.APP_HOME), PPConstants.WORKFLOWS_FILENAME);
        if (Files.exists(workflowPath)) {
            try (InputStreamReader in = new InputStreamReader(Files.newInputStream(workflowPath), StandardCharsets.UTF_8)) {
                Type listType = new TypeToken<ArrayList<Workflow>>() {
                }.getType();
                workflows = gson.fromJson(in, listType);
            }
        } else {
            workflows = new ArrayList<>();
        }

        return workflows;
    }

    public static void saveWorkflows(List<Workflow> workflows) throws IOException {
        Gson gson = new GsonBuilder()
                .setPrettyPrinting()
                .registerTypeAdapter(Action.class, new AbstractActionAdapter())
                .create();
        Path workflowPath = Paths.get(String.valueOf(Main.APP_HOME), PPConstants.WORKFLOWS_FILENAME);
        Files.writeString(workflowPath, gson.toJson(workflows));
    }
}
