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
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.InetAddress;
import java.net.URLEncoder;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.mortbay.jetty.Handler;
import org.mortbay.jetty.Request;
import org.mortbay.jetty.Server;
import org.mortbay.jetty.handler.AbstractHandler;
import org.mortbay.jetty.handler.DefaultHandler;
import org.mortbay.jetty.handler.HandlerList;
import org.mortbay.jetty.handler.ResourceHandler;
import org.mortbay.jetty.nio.SelectChannelConnector;
import org.paxml.core.PaxmlRuntimeException;
import org.springframework.core.io.ClassPathResource;

public class FileServer {
    private static final Log log = LogFactory.getLog(FileServer.class);
    public static final String PREFERRED_HOST_ADDRESS = "paxml.attachFile.preferredHostAddress";
    private static final ConcurrentMap<String, String> memFiles = new ConcurrentHashMap<String, String>();
    public static final String IN_MEM_IDENT = "mem:";
    public static final String TMP_FILE_IDENT = "tmpf:";
    public static final String CLASSPATH_IDENT = "classpath:";
    private static final String TMP_DIR = "target";
    private final Server server = new Server();
    private volatile int port;

    public String hostIt(String data, boolean content) {
        start();
        if (content) {
            return getHostedUrl(hostFileContent(data));
        } else if (data.contains("://")) {
            return data;
        } else {
            return getHostedUrl(CLASSPATH_IDENT + data);
        }
    }

    /**
     * Host a string as file content, preferrably in a system temp file. If
     * system temp file does not work, host in memory. In either case, a file is
     * readable for only once.
     * 
     * @param content
     *            the file content
     * @return the hosted path
     */
    public static String hostFileContent(String content) {
        String path;
        File file;
        try {
            File dir=new File(TMP_DIR);
            dir.mkdirs();
            file = File.createTempFile(FileServer.class.getSimpleName() + "_", ".tmp", dir);
            file.deleteOnExit();

            FileOutputStream fout = new FileOutputStream(file, false);
            try {
                fout.write(content.getBytes("UTF-8"));
                fout.flush();
            } finally {
                IOUtils.closeQuietly(fout);
            }
            path = TMP_FILE_IDENT + file.getName();

        } catch (IOException e) {
            path = IN_MEM_IDENT + UUID.randomUUID().toString() + ".txt";
            if (null != memFiles.putIfAbsent(path, content)) {
                throw new RuntimeException("Duplicated in memory fike key: " + path);
            }

        }
        if (log.isDebugEnabled()) {
            log.debug("File content held in: " + path);
        }
        return path;

    }

    public String getHostedUrl(String path) {
        
        if (path.contains("://")) {
            return path;
        }
        path=URLEncoder.encode(path);
        return "http://" + getCalculatedHostAddress() + ":" + getHostPort()
                + (path.startsWith("/") ? path : ("/" + path));
    }

    public static AbstractHandler newFileHandler() {
        return new AbstractHandler() {
            @Override
            public void handle(String path, HttpServletRequest request, HttpServletResponse res, int arg3)
                    throws IOException, ServletException {
                if (path.startsWith("/")) {
                    path = path.substring(1);
                }
                String tmpFile = null;
                InputStream in = null;
                try {
                    if (path.startsWith(IN_MEM_IDENT)) {
                        // let a file be read for only once, otherwise memory
                        // could explode
                        String content = memFiles.remove(path);
                        in = new ByteArrayInputStream(content.getBytes("UTF-8"));
                    } else if (path.startsWith(TMP_FILE_IDENT)) {
                        tmpFile = path.substring(TMP_FILE_IDENT.length());
                        in = new FileInputStream(new File(TMP_DIR, tmpFile));
                    } else if (path.startsWith(CLASSPATH_IDENT)) {
                        in = new ClassPathResource(path.substring(CLASSPATH_IDENT.length())).getInputStream();
                    } else {
                        throw new IOException("Path with unknown identifier: " + path);
                    }

                    res.setStatus(HttpServletResponse.SC_OK);
                    res.setContentType("binary/x-paxml");
                    res.setHeader("Content-Disposition", "attachment; filename=\"" + path +"\"");
                    res.setHeader("Pragma", "");
                    res.setHeader("Cache-control", "");

                    IOUtils.copy(in, res.getOutputStream());

                    ((Request) request).setHandled(true);

                } catch (Exception e) {
                    if (log.isErrorEnabled()) {
                        log.error("Cannot resolve path: " + path, e);
                    }
                    res.setStatus(HttpServletResponse.SC_NOT_FOUND);

                    return;
                } finally {
                    IOUtils.closeQuietly(in);
                    if (tmpFile != null) {
                        new File(tmpFile).delete();
                    }
                }

            }

        };
    }

    public synchronized void start() {
        if (isRunning()) {
            return;
        }
        start(0, newFileHandler());
    }

    public synchronized void start(int port, AbstractHandler... handlers) {
        if (isRunning()) {
            return;
        }
        // install shutdown hook to stop file server
        Runtime.getRuntime().addShutdownHook(new Thread(new Runnable() {

            @Override
            public void run() {
                stop();
            }

        }));
        if (log.isInfoEnabled()) {
            log.info("Starting file server");
        }
        SelectChannelConnector connector = new SelectChannelConnector();
        connector.setPort(port);
        server.addConnector(connector);

        HandlerList _handlers = new HandlerList();
        List<AbstractHandler> list = new ArrayList<AbstractHandler>(Arrays.asList(handlers));
        list.add(new DefaultHandler());

        _handlers.setHandlers(list.toArray(new Handler[list.size()]));
        server.setHandler(_handlers);

        try {
            server.start();
        } catch (Exception e) {
            throw new PaxmlRuntimeException("Cannot start file server", e);
        }

        this.port = connector.getLocalPort();

        if (log.isInfoEnabled()) {
            log.info("File server started at port: " + this.port);
        }

    }

    public synchronized void stop() {
        if (!isRunning()) {
            return;
        }
        try {
            if (log.isInfoEnabled()) {
                log.info("Stopping file server");
            }
            server.stop();
        } catch (Exception e) {
            throw new PaxmlRuntimeException("Cannot stop file server", e);
        }
    }

    public static ResourceHandler newFileHandler(String baseDir) {
        if (StringUtils.isBlank(baseDir)) {
            baseDir = ".";
        }
        ResourceHandler fileHandler = new ResourceHandler();

        fileHandler.setResourceBase(baseDir);
        return fileHandler;
    }

    public static String getHostIp() {
        try {
            InetAddress addr = InetAddress.getLocalHost();
            return addr.getHostAddress();
        } catch (UnknownHostException e) {
            throw new PaxmlRuntimeException("Cannot get this machine's ip", e);
        }
    }

    public static String getHostName() {
        try {
            InetAddress addr = InetAddress.getLocalHost();
            return addr.getHostName();
        } catch (UnknownHostException e) {
            throw new PaxmlRuntimeException("Cannot get this machine's name", e);
        }
    }

    public static String getPreferredHostAddress() {
        return System.getProperty(PREFERRED_HOST_ADDRESS);
    }

    public static String getCalculatedHostAddress() {
        String addr = getPreferredHostAddress();
        if (StringUtils.isBlank(addr)) {
            addr = getHostIp();
        }
        return addr;
    }

    public int getHostPort() {
        return port;
    }

    public synchronized boolean isRunning() {

        return server.isRunning();
    }
}
