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

package net.jeremybrooks.photopipr.worker;

import net.jeremybrooks.jinx.JinxConstants;
import net.jeremybrooks.jinx.JinxException;
import net.jeremybrooks.jinx.api.PhotosApi;
import net.jeremybrooks.jinx.response.photos.PermsSetResponse;
import net.jeremybrooks.jinx.response.photos.upload.UploadResponse;
import net.jeremybrooks.photopipr.JinxFactory;
import net.jeremybrooks.photopipr.PPConstants;
import net.jeremybrooks.photopipr.helper.DesktopAlert;
import net.jeremybrooks.photopipr.helper.PhotoMetadata;
import net.jeremybrooks.photopipr.helper.PhotoMetadataHelper;
import net.jeremybrooks.photopipr.model.Action;
import net.jeremybrooks.photopipr.model.GroupRule;
import net.jeremybrooks.photopipr.model.UploadAction;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.util.FileUtils;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Stream;

import static net.jeremybrooks.photopipr.PPConstants.*;

public class Upload {
    private static final Logger logger = LogManager.getLogger();
    private final UploadAction uploadAction;
    private final int index;

    private final WorkflowRunner workflowRunner;

    private final SimpleDateFormat simpleDateFormat;

    public Upload(UploadAction uploadAction, int index, WorkflowRunner workflowRunner) {
        this.uploadAction = uploadAction;
        this.index = index;
        this.workflowRunner = workflowRunner;
        this.simpleDateFormat = new SimpleDateFormat(uploadAction.getDateFormat());
    }

    void go() {
        uploadAction.setHasErrors(false);
        uploadAction.setStatus(Action.Status.RUNNING);
        uploadAction.setStatusMessage("Selecting " + uploadAction.getQuantity() + " photos..." );
        workflowRunner.publish(uploadAction, index);

        List<Path> photos = getPhotos();
        int count = 0;
        logger.info("Got {} photos to upload", photos.size());
        if (photos.isEmpty()) {
            uploadAction.setStatus(Action.Status.IDLE);
            uploadAction.setStatusMessage("No photos found to upload at " + new Date());
            workflowRunner.publish(uploadAction, index);
        } else {
            for (Path p : photos) {
                try {
                    PhotoMetadata uploadMetadata = PhotoMetadataHelper.readMetadata(p);
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

                    processGroupRules(uploadMetadata, response.getPhotoId());

                    // sleep
                    if (count < photos.size() && uploadAction.getInterval() > 0) {
                        new TimeDelay(workflowRunner, uploadAction, index,
                                String.format("[%d/%d] Sleeping", count, photos.size()),
                                uploadAction.getInterval() * 60 * 1000L).go();
                    }
                } catch (Exception e) {
                    logger.error("Error while processing photo {}", p, e);
                    uploadAction.setHasErrors(true);
                    new DesktopAlert()
                            .withTitle("PhotoPipr Upload Error" )
                            .withMessage("There was an error while uploading photo "
                                    + p.getFileName() + ".\nSee the logs for details." )
                            .showAlert();
                }
            }
            uploadAction.setStatus(Action.Status.IDLE);
            uploadAction.setStatusMessage("Upload action completed at " + new Date());
            workflowRunner.publish(uploadAction, index);
        }
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

        switch (PPConstants.SelectionOrder.valueOf(uploadAction.getSelectionOrder())) {
            case RANDOM -> { //random
                List<Path> mutableList = new ArrayList<>(tempList);
                Collections.shuffle(mutableList);
                list = mutableList.stream().limit(uploadAction.getQuantity()).toList();
            }
            case DATE_DESC -> // newest
                    list = tempList.stream().sorted(Comparator.comparing((Path o) -> {
                                try {
                                    return Files.getLastModifiedTime(o);
                                } catch (IOException e) {
                                    throw new RuntimeException(e);
                                }
                            }))
                            .limit(uploadAction.getQuantity())
                            .toList();
            case DATE_ASC -> //oldest
                    list = tempList.stream().sorted((o1, o2) -> {
                                try {
                                    return Files.getLastModifiedTime(o2).compareTo(Files.getLastModifiedTime(o1));
                                } catch (IOException e) {
                                    throw new RuntimeException(e);
                                }
                            })
                            .limit(uploadAction.getQuantity())
                            .toList();
            case ALPHA_ASC -> // a-z
                    list = tempList.stream().sorted(Comparator.naturalOrder())
                            .limit(uploadAction.getQuantity())
                            .toList();
            case ALPHA_DESC -> // z-a
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

    private void move(Path p, PhotoMetadata metadata) {
        Path destDir;
        try {
            switch (PPConstants.DirectoryCreateStrategy.valueOf(uploadAction.getDirectoryCreateStrategy())) {
                case NO_NEW_DIRECTORIES -> destDir = Paths.get(uploadAction.getMovePath());
                case DATE_TAKEN -> {
                    Date date = PhotoMetadataHelper.parseDateCreated(metadata);
                    if (date == null) {
                        date = new Date();
                    }
                    destDir = Paths.get(uploadAction.getMovePath(), simpleDateFormat.format(date));
                    if (Files.notExists(destDir)) {
                        Files.createDirectories(destDir);
                    }
                }
                case DATE_UPLOADED -> {
                    destDir = Paths.get(uploadAction.getMovePath(), simpleDateFormat.format(new Date()));
                    if (Files.notExists(destDir)) {
                        Files.createDirectories(destDir);
                    }
                }
                default ->
                        throw new Exception("Invalid directory create strategy " + uploadAction.getDirectoryCreateStrategy());
            }
            Path dest = destDir.resolve(p.getFileName());
            logger.info("Moving {} to {}", p, dest);
            Files.move(p, dest);
        } catch (Exception e) {
            logger.error("Error moving {}", p, e);
            uploadAction.setHasErrors(true);
        }
    }


    private void processGroupRules(PhotoMetadata metadata, String photoId) {
        Set<GroupRule.FlickrGroup> groupSet = new HashSet<>();

        // attempt to match the keywords in the photo against each rule.
        // if there's a match, add the group to the groupSet.
        // This de-duplicates the list of groups.
        for (GroupRule rule : uploadAction.getGroupRules()) {
            boolean match;
            List<String> tags = rule.getTags().stream()
                    .map(s -> s.replaceAll(" ", "" ).toLowerCase())
                    .toList();

            if (rule.getTagMode().equals(TagMode.ALL.name())) {
                match = CollectionUtils.containsAll(metadata.getKeywords(), tags);
            } else { // any tags
                match = CollectionUtils.containsAny(metadata.getKeywords(), tags);
            }
            if (match) {
                groupSet.addAll(rule.getGroups());
            }
        }

        // now add the photo to each group in the set
        groupSet.forEach(flickrGroup ->
        {
            logger.info("Adding photo {} to group {}", photoId, flickrGroup.getGroupId());
            uploadAction.setStatusMessage("Adding photo to group " + flickrGroup.getGroupName());
            workflowRunner.publish(uploadAction, index);
            try {
                JinxFactory.getInstance().getGroupsPoolsApi().add(photoId, flickrGroup.getGroupId());
            } catch (JinxException je) {
                logger.error("Error adding photo to group.", je);
                uploadAction.setHasErrors(true);
            }
        });
    }

}
