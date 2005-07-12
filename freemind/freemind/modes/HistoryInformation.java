/*FreeMind - A Program for creating and viewing Mindmaps
 *Copyright (C) 2000-2005  Joerg Mueller, Daniel Polansky, Christian Foltin and others.
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
 *
 * Created on 05.07.2005
 */
/*$Id: HistoryInformation.java,v 1.1.4.2 2005-07-12 15:41:15 dpolivaev Exp $*/
package freemind.modes;

import java.util.Date;

/**
 * Here, the creation and modification times of objects (by now, only for
 * nodes) are stored.
 * 
 * The storage as longs is preferred as they are normally inlined by the Java
 * compiler.
 * 
 * @author foltin
 *
 */
public class HistoryInformation {
	long createdAt=0l;
	long lastModifiedAt=0l;

	/**
	 * Initializes to today.
	 */
	public HistoryInformation() {
		long now = new Date().getTime();
		createdAt = now;
		lastModifiedAt = now;
	}
	public HistoryInformation(Date createdAt, Date lastModifiedAt) {
		this.createdAt = createdAt.getTime();
		this.lastModifiedAt = lastModifiedAt.getTime();
	}
	public Date getCreatedAt() {
		return new Date(createdAt);
	}
	public Date getLastModifiedAt() {
		return new Date(lastModifiedAt);
	}
	public void setCreatedAt(Date createdAt) {
		this.createdAt = createdAt.getTime();
	}
	public void setLastModifiedAt(Date lastModifiedAt) {
		this.lastModifiedAt = lastModifiedAt.getTime();
	}
}