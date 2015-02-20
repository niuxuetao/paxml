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
package org.paxml.log4j;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;

import org.apache.log4j.FileAppender;
import org.apache.log4j.spi.LoggingEvent;
import org.paxml.core.Context;

public class PerProcessFileAppender extends FileAppender {
	private boolean duplicate;
	private final HashMap<String, FileAppender> appenders = new HashMap<String, FileAppender>();

	@Override
	public void append(LoggingEvent event) {
		Context context = Context.getCurrentContext();
		if (context == null) {
			super.append(event);
		} else {
			if (duplicate) {
				super.append(event);
			}
			final long pid = context.getProcessId();
			final String key = getProcessFileName(pid);
			FileAppender appender = null;
			synchronized (appenders) {
				appender = appenders.get(key);
				if (appender == null) {
					appender = new FileAppender();
					appender.setLayout(getLayout());
					appender.setAppend(getAppend());
					appender.setBufferSize(getBufferSize());
					appender.setBufferedIO(getBufferedIO());
					appender.setEncoding(getEncoding());
					appender.setImmediateFlush(getImmediateFlush());
					appender.setThreshold(getThreshold());
					appender.setName(getName() + "_" + pid);
					appender.setFile(getProcessFileName(pid));
					try {
						appender.setWriter(new FileWriter(appender.getFile(), appender.getAppend()));
					} catch (IOException e) {
						throw new RuntimeException("Cannot write to log file: " + new File(appender.getFile()).getAbsolutePath(), e);
					}
					appenders.put(key, appender);
				}
			}
			appender.append(event);
		}
	}

	@Override
	public synchronized void close() {
		super.close();
		for (FileAppender appender : appenders.values()) {
			appender.finalize();
			appender.close();
		}
	}

	private String getProcessFileName(long pid) {
		File f = new File(getFile());
		if (pid == 0) {
			return f.getAbsolutePath();
		}
		return new File(f.getParent(), pid + "." + Thread.currentThread().getName() + ".log").getAbsolutePath();
	}

	public boolean getDuplicate() {
		return duplicate;
	}

	public void setDuplicate(boolean duplicate) {
		this.duplicate = duplicate;
	}
}