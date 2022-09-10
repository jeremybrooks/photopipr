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

import com.drew.imaging.ImageMetadataReader;
import com.drew.metadata.Directory;
import com.drew.metadata.Metadata;
import com.drew.metadata.Tag;
import com.drew.metadata.iptc.IptcDirectory;
import junit.framework.TestCase;
import org.junit.Test;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class FinishActionTest extends TestCase {
    @Test
    public void test() throws Exception {
        try (InputStream in = FinishActionTest.class.getResourceAsStream("/ALA.jpg")) {
            Metadata metadata = ImageMetadataReader.readMetadata(in);
            Collection<IptcDirectory> iptc = metadata.getDirectoriesOfType(IptcDirectory.class);
            List<String> keywords = new ArrayList<>();
            iptc.forEach(iptcDirectory -> {
                if (iptcDirectory.getKeywords() != null) {
                    keywords.addAll(iptcDirectory.getKeywords());
                }
            });
            System.out.println(keywords);
        }
    }

    @Test
    public void testTitle() throws Exception {
        System.out.println("hi");
        try (InputStream in = FinishActionTest.class.getResourceAsStream("/ALA.jpg")) {
            Metadata metadata = ImageMetadataReader.readMetadata(in);
            System.out.println("got " + metadata.getDirectoryCount());
            for (Directory d : metadata.getDirectories()) {
//                for (Tag t : d.getTags()) {
//                    System.out.println(String.format("%s %d %s", t.getTagName(), t.getTagType(),
//                            t.getDescription()));
//                }
                if (d instanceof IptcDirectory) {
                    for (Tag t : d.getTags()) {
//                        System.out.println(String.format("tagName:%s%n" +
//                                "tagType: %d%n" +
//                                "description: %s%n",t.getTagName(), t.getTagType(),
//                                t.getDescription()));
                        if (t.getTagType() == 517) { // title
                            System.out.println(t.getDescription());
                        }
                        if (t.getTagType() == 632) { // caption
                            System.out.println(t.getDescription());
                        }
                        if (t.getTagType() == 567) { // date created
                            System.out.println(t.getDescription());
                        }
                    }
                }
            }
        }
    }
}