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

import freemind.controller.actions.generated.instance.ArrowLinkArrowXmlAction;
import freemind.controller.actions.generated.instance.XmlAction;
import freemind.modes.ArrowLinkAdapter;
import freemind.modes.ExtendedMapFeedback;
import freemind.modes.MindMapArrowLink;
import freemind.modes.MindMapLink;
import freemind.modes.mindmapmode.actions.xml.ActionPair;

/**
 * @author foltin
 * @date 01.04.2014
 */
public class ChangeArrowsInArrowLinkActor extends XmlActorAdapter {

	/**
	 * @param pMapFeedback
	 */
	public ChangeArrowsInArrowLinkActor(ExtendedMapFeedback pMapFeedback) {
		super(pMapFeedback);
	}
	
	public void changeArrowsOfArrowLink(MindMapArrowLink arrowLink,
			boolean hasStartArrow, boolean hasEndArrow) {
		execute(getActionPair(arrowLink, hasStartArrow, hasEndArrow));
	}

	/**
     */
	private ActionPair getActionPair(MindMapArrowLink arrowLink2,
			boolean hasStartArrow2, boolean hasEndArrow2) {
		return new ActionPair(createArrowLinkArrowXmlAction(arrowLink2,
				hasStartArrow2, hasEndArrow2), createArrowLinkArrowXmlAction(
				arrowLink2, arrowLink2.getStartArrow(),
				arrowLink2.getEndArrow()));
	}

	public void act(XmlAction action) {
		if (action instanceof ArrowLinkArrowXmlAction) {
			ArrowLinkArrowXmlAction arrowAction = (ArrowLinkArrowXmlAction) action;
			MindMapLink link = getLinkRegistry().getLinkForId(
					arrowAction.getId());
			((ArrowLinkAdapter) link)
					.setStartArrow(arrowAction.getStartArrow());
			((ArrowLinkAdapter) link).setEndArrow(arrowAction.getEndArrow());
			getExMapFeedback().nodeChanged(link.getSource());
			getExMapFeedback().nodeChanged(link.getTarget());
		}
	}

	public Class getDoActionClass() {
		return ArrowLinkArrowXmlAction.class;
	}

	private ArrowLinkArrowXmlAction createArrowLinkArrowXmlAction(
			MindMapArrowLink arrowLink, boolean hasStartArrow,
			boolean hasEndArrow) {
		return createArrowLinkArrowXmlAction(arrowLink,
				(hasStartArrow) ? "Default" : "None", (hasEndArrow) ? "Default"
						: "None");
	}

	private ArrowLinkArrowXmlAction createArrowLinkArrowXmlAction(
			MindMapArrowLink arrowLink, String hasStartArrow,
			String hasEndArrow) {
		ArrowLinkArrowXmlAction action = new ArrowLinkArrowXmlAction();
		action.setStartArrow(hasStartArrow);
		action.setEndArrow(hasEndArrow);
		action.setId(arrowLink.getUniqueId());
		return action;
	}


}
