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

import java.awt.Point;

import freemind.controller.actions.generated.instance.ArrowLinkPointXmlAction;
import freemind.controller.actions.generated.instance.XmlAction;
import freemind.main.Tools;
import freemind.modes.ExtendedMapFeedback;
import freemind.modes.MindMapArrowLink;
import freemind.modes.MindMapLinkRegistry;
import freemind.modes.mindmapmode.actions.xml.ActionPair;

/**
 * @author foltin
 * @date 01.04.2014
 */
public class ChangeArrowLinkEndPointsActor extends XmlActorAdapter {

	/**
	 * @param pMapFeedback
	 */
	public ChangeArrowLinkEndPointsActor(ExtendedMapFeedback pMapFeedback) {
		super(pMapFeedback);
	}

	public void setArrowLinkEndPoints(MindMapArrowLink link, Point startPoint,
			Point endPoint) {
		execute(getActionPair(link, startPoint, endPoint));
	}

	/**
	 */
	private ActionPair getActionPair(MindMapArrowLink link, Point startPoint,
			Point endPoint) {
		return new ActionPair(createArrowLinkPointXmlAction(link, startPoint,
				endPoint), createArrowLinkPointXmlAction(link,
				link.getStartInclination(), link.getEndInclination()));
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * freemind.controller.actions.ActorXml#act(freemind.controller.actions.
	 * generated.instance.XmlAction)
	 */
	public void act(XmlAction action) {
		if (action instanceof ArrowLinkPointXmlAction) {
			ArrowLinkPointXmlAction pointAction = (ArrowLinkPointXmlAction) action;
			MindMapArrowLink link = (MindMapArrowLink) getLinkRegistry()
					.getLinkForId(pointAction.getId());
			link.setStartInclination(Tools.xmlToPoint(pointAction
					.getStartPoint()));
			link.setEndInclination(Tools.xmlToPoint(pointAction.getEndPoint()));
			getExMapFeedback().nodeChanged(link.getSource());
			getExMapFeedback().nodeChanged(link.getTarget());
		}

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see freemind.controller.actions.ActorXml#getDoActionClass()
	 */
	public Class getDoActionClass() {
		return ArrowLinkPointXmlAction.class;
	}

	private ArrowLinkPointXmlAction createArrowLinkPointXmlAction(
			MindMapArrowLink arrowLink, Point startPoint, Point endPoint) {
		ArrowLinkPointXmlAction action = new ArrowLinkPointXmlAction();
		action.setStartPoint(Tools.PointToXml(startPoint));
		action.setEndPoint(Tools.PointToXml(endPoint));
		action.setId(arrowLink.getUniqueId());
		return action;
	}

}
