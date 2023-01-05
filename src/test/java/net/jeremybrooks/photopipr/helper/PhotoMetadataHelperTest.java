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

package net.jeremybrooks.photopipr.helper;

import org.junit.Test;

import java.nio.file.Path;
import java.nio.file.Paths;

import static org.junit.Assert.*;

public class PhotoMetadataHelperTest {

    @Test
    public void testMetadata() throws Exception {
        Path p = Paths.get(PhotoMetadataHelperTest.class.getResource("/Stop Me!.jpg").toURI());
        PhotoMetadata metadata = PhotoMetadataHelper.readMetadata(p);
        assertEquals("Stop Me!", metadata.getTitle());
        assertEquals("This is the caption.", metadata.getCaption());

        assertTrue(metadata.getKeywords().contains("graffiti"));
        assertTrue(metadata.getKeywords().contains("multnomahcounty"));

        assertNotNull(PhotoMetadataHelper.parseDateCreated(metadata));
    }
}