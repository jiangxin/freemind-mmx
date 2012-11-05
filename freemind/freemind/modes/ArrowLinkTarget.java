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
import freemind.modes.mindmapmode.MindMapArrowLinkModel;
import freemind.view.mindmapview.MapView;

/**
 * Stores targets of arrow links. It is used to enable cut/copy+paste for every
 * parts of links (ie. source, destination or both).
 * 
 * @author foltin
 * @date 27.10.2012
 */
public class ArrowLinkTarget extends ArrowLinkAdapter {
	private String mSourceLabel;

	/**
	 * @param pSource
	 * @param pTarget I am the target!
	 * @param pFrame
	 */
	public ArrowLinkTarget(MindMapNode pSource, MindMapNode pTarget,
			FreeMindMain pFrame) {
		super(pSource, pTarget, pFrame);
	}

	public String getSourceLabel() {
		return mSourceLabel;
	}

	public void setSourceLabel(String sourceLabel) {
		mSourceLabel = sourceLabel;
	}

	public XMLElement save() {
		XMLElement arrowLink = super.save();
		arrowLink.setName("linktarget");
		if (getSourceLabel() != null) {
			arrowLink.setAttribute("SOURCE", getSourceLabel());
		}
		return arrowLink;
	}

	/* (non-Javadoc)
	 * @see freemind.modes.MindMapArrowLink#changeInclination(freemind.view.mindmapview.MapView, int, int, int, int)
	 */
	public void changeInclination(MapView pMap, int pOriginX, int pOriginY,
			int pDeltaX, int pDeltaY) {
	}

	public ArrowLinkAdapter createArrowLinkAdapter(MindMapLinkRegistry pRegistry) {
		ArrowLinkAdapter linkAdapter = new MindMapArrowLinkModel(source, target, frame);
		copy(linkAdapter);
		return linkAdapter;
	}


}
