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
 * Created on 19.07.2004
 */


package freemind.modes.mindmapmode.actions;

import javax.swing.AbstractAction;
import javax.swing.Icon;
import javax.swing.ImageIcon;

import freemind.modes.mindmapmode.MindMapController;
import freemind.modes.mindmapmode.actions.xml.ActorXml;

/**
 * @author foltin
 *
 */
/**
 * @author foltin
 * 
 */
public abstract class FreemindAction extends AbstractAction {

	private Icon actionIcon;
	private static Icon selectedIcon;
	private final MindMapController pMindMapController;

	/**
	 * @param title
	 *            is a fixed title (no translation is done via resources)
	 */
	public FreemindAction(String title, Icon icon,
			MindMapController mindMapController) {
		super(title, icon);
		this.actionIcon = icon;
		this.pMindMapController = mindMapController;

	}

	/**
	 * @param title
	 *            Title is a resource.
	 * @param iconPath
	 *            is a path to an icon.
	 */
	public FreemindAction(String title, String iconPath,
			final MindMapController mindMapController) {
		this(mindMapController.getText(title), (iconPath == null) ? null
				: new ImageIcon(mindMapController.getResource(iconPath)),
				mindMapController);
	}

	public void addActor(ActorXml actor) {
		// registration:
		pMindMapController.getActionFactory().registerActor(actor,
				actor.getDoActionClass());
	}

	public MindMapController getMindMapController() {
		return pMindMapController;
	}
}
