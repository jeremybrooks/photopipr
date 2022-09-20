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

package net.jeremybrooks.photopipr.helper;

import com.drew.imaging.ImageMetadataReader;
import com.drew.metadata.Metadata;
import com.drew.metadata.Tag;
import com.drew.metadata.iptc.IptcDirectory;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.text.SimpleDateFormat;
import java.util.Date;

public class PhotoMetadataHelper {

    private static final Logger logger = LogManager.getLogger();

    private static final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy:MM:dd");
    public static PhotoMetadata readMetadata(Path p) {
        PhotoMetadata metadata = new PhotoMetadata();
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

    /**
     * Parse the date from the "date created" metadata field.
     *
     * <p>It is expected that the date is in the format yyyy:MM:dd</p>
     * @param metadata the metadata containing the date created value.
     * @return a date object, or null if the date could not be parsed.
     */
    public static Date parseDateCreated(PhotoMetadata metadata) {
        Date date = null;
        try {
            date = dateFormat.parse(metadata.getDateCreated());
        } catch (Exception e) {
            logger.error("Error parsing date {}", metadata.getDateCreated(), e);
        }
        return date;
    }
}
