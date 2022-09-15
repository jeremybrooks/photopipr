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

package net.jeremybrooks.photopipr.worker;

import com.drew.imaging.ImageMetadataReader;
import com.drew.metadata.Metadata;
import com.drew.metadata.Tag;
import com.drew.metadata.iptc.IptcDirectory;
import net.jeremybrooks.jinx.JinxConstants;
import net.jeremybrooks.jinx.JinxException;
import net.jeremybrooks.jinx.api.PhotosApi;
import net.jeremybrooks.jinx.response.photos.PermsSetResponse;
import net.jeremybrooks.jinx.response.photos.upload.UploadResponse;
import net.jeremybrooks.photopipr.JinxFactory;
import net.jeremybrooks.photopipr.model.Action;
import net.jeremybrooks.photopipr.model.UploadAction;
import net.jeremybrooks.photopipr.model.GroupRule;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.util.FileUtils;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.stream.Stream;

import static net.jeremybrooks.photopipr.PPConstants.*;

public class Upload {
    private static final Logger logger = LogManager.getLogger();
    private final UploadAction uploadAction;
    private final int index;

    private final WorkflowRunner workflowRunner;

    public Upload(UploadAction uploadAction, int index, WorkflowRunner workflowRunner) {
        this.uploadAction = uploadAction;
        this.index = index;
        this.workflowRunner = workflowRunner;
    }

    void go() {
        uploadAction.setHasErrors(false);
        uploadAction.setStatus(Action.Status.RUNNING);
        uploadAction.setStatusMessage("Selecting " + uploadAction.getQuantity() + " photos...");
        workflowRunner.publish(uploadAction, index);

        List<Path> photos = getPhotos();
        int count = 0;
        logger.info("Got {} photos to upload", photos.size());
        for (Path p : photos) {
            try {
                UploadMetadata uploadMetadata = readMetadata(p);
                count++;
                logger.info("Uploading photo {}", p);
                uploadAction.setStatusMessage(String.format("[%d/%d] Uploading photo %s...", count,
                        photos.size(), p.getFileName()));
                workflowRunner.publish(uploadAction, index);
                UploadResponse response = JinxFactory.getInstance().getPhotosUploadApi().upload(
                        Files.readAllBytes(p),
                        uploadMetadata.getTitle(), uploadAction.getDescription(),
                        null, false, false, false, null,
                        null, false, false);
                logger.info(response);
                logger.info("Photo {} uploaded with ID {}", p, response.getPhotoId());

                if (uploadAction.isMakePrivate()) {
                    makePrivate(response.getPhotoId(), count, photos.size());
                }
                if (!uploadAction.getSafetyLevel().equals(JinxConstants.SafetyLevel.safe.name())) {
                    changeSafetyLevel(response.getPhotoId(), count, photos.size());
                }


                // handle uploaded photo
                switch (uploadAction.getPostUploadAction()) {
                    case UPLOAD_DONE_ACTION_DELETE -> delete(p);
                    case UPLOAD_DONE_ACTION_MOVE -> move(p, uploadMetadata);
                    default -> logger.warn("Unexpected post-upload action {}", uploadAction.getPostUploadAction());
                }

                processGroupRules(p, uploadMetadata);

                // sleep
                if (count < photos.size() && uploadAction.getInterval() > 0) {
                    new TimeDelay(workflowRunner, uploadAction, index,
                            String.format("[%d/%d] Sleeping", count, photos.size()),
                            uploadAction.getInterval() * 60 * 1000L).go();
                }
            } catch (Exception e) {
                logger.error("Error while processing photo {}", p, e);
                uploadAction.setHasErrors(true);
            }
        }
        uploadAction.setStatus(Action.Status.IDLE);
        uploadAction.setStatusMessage("Upload action completed at " + new Date());
        workflowRunner.publish(uploadAction,index);
    }

    private List<Path> getPhotos() {
        List<Path> list;
        List<Path> tempList;
        try (Stream<Path> stream = Files.list(Paths.get(uploadAction.getSourcePath()))) {
            tempList = stream.filter(path -> {
                String extension = FileUtils.getFileExtension(path.toFile());
                if (extension == null) {
                    return false;
                } else {
                    return SUPPORTED_FILE_TYPES.contains(extension.toLowerCase());
                }
            }).toList();
        } catch (Exception e) {
            logger.error("Error listing files.", e);
            tempList = new ArrayList<>();
        }

        switch (uploadAction.getSelectionOrderIndex()) {
            case 0 -> { //random
                List<Path> mutableList = new ArrayList<>(tempList);
                Collections.shuffle(mutableList);
                list = mutableList.stream().limit(uploadAction.getQuantity()).toList();
            }
            case 1 -> // newest
                    list = tempList.stream().sorted(Comparator.comparing((Path o) -> {
                                try {
                                    return Files.getLastModifiedTime(o);
                                } catch (IOException e) {
                                    throw new RuntimeException(e);
                                }
                            }))
                            .limit(uploadAction.getQuantity())
                            .toList();
            case 2 -> //oldest
                    list = tempList.stream().sorted((o1, o2) -> {
                                try {
                                    return Files.getLastModifiedTime(o2).compareTo(Files.getLastModifiedTime(o1));
                                } catch (IOException e) {
                                    throw new RuntimeException(e);
                                }
                            })
                            .limit(uploadAction.getQuantity())
                            .toList();
            case 3 -> // a-z
                    list = tempList.stream().sorted(Comparator.naturalOrder())
                            .limit(uploadAction.getQuantity())
                            .toList();
            case 4 -> // z-a
                    list = tempList.stream().sorted(Comparator.reverseOrder())
                            .limit(uploadAction.getQuantity())
                            .toList();
            default -> // error
                    list = new ArrayList<>();
        }
        return list;
    }

