/*FreeMind - A Program for creating and viewing Mindmaps
 *Copyright (C) 2000-2001  Joerg Mueller <joergmueller@bigfoot.com>
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
/*$Id: BrowseArrowLinkModel.java,v 1.1.18.1.16.1 2007/04/21 15:11:21 dpolivaev Exp $*/

package freemind.modes.browsemode;

import freemind.main.FreeMindMain;
import freemind.main.XMLElement;
import freemind.modes.ArrowLinkAdapter;
import freemind.modes.MindMapNode;
import freemind.view.mindmapview.MapView;

public class BrowseArrowLinkModel extends ArrowLinkAdapter {

	public BrowseArrowLinkModel(MindMapNode source, MindMapNode target,
			FreeMindMain frame) {
		super(source, target, frame);
	}

	/* maybe this method is wrong here, but ... */
	public Object clone() {
		return super.clone();
	}

	public XMLElement save() {
		return null;
	}

	public String toString() {
		return "Source=" + getSource() + ", target=" + getTarget();
	}

	/**
	 * @see freemind.modes.MindMapArrowLink#changeInclination(MapView, int, int,
	 *      int, int)
	 */
	public void changeInclination(MapView map, int oldX, int oldY, int deltaX,
			int deltaY) {

	}

}
