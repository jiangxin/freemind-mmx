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
/*$Id: MindMapXMLElement.java,v 1.13 2003-12-07 21:00:27 christianfoltin Exp $*/


package freemind.modes.mindmapmode;

import freemind.main.XMLElement;
import freemind.main.FreeMindMain;
import freemind.main.Tools;
import freemind.modes.NodeAdapter;
import freemind.modes.EdgeAdapter;
import freemind.modes.CloudAdapter;
import freemind.modes.ArrowLinkAdapter;
import freemind.modes.XMLElementAdapter;
import freemind.modes.MindIcon;
import freemind.modes.MindMapLinkRegistry;

import java.awt.Font;
import java.util.Vector;
import java.util.HashMap;
import java.util.Collection;
import java.util.Iterator;

public class MindMapXMLElement extends XMLElementAdapter {

   public MindMapXMLElement(FreeMindMain frame) {
       super(frame);
   }

    protected MindMapXMLElement(FreeMindMain frame, Vector ArrowLinkAdapters, HashMap IDToTarget) {
        super(frame, ArrowLinkAdapters, IDToTarget);
    }

    /** abstract method to create elements of my type (factory).*/
    protected XMLElement  createAnotherElement(){
    // We do not need to initialize the things of XMLElement.
        return new MindMapXMLElement(getFrame(), ArrowLinkAdapters, IDToTarget);
    }
    protected NodeAdapter createNodeAdapter(FreeMindMain     frame){
        return new MindMapNodeModel(frame);
    }
    protected EdgeAdapter createEdgeAdapter(NodeAdapter node, FreeMindMain frame){
        return new MindMapEdgeModel(node, frame); 
    }
    protected CloudAdapter createCloudAdapter(NodeAdapter node, FreeMindMain frame){
        return new MindMapCloudModel(node, frame); 
    }
    protected ArrowLinkAdapter createArrowLinkAdapter(NodeAdapter source, NodeAdapter target, FreeMindMain frame) {
        return new MindMapArrowLinkModel(source,target,frame);
    }

}