    private void makePrivate(String photoId, int count, int size) throws JinxException {
        if (uploadAction.isMakePrivate()) {
            logger.info("Marking photo {} as private", photoId);
            uploadAction.setStatusMessage(String.format("[%d/%d] Marking photo as private",
                    count, size));
            workflowRunner.publish(uploadAction, index);
            PhotosApi photosApi = JinxFactory.getInstance().getPhotosApi();
            PermsSetResponse psr = photosApi.setPerms(photoId,
                    false, false, false,
                    JinxConstants.Perms.nobody, JinxConstants.Perms.nobody);
            logger.info("Set photoId {} to private", psr.getPhotoId());
        }
    }

    private void changeSafetyLevel(String photoId, int count, int size) throws JinxException {
        JinxConstants.SafetyLevel safetyLevel;
        if (uploadAction.getSafetyLevel().equals(JinxConstants.SafetyLevel.moderate.name())) {
            safetyLevel = JinxConstants.SafetyLevel.moderate;
        } else {
            safetyLevel = JinxConstants.SafetyLevel.restricted;
        }
        logger.info("Setting safety level to {}", safetyLevel);
        uploadAction.setStatusMessage(String.format("[%d/%d] Setting safety level to %s",
                count, size, safetyLevel));
        workflowRunner.publish(uploadAction, index);
        PhotosApi photosApi = JinxFactory.getInstance().getPhotosApi();
        photosApi.setSafetyLevel(photoId, safetyLevel, true);
    }

    private void delete(Path p) {
        logger.info("Deleting {}", p);
        try {
            Files.delete(p);
        } catch (Exception e) {
            logger.error("Error deleting {}", p, e);
            uploadAction.setHasErrors(true);
        }
    }

    private void move(Path p, UploadMetadata metadata) {
        Path destDir;
        try {
            if (uploadAction.isCreateFolders()) {
                destDir = Paths.get(uploadAction.getMovePath(), metadata.getDateCreated());
                if (Files.notExists(destDir)) {
                    Files.createDirectories(destDir);
                }
            } else {
                destDir = Paths.get(uploadAction.getMovePath());
            }
            Path dest = destDir.resolve(p.getFileName());
            logger.info("Moving {} to {}", p, dest);
            Files.move(p, dest);
        } catch (Exception e) {
            logger.error("Error moving {}", p, e);
            uploadAction.setHasErrors(true);
        }
    }

    private UploadMetadata readMetadata(Path p) {
        UploadMetadata metadata = new UploadMetadata();
        metadata.setTitle(String.valueOf(p.getFileName()));
        try (InputStream in = Files.newInputStream(p)) {
            Metadata m = ImageMetadataReader.readMetadata(in);
            for (IptcDirectory d : m.getDirectoriesOfType(IptcDirectory.class)) {
                for (Tag t : d.getTags()) {
                    switch (t.getTagType()) {
                        case 517 -> metadata.setTitle(t.getDescription());
                        case 632 -> metadata.setCaption(t.getDescription());
                        case 567 -> metadata.setDateCreated(t.getDescription());
                    }
                }
                if (d.getKeywords() != null) {
                    d.getKeywords().forEach(k ->
                            metadata.addKeyword(k.replaceAll(" ", "").toLowerCase()));
                }
            }
        } catch (Exception e) {
            logger.info("Error reading metadata from file {}", p, e);
        }
        return metadata;
    }

    private void processGroupRules(Path p, UploadMetadata metadata) {
        for (GroupRule rule : uploadAction.getGroupRules()) {
            boolean match;
            List<String> tags = rule.getTags().stream()
                    .map(s -> s.replaceAll(" ", "").toLowerCase())
                    .toList();

            if (rule.getTagModeIndex() == 0) { // all tags
                match = CollectionUtils.containsAll(metadata.getKeywords(), tags);
            } else { // any tags
                match = CollectionUtils.containsAny(metadata.getKeywords(), tags);
            }
            if (match) {
                logger.info("Adding photo {} to group {}", p, rule.getGroupName());
                uploadAction.setStatusMessage("Adding photo to group " + rule.getGroupName());
                workflowRunner.publish(uploadAction, index);
                // todo add photo to group
            }
        }
    }

    private class UploadMetadata {
        private String title;
        private String caption;
        private String dateCreated = "0000-00-00";

        private List<String> keywords = new ArrayList<>();

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public String getCaption() {
            return caption;
        }

        public void setCaption(String caption) {
            this.caption = caption;
        }

        public String getDateCreated() {
            return dateCreated;
        }

        public void setDateCreated(String dateCreated) {
            this.dateCreated = dateCreated;
        }

        public List<String> getKeywords() {
            return keywords;
        }

        public void setKeywords(List<String> keywords) {
            this.keywords = keywords;
        }

        public void addKeyword(String keyword) {
            this.keywords.add(keyword);
        }
    }
}
