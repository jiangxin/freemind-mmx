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

package freemind.modes;

import freemind.main.FreeMindMain;
import freemind.main.XMLElement;

/**
 * Stores targets of arrow links. It is used to enable cut/copy+paste for every
 * parts of links (ie. source, destination or both).
 * 
 * @author foltin
 * @date 27.10.2012
 */
public class ArrowLinkTarget {
	private String mUniqueId;
	private MindMapNode mSource;
	private String mSourceLabel;
	private MindMapNode mTarget;
	private FreeMindMain mFrame;

	/**
	 * @param pSource
	 * @param pTarget I am the target!
	 * @param pFrame
	 */
	public ArrowLinkTarget(MindMapNode pSource, MindMapNode pTarget,
			FreeMindMain pFrame) {
		mSource = pSource;
		setTarget(pTarget);
		mFrame = pFrame;
	}

	MindMapNode getSource() {
		return mSource;
	}

	/** The id is automatically set on creation. Is saved and restored. */
	public String getUniqueId() {
		return mUniqueId;
	}

	public void setUniqueId(String uniqueId) {
		mUniqueId = uniqueId;
	}

	public void setSource(MindMapNode source) {
		mSource = source;
	}

	public String getSourceLabel() {
		return mSourceLabel;
	}

	public void setSourceLabel(String sourceLabel) {
		mSourceLabel = sourceLabel;
	}

	public XMLElement save() {
		XMLElement arrowLink = new XMLElement();
		arrowLink.setName("linktarget");

		if (getUniqueId() != null) {
			arrowLink.setAttribute("ID", getUniqueId());
		}
		if (getSourceLabel() != null) {
			arrowLink.setAttribute("SOURCE", getSourceLabel());
		}
		return arrowLink;
	}

	public MindMapNode getTarget() {
		return mTarget;
	}

	public void setTarget(MindMapNode pTarget) {
		mTarget = pTarget;
	}
}
