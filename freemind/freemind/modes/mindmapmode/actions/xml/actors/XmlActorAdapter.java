/*FreeMind - A Program for creating and viewing Mindmaps
*Copyright (C) 2000-2014 Joerg Mueller, Daniel Polansky, Christian Foltin, Dimitri Polivaev and others.
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

import freemind.modes.ExtendedMapFeedback;
import freemind.modes.MindMapLinkRegistry;
import freemind.modes.MindMapNode;
import freemind.modes.NodeAdapter;
import freemind.modes.mindmapmode.MindMapController;
import freemind.modes.mindmapmode.actions.xml.ActionPair;
import freemind.modes.mindmapmode.actions.xml.ActorXml;
import freemind.view.mindmapview.ViewFeedback;

/**
 * @author foltin
 * @date 16.03.2014
 */
public abstract class XmlActorAdapter implements ActorXml {

	protected ExtendedMapFeedback mMapFeedback;
	private ActorXml mActor;
	protected static java.util.logging.Logger logger = null;

	/**
	 * 
	 */
	public XmlActorAdapter(ExtendedMapFeedback pMapFeedback) {
		mMapFeedback = pMapFeedback;
		if (logger == null) {
			logger = freemind.main.Resources.getInstance().getLogger(
					this.getClass().getName());
		}
		addActor(this);
	}

	
	/**
	 * @deprecated replaced by {@link XmlActorAdapter#getExMapFeedback()}
	 * @return
	 */
	@Deprecated
	protected MindMapController getModeController() {
		return (MindMapController) mMapFeedback;
	}

	/**
	 * @return the mapFeedback
	 */
	public ExtendedMapFeedback getExMapFeedback() {
		return mMapFeedback;
	}

	public ViewFeedback getViewFeedback() {
		return getExMapFeedback().getViewFeedback();
	}
	
	/**
	 * @param pActionPair
	 */
	protected void execute(ActionPair pActionPair) {
		getExMapFeedback().doTransaction(getDoActionClass().getName(), pActionPair);
		
	}

	/**
	 * @param pNodeId
	 * @return
	 */
	protected NodeAdapter getNodeFromID(String pNodeId) {
		return getExMapFeedback().getNodeFromID(pNodeId);
	}


	/**
	 * @return
	 */
	protected MindMapNode getSelected() {
		return getExMapFeedback().getSelected();
	}

	/**
	 * @param pSelected
	 * @return
	 */
	protected String getNodeID(MindMapNode pNode) {
		return getExMapFeedback().getNodeID(pNode);
	}

	protected void addActor(ActorXml actor) {
		this.mActor = actor;
		if (actor != null) {
			// registration:
			getExMapFeedback().getActionRegistry().registerActor(actor,
					actor.getDoActionClass());
		}
	}
	
	protected XmlActorFactory getXmlActorFactory() {
		return getExMapFeedback().getActorFactory();
	}


	/**
	 */
	protected MindMapLinkRegistry getLinkRegistry() {
		return getExMapFeedback().getMap().getLinkRegistry();
	}
	
}
