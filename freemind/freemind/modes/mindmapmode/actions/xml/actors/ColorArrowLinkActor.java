/*FreeMind - A Program for creating and viewing Mindmaps
*Copyright (C) 2000-2014 Christian Foltin, Joerg Mueller, Daniel Polansky, Dimitri Polivaev and others.
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

package freemind.modes.mindmapmode.actions.xml.actors;

import java.awt.Color;

import freemind.controller.actions.generated.instance.ArrowLinkColorXmlAction;
import freemind.controller.actions.generated.instance.XmlAction;
import freemind.main.Tools;
import freemind.modes.ExtendedMapFeedback;
import freemind.modes.LineAdapter;
import freemind.modes.MindMapLink;
import freemind.modes.MindMapLinkRegistry;
import freemind.modes.mindmapmode.actions.xml.ActionPair;

/**
 * @author foltin
 * @date 01.04.2014
 */
public class ColorArrowLinkActor extends XmlActorAdapter {

	/**
	 * @param pMapFeedback
	 */
	public ColorArrowLinkActor(ExtendedMapFeedback pMapFeedback) {
		super(pMapFeedback);
	}

	public void setArrowLinkColor(MindMapLink arrowLink, Color color) {
		execute(getActionPair(arrowLink, color));
	}

	/**
     */
	private ActionPair getActionPair(MindMapLink arrowLink, Color color) {
		return new ActionPair(createArrowLinkColorXmlAction(arrowLink, color),
				createArrowLinkColorXmlAction(arrowLink, arrowLink.getColor()));
	}

	public void act(XmlAction action) {
		if (action instanceof ArrowLinkColorXmlAction) {
			ArrowLinkColorXmlAction colorAction = (ArrowLinkColorXmlAction) action;
			MindMapLink link = getLinkRegistry().getLinkForId(
					colorAction.getId());
			((LineAdapter) link).setColor(Tools.xmlToColor(colorAction
					.getColor()));
			getExMapFeedback().nodeChanged(link.getSource());
		}
	}

	public Class getDoActionClass() {
		return ArrowLinkColorXmlAction.class;
	}

	private ArrowLinkColorXmlAction createArrowLinkColorXmlAction(
			MindMapLink arrowLink, Color color) {
		ArrowLinkColorXmlAction action = new ArrowLinkColorXmlAction();
		action.setColor(Tools.colorToXml(color));
		action.setId(arrowLink.getUniqueId());
		return action;
	}

}
