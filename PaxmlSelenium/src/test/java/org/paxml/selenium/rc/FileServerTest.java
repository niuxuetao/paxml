/**
 * This file is part of PaxmlSelenium.
 *
 * PaxmlSelenium is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * PaxmlSelenium is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with PaxmlSelenium.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.paxml.selenium.rc;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.net.URL;

import junit.framework.Assert;

import org.apache.commons.io.IOUtils;
import org.junit.Test;
import org.paxml.selenium.rc.FileServer;
import org.springframework.core.io.ClassPathResource;

public class FileServerTest {
    private static final FileServer server = new FileServer();

    @Test
    public void testClasspathResource() throws Exception {
        
        InputStream inWeb = null;
        InputStream inClass = null;
        final String path = "paxml/dynamic.xml";
        try {
        
            URL url = new URL(server.hostIt(path, false));

            inWeb = url.openStream();
            inClass = new ClassPathResource(path).getInputStream();

            Assert.assertEquals(IOUtils.readLines(inClass).toString(), IOUtils.readLines(inWeb).toString());

        } finally {
            IOUtils.closeQuietly(inWeb);
            IOUtils.closeQuietly(inClass);            
        }
    }
    
    @Test
    public void testClasspathResource2() throws Exception {
        
        InputStream inWeb = null;
        InputStream inClass = null;
        final String path = "/paxml/dynamic.xml";
        try {
        
            URL url = new URL(server.hostIt(path, false));

            inWeb = url.openStream();
            inClass = new ClassPathResource(path).getInputStream();

            Assert.assertEquals(IOUtils.readLines(inClass).toString(), IOUtils.readLines(inWeb).toString());

        } finally {
            IOUtils.closeQuietly(inWeb);
            IOUtils.closeQuietly(inClass);            
        }
    }
    
    @Test
    public void testStringResource() throws Exception {
        
        InputStream inWeb = null;
        InputStream inStr = null;
        final String content = "this is the content";
        try {
        
            URL url = new URL(server.hostIt(content, true));

            inWeb = url.openStream();
            inStr = new ByteArrayInputStream(content.getBytes("UTF-8"));

            Assert.assertEquals(IOUtils.readLines(inStr).toString(), IOUtils.readLines(inWeb).toString());

        } finally {
            IOUtils.closeQuietly(inWeb);
            IOUtils.closeQuietly(inStr);            
        }
    }

}
