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
/*$Id: MindMapArrowLinkModel.java,v 1.5.18.1 2004-10-17 20:22:45 dpolivaev Exp $*/

package freemind.modes.mindmapmode;

import freemind.main.FreeMindMain;
import freemind.modes.MindMapNode;
import freemind.modes.ArrowLinkAdapter;
import freemind.main.Tools;
import java.awt.Color;
import java.awt.Point;

import freemind.main.XMLElement;
import freemind.view.mindmapview.NodeView;

public class MindMapArrowLinkModel extends ArrowLinkAdapter {

    public MindMapArrowLinkModel(MindMapNode source,MindMapNode target,FreeMindMain frame) {
        super(source,target,frame);
    }

    /* maybe this method is wrong here, but ...*/
    public Object clone() {
        return super.clone();
    }

    public XMLElement save() {
	    XMLElement arrowLink = new XMLElement();
	    arrowLink.setName("arrowlink");

	    if (style != null) {
            arrowLink.setAttribute("style",style);
	    }
	    if (color != null) {
            arrowLink.setAttribute("color",Tools.colorToXml(color));
	    }
        if(getDestinationLabel() != null) {
            arrowLink.setAttribute("destination",getDestinationLabel());
        }
        if(getReferenceText() != null) {
            arrowLink.setAttribute("referenceText",getReferenceText());
        }
        if(getStartInclination() != null) {
            arrowLink.setAttribute("startInclination",Integer.toString(getStartInclination().x) + ";" + Integer.toString(getStartInclination().y) + ";");
        }
        if(getEndInclination() != null) {
            arrowLink.setAttribute("endInclination",Integer.toString(getEndInclination().x) + ";" + Integer.toString(getEndInclination().y) + ";");
        }
        if(getStartArrow() != null)
            arrowLink.setAttribute("startArrow",(getStartArrow()));
        if(getEndArrow() != null)
            arrowLink.setAttribute("endArrow",(getEndArrow()));
	    return arrowLink;
    }

    public String toString() { return "Source="+getSource()+", target="+getTarget()+", "+save().toString(); }

    /* (non-Javadoc)
     * @see freemind.modes.MindMapArrowLink#changeInclination(int, int, int, int)
     */
    public void changeInclination(int originX, int originY, int deltaX, int deltaY) {
		NodeView targetNode = getTarget().getViewer();
		NodeView sourceNode = getSource().getViewer();
		double distSqToTarget = 0;
		double distSqToSource = 0;
		if(targetNode != null && sourceNode != null){
			Point targetLinkPoint =  targetNode.getLinkPoint(getEndInclination());
			Point sourceLinkPoint =  sourceNode.getLinkPoint(getStartInclination());
			distSqToTarget = targetLinkPoint.distanceSq(originX, originY);
			distSqToSource = sourceLinkPoint.distanceSq(originX, originY);
		}
		if(targetNode == null || sourceNode != null && distSqToSource < distSqToTarget * 2.25){
			Point changedInclination = getStartInclination();
            changeInclination(deltaX, deltaY, sourceNode, changedInclination);
		}

		if(sourceNode == null || targetNode != null && distSqToTarget < distSqToSource * 2.25){
			Point changedInclination = getEndInclination();
			changeInclination(deltaX, deltaY, targetNode, changedInclination);
		}
        
    }
    
    private void changeInclination(
        int deltaX,
        int deltaY,
        NodeView linkedNodeView,
        Point changedInclination) {
        if(linkedNodeView.isLeft() || linkedNodeView.isRoot()){
			deltaX = - deltaX;
        }
		changedInclination.translate(deltaX, deltaY);			
        if(changedInclination.x != 0 && Math.abs((double)changedInclination.y / changedInclination.x) < 0.015){
        	changedInclination.y = 0;
        }
        double k = changedInclination.distance(0,0);
        if(k < 10){
        	if (k > 0){
        		changedInclination.x = (int) (changedInclination.x * 10 / k);
        		changedInclination.y = (int) (changedInclination.y * 10 / k);
        	}
        	else{
        		changedInclination.x = 10;
        	}
        }
    }

}
