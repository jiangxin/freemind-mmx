/*FreeMind - A Program for creating and viewing Mindmaps
 *Copyright (C) 2000-2005  Joerg Mueller, Daniel Polansky, Christian Foltin and others.
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
 * Created on 09.01.2005
 */
/*$Id: ChangeArrowLinkEndPoints.java,v 1.1.2.1 2005-01-10 07:29:07 christianfoltin Exp $*/
package freemind.modes.actions;

import java.awt.Point;
import java.awt.event.ActionEvent;

import javax.xml.bind.JAXBException;

import freemind.controller.actions.ActionPair;
import freemind.controller.actions.ActorXml;
import freemind.controller.actions.FreemindAction;
import freemind.controller.actions.generated.instance.ArrowLinkPointXmlAction;
import freemind.controller.actions.generated.instance.ArrowLinkPointXmlAction;
import freemind.controller.actions.generated.instance.XmlAction;
import freemind.main.Tools;
import freemind.modes.ArrowLinkAdapter;
import freemind.modes.MindMapArrowLink;
import freemind.modes.MindMapLink;
import freemind.modes.MindMapLinkRegistry;
import freemind.modes.ModeController;
import freemind.modes.mindmapmode.MindMapArrowLinkModel;

/**
 * @author foltin
 *
 */
public class ChangeArrowLinkEndPoints extends FreemindAction  implements ActorXml {
    private ModeController controller;

	public ChangeArrowLinkEndPoints(ModeController modeController) {
        //fixme: icon wrong
		super("change_link_arrows", "images/designer.png",  modeController);
        this.controller = modeController;
        addActor(this);
    }

	public void setArrowLinkEndPoints(MindMapArrowLink link, Point startPoint,
			Point endPoint){
        controller.getActionFactory().startTransaction(
                (String) getValue(NAME));
        controller.getActionFactory().executeAction(
                getActionPair(link, startPoint, endPoint));
        controller.getActionFactory().endTransaction(
                (String) getValue(NAME));

	}
	
	/**
	 * @param link
	 * @param startPoint
	 * @param endPoint
	 * @return
	 */
	private ActionPair getActionPair(MindMapArrowLink link, Point startPoint, Point endPoint) {
		return new ActionPair(createArrowLinkPointXmlAction(link, startPoint, endPoint), 
				createArrowLinkPointXmlAction(link, link.getStartInclination(), link.getEndInclination()));
	}

	/* (non-Javadoc)
	 * @see freemind.controller.actions.ActorXml#act(freemind.controller.actions.generated.instance.XmlAction)
	 */
	public void act(XmlAction action) {
		if (action instanceof ArrowLinkPointXmlAction) {
			ArrowLinkPointXmlAction pointAction = (ArrowLinkPointXmlAction) action;
            MindMapArrowLink link = (MindMapArrowLink) getLinkRegistry().getLinkForID(pointAction.getId());
            link.setStartInclination(Tools.xmlToPoint(pointAction.getStartPoint()));
            link.setEndInclination(Tools.xmlToPoint(pointAction.getEndPoint()));
	        controller.nodeChanged(link.getSource());
	        controller.nodeChanged(link.getTarget());
		}
		
	}

	/* (non-Javadoc)
	 * @see freemind.controller.actions.ActorXml#getDoActionClass()
	 */
	public Class getDoActionClass() {
		return ArrowLinkPointXmlAction.class;
	}

	/* (non-Javadoc)
	 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
	 */
	public void actionPerformed(ActionEvent arg0) {
		// TODO Auto-generated method stub
		
	}
    private ArrowLinkPointXmlAction createArrowLinkPointXmlAction(MindMapArrowLink arrowLink,
            Point startPoint, Point endPoint){
        try {
            ArrowLinkPointXmlAction action = controller.getActionXmlFactory().createArrowLinkPointXmlAction();
            action.setStartPoint(Tools.PointToXml(startPoint));
            action.setEndPoint(Tools.PointToXml(endPoint));
            action.setId(arrowLink.getUniqueID());
            return action;
        } catch (JAXBException e) {
            e.printStackTrace();
            return null;
        }
    }
    /**
     * @return
     */
    private MindMapLinkRegistry getLinkRegistry() {
        return controller.getMap().getLinkRegistry();
    }


}
