/**
 * This file is part of PaxmlCore.
 *
 * PaxmlCore is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * PaxmlCore is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with PaxmlCore.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.paxml.bean;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.paxml.annotation.Tag;
import org.paxml.core.Context;
import org.paxml.core.PaxmlRuntimeException;

import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.Session;
import com.jcraft.jsch.SftpException;

/**
 * Sftp tag impl.
 * 
 * @author Xuetao Niu
 * 
 */
@Tag(name = "sftp")
public class SftpTag extends BeanTag {

    private static final Log log = LogFactory.getLog(SftpTag.class);

    private String username;
    private String password;
    private String host;
    private int port;
    private boolean strictHostKeyChecking;
    private String from;
    private String to;
    private String action;
    private String postMove;
    private boolean postDelete;

    @Override
    protected Object doInvoke(Context context) throws Exception {
        if (log.isDebugEnabled()) {
            log.debug("Sftp connecting to: " + host + ":" + port);
        }
        JSch jsch = new JSch();
        Session session = null;
        ChannelSftp channel = null;
        try {
            session = jsch.getSession(username, host, port);
            session.setConfig("StrictHostKeyChecking", strictHostKeyChecking ? "yes" : "no");
            session.setPassword(password);
            session.connect();

            channel = (ChannelSftp) session.openChannel("sftp");
            channel.connect();
            if(log.isDebugEnabled()){
                log.debug("Connected, going to the home dir: "+channel.getHome());
            }
            channel.cd(channel.getHome());

            return processCommands(channel);

        } catch (Exception e) {
            throw new PaxmlRuntimeException(e);
        } finally {
            try {
                if (channel != null) {
                    channel.exit();
                }
            } finally {
                if (session != null) {
                    session.disconnect();
                }
                if (log.isDebugEnabled()) {
                    log.debug("Sftp disconnected.");
                }
            }
        }

    }

    private Object processCommands(ChannelSftp channel) throws Exception {
        Object result = null;
        if ("get".equals(action)) {
            if (log.isDebugEnabled()) {
                log.debug("Sftp get from '" + from + "' to '" + to + "'");
            }
            ensureFrom();
            if (StringUtils.isBlank(to)) {
                // return content as result
                ByteArrayOutputStream out = new ByteArrayOutputStream();
                channel.get(from, out);
                result = out.toString("UTF-8");
            } else {
                channel.get(from, to);
            }
            doPostOperations(channel, from);
        } else if ("put".equals(action)) {
            if (log.isDebugEnabled()) {
                log.debug("Sftp put from '" + from + "' to '" + to + "'");
            }
            ensureTo();
            if (StringUtils.isBlank(from)) {
                // put value as content
                Object val = getValue();
                if (val == null) {
                    throw new PaxmlRuntimeException("Sftp command wrong: no value to put on remote server");
                }
                InputStream in = new ByteArrayInputStream(String.valueOf(val).getBytes("UTF-8"));
                channel.put(in, to);
            } else {
                channel.put(from, to);
            }
            doPostOperations(channel, to);
        } else if ("move".equals(action)) {
            if (log.isDebugEnabled()) {
                log.debug("Sftp move from '" + from + "' to '" + to + "'");
            }
            ensureFrom();
            ensureTo();
            channel.rename(from, to);
        } else if ("delete".equals(action)) {
            if (log.isDebugEnabled()) {
                log.debug("Sftp delete from: " + from);
            }
            ensureFrom();
            channel.rm(from);
        } else if ("mkdir".equals(action)) {
            if (log.isDebugEnabled()) {
                log.debug("Sftp mkdir to: " + to);
            }
            ensureTo();
            channel.mkdir(to);
        } else if ("list".equals(action)) {
            if (log.isDebugEnabled()) {
                log.debug("Sftp list from: " + from);
            }
            ensureFrom();
            result = channel.ls(from);
        } else {
            throw new PaxmlRuntimeException("Unknown sftp action: " + action);
        }

        return result;
    }

    private void doPostOperations(ChannelSftp channel, String org) throws SftpException {
        if (StringUtils.isNotBlank(postMove)) {
            if (log.isDebugEnabled()) {
                log.debug("Sft post moving from '" + org + "' to '" + postMove + "'");
            }
            channel.rename(org, postMove);
        }
        if (postDelete) {
            if (log.isDebugEnabled()) {
                log.debug("Sft post deleting: " + org);
            }
            channel.rm(org);
        }
    }

    private void ensureFrom() {
        if (StringUtils.isBlank(from)) {
            throw new PaxmlRuntimeException("No 'from' parameter given in sftp command");
        }
    }

    private void ensureTo() {
        if (StringUtils.isBlank(to)) {
            throw new PaxmlRuntimeException("No 'to' parameter given in sftp command");
        }
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public boolean isStrictHostKeyChecking() {
        return strictHostKeyChecking;
    }

    public void setStrictHostKeyChecking(boolean strictHostKeyChecking) {
        this.strictHostKeyChecking = strictHostKeyChecking;
    }

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public String getTo() {
        return to;
    }

    public void setTo(String to) {
        this.to = to;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public String getPostMove() {
        return postMove;
    }

    public void setPostMove(String postMove) {
        this.postMove = postMove;
    }

    public boolean isPostDelete() {
        return postDelete;
    }

    public void setPostDelete(boolean postDelete) {
        this.postDelete = postDelete;
    }

}
