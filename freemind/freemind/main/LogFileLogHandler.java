/*FreeMind - A Program for creating and viewing Mindmaps
*Copyright (C) 2000-2012 Joerg Mueller, Daniel Polansky, Christian Foltin, Dimitri Polivaev and others.
*
*See COPYING for Details
*
*This program is free software; you can redistribute it and/or
*modify it under the terms of the GNU General Public License
*as published by the Free Software Foundation; either version 2
*of the License, or (at your option) any later version.
*
*This program is distributed in the hope that it will be useful,
*but WITHOUT ANY WARRANTY; without even the implied warranty of
*MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
*GNU General Public License for more details.
*
*You should have received a copy of the GNU General Public License
*along with this program; if not, write to the Free Software
*Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
*/

package freemind.main;

import java.util.logging.Handler;
import java.util.logging.LogRecord;

/**
 * @author foltin
 * @date 19.04.2012
 */
public class LogFileLogHandler extends Handler {

	private LogReceiver mLogReceiver = null;

	public interface LogReceiver {
		void receiveLog(LogRecord pRecord);
	}

	/**
	 * 
	 */
	public LogFileLogHandler(LogReceiver pLogReceiver) {
		mLogReceiver = pLogReceiver;
	}
	
	public LogFileLogHandler() {
	}
	
	/* (non-Javadoc)
	 * @see java.util.logging.Handler#publish(java.util.logging.LogRecord)
	 */
	public void publish(LogRecord pRecord) {
		if (mLogReceiver != null) {
			if (!isLoggable(pRecord)) {
				return;
			}
			mLogReceiver.receiveLog(pRecord);
		}
	}

	/* (non-Javadoc)
	 * @see java.util.logging.Handler#flush()
	 */
	public void flush() {
	}

	/* (non-Javadoc)
	 * @see java.util.logging.Handler#close()
	 */
	public void close() throws SecurityException {
	}

	public LogReceiver getLogReceiver() {
		return mLogReceiver;
	}

	public void setLogReceiver(LogReceiver pLogReceiver) {
		mLogReceiver = pLogReceiver;
	}

}
