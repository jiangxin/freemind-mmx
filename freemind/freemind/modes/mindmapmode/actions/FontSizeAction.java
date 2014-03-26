/*FreeMind - A Program for creating and viewing Mindmaps
 *Copyright (C) 2000-2004  Joerg Mueller, Daniel Polansky, Christian Foltin and others.
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
 * Created on 27.08.2004
 */


package freemind.modes.mindmapmode.actions;

import freemind.modes.mindmapmode.MindMapController;
import freemind.modes.mindmapmode.MindMapNodeModel;
import freemind.modes.mindmapmode.actions.xml.ActionPair;

/**
 * @author foltin
 * 
 */
public class FontSizeAction extends NodeGeneralAction {

	/** This action is used for all sizes, which have to be set first. */
	private String actionSize;

	/**
     */
	public FontSizeAction(MindMapController modeController) {
		super(modeController, "font_size", null, (NodeActorXml) null);
		// default value:
		actionSize = modeController.getFrame().getProperty("defaultfontsize");
	}

	public void actionPerformed(String size) {
		this.actionSize = size;
		super.actionPerformed(null);
	}

	/* (non-Javadoc)
	 * @see freemind.modes.mindmapmode.actions.NodeGeneralAction#getActionPair(freemind.modes.mindmapmode.MindMapNodeModel)
	 */
	@Override
	protected ActionPair getActionPair(MindMapNodeModel pSelected) {
		return getMindMapController().getActorFactory().getFontSizeActor().getActionPair(pSelected, actionSize);
	}
}
