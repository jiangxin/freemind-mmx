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
/*$Id: BrowseXMLElement.java,v 1.6.18.1 2004-12-19 09:00:40 christianfoltin Exp $*/


package freemind.modes.browsemode;

import freemind.main.XMLElement;
import freemind.main.FreeMindMain;
import freemind.main.Tools;
import freemind.modes.NodeAdapter;
import freemind.modes.EdgeAdapter;
import freemind.modes.CloudAdapter;
import freemind.modes.ArrowLinkAdapter;
import freemind.modes.MindIcon;
import freemind.modes.XMLElementAdapter;
import freemind.modes.browsemode.BrowseEdgeModel;
import freemind.modes.browsemode.BrowseCloudModel;

import java.awt.Font;
import java.util.Vector;
import java.util.HashMap;

public class BrowseXMLElement extends XMLElementAdapter {

   public BrowseXMLElement(FreeMindMain frame) {
       super(frame);
   }

    protected BrowseXMLElement(FreeMindMain frame, Vector ArrowLinkAdapters, HashMap IDToTarget) {
        super(frame, ArrowLinkAdapters, IDToTarget);
    }

    /** abstract method to create elements of my type (factory).*/
    protected XMLElement  createAnotherElement(){
    // We do not need to initialize the things of XMLElement.
        return new BrowseXMLElement(getFrame(), ArrowLinkAdapters, IDToTarget);
    }
    protected NodeAdapter createNodeAdapter(FreeMindMain     frame, String nodeClass){
        return new BrowseNodeModel(frame);
    }
    protected EdgeAdapter createEdgeAdapter(NodeAdapter node, FreeMindMain frame){
        return new BrowseEdgeModel(node, frame); 
    }
    protected CloudAdapter createCloudAdapter(NodeAdapter node, FreeMindMain frame){
        return new BrowseCloudModel(node, frame); 
    }
    protected ArrowLinkAdapter createArrowLinkAdapter(NodeAdapter source, NodeAdapter target, FreeMindMain frame) {
        return new BrowseArrowLinkModel(source,target,frame);
    }

}

