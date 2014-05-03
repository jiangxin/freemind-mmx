/*
 * FreeMind - A Program for creating and viewing Mindmaps Copyright (C)
 * 2000-2004 Joerg Mueller, Daniel Polansky, Christian Foltin and others.
 * 
 * See COPYING for Details
 * 
 * This program is free software; you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software
 * Foundation; either version 2 of the License, or (at your option) any later
 * version.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU General Public License along with
 * this program; if not, write to the Free Software Foundation, Inc., 59 Temple
 * Place - Suite 330, Boston, MA 02111-1307, USA.
 * 
 * Created on 05.10.2004
 */
/*
 * $Id: ApplyPatternAction.java,v 1.16.10.1 05.10.2004 11:32:42 christianfoltin
 * Exp $
 */

package freemind.modes.mindmapmode.actions;

import freemind.controller.actions.generated.instance.Pattern;
import freemind.modes.EdgeAdapter;
import freemind.modes.MindMapNode;
import freemind.modes.StylePatternFactory;
import freemind.modes.mindmapmode.MindMapController;
import freemind.modes.mindmapmode.MindMapController.MindMapControllerPlugin;
import freemind.modes.mindmapmode.MindMapMapModel;
import freemind.modes.mindmapmode.MindMapNodeModel;

public class ApplyPatternAction extends NodeGeneralAction implements
		SingleNodeOperation {
	public interface ExternalPatternAction extends MindMapControllerPlugin {
		public void act(MindMapNode node, Pattern pattern);
	}

	private Pattern mpattern;

	public ApplyPatternAction(MindMapController controller, Pattern pattern) {
		super(controller, null /* no text */, null /* = no icon */);
		setName(pattern.getName());
		this.mpattern = pattern;
		setSingleNodeOperation(this);
	}

	public void apply(MindMapMapModel map, MindMapNodeModel node) {
		StylePatternFactory.applyPattern(node, mpattern, getMindMapController().getPatternsList(), getMindMapController().getPlugins(), getMindMapController());
	}

	public static String edgeWidthIntToString(int value) {
		if (value == EdgeAdapter.DEFAULT_WIDTH) {
			return null;
		}
		if (value == EdgeAdapter.WIDTH_THIN) {
			return EdgeAdapter.EDGE_WIDTH_THIN_STRING;
		}
		return Integer.toString(value);
	}

	/**
	 * @return Returns the pattern.
	 */
	public Pattern getPattern() {
		return mpattern;
	}

}
