/*FreeMind - A Program for creating and viewing Mindmaps
 *Copyright (C) 2000-2007  Joerg Mueller, Daniel Polansky, Christian Foltin and others.
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
/*$Id: StdFormatter.java,v 1.1.2.5 2008/02/03 21:50:04 dpolivaev Exp $*/

package freemind.main;

import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.SimpleFormatter;

class StdFormatter extends SimpleFormatter {

	private static class StdOutErrLevel extends Level {
		public StdOutErrLevel(String name, int value) {
			super(name, value);
		}
	}

	/**
	 * Level for STDOUT activity.
	 */
	final static Level STDOUT = new StdOutErrLevel("STDOUT",
			Level.WARNING.intValue() + 53);

	/**
	 * Level for STDERR activity
	 */
	final static Level STDERR = new StdOutErrLevel("STDERR",
			Level.SEVERE.intValue() + 53);

	// Line separator string. This is the value of the line.separator
	// property at the moment that the SimpleFormatter was created.
	private String lineSeparator = System.getProperty("line.separator");

	/**
	 * Format the given LogRecord.
	 * 
	 * @param record
	 *            the log record to be formatted.
	 * @return a formatted log record
	 */
	public synchronized String format(LogRecord record) {
		if (!STDERR.getName().equals(record.getLoggerName())
				&& !STDOUT.getName().equals(record.getLoggerName())) {
			return super.format(record);
		}
		StringBuffer sb = new StringBuffer();
		sb.append(lineSeparator);
		String message = formatMessage(record);
		sb.append(record.getLevel().getLocalizedName());
		sb.append(": ");
		sb.append(message);
		return sb.toString();
	}
}
