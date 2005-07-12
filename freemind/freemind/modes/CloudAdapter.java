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
/*$Id: CloudAdapter.java,v 1.1.16.2.10.1 2005-07-12 15:41:14 dpolivaev Exp $*/

package freemind.modes;

import java.awt.Color;

import freemind.controller.Controller;
import freemind.main.FreeMind;
import freemind.main.FreeMindMain;
import freemind.main.Tools;
import freemind.main.XMLElement;

public abstract class CloudAdapter extends LineAdapter implements MindMapCloud {

    private static Color standardColor = null;
    private static String standardStyle = null;
    private static LineAdapterListener listener = null;
    //
    // Constructors
    //
    public CloudAdapter(MindMapNode target,FreeMindMain frame) {
        this(target, frame, FreeMind.RESOURCES_CLOUD_COLOR, "standardcloudstyle");
    }

    /** For derived classes.*/
    protected  CloudAdapter(MindMapNode target,FreeMindMain frame, String standardColorPropertyString, String standardStylePropertyString)  {
        super(target, frame, standardColorPropertyString, standardStylePropertyString);
        NORMAL_WIDTH = 3;
        iterativeLevel = -1;
        if(listener == null) {
            listener = new LineAdapterListener(); 
            Controller.addPropertyChangeListener(listener);
        }
        
    }
    /**
    *  calculates the cloud iterative level which 
    *  is importent for the cloud size
    */
    
    private void calcIterativeLevel(MindMapNode target) {
        iterativeLevel = 0;	
        if (target != null) {	
        	for(MindMapNode parentNode = target.getParentNode(); 
        	    parentNode != null; 
        	    parentNode = parentNode.getParentNode()) {
        	    	MindMapCloud cloud = parentNode.getCloud();
        	    	if (cloud != null) {
        				iterativeLevel = cloud.getIterativeLevel() + 1;
        	    		break;
        	    	} 
        	}
        }
    }

	public void setTarget(MindMapNode target) {
		super.setTarget(target); 
    }

    public Color getExteriorColor() {
        return getColor().darker();
    }

    /**  gets iterative level which is required for painting and layout. */
	public int getIterativeLevel() {
		if (iterativeLevel == -1) {
			calcIterativeLevel(target);
		}
		return iterativeLevel;
	}

	/**  changes the iterative level.*/
	public void changeIterativeLevel(int deltaLevel) {
		if (iterativeLevel != -1) {
			iterativeLevel = iterativeLevel + deltaLevel;
		}	
	}
	
	private int iterativeLevel;
	
    public XMLElement save() {
        XMLElement cloud = new XMLElement();
        cloud.setName("cloud");
    
        if (style != null) {
            cloud.setAttribute("STYLE",style);
        }
        if (color != null) {
            cloud.setAttribute("COLOR",Tools.colorToXml(color));
        }
        if(width != DEFAULT_WIDTH) {
            cloud.setAttribute("WIDTH",Integer.toString(width));
        }
        return cloud;
    }

    protected Color getStandardColor() {
        return standardColor;
    }
    protected void setStandardColor(Color standardColor) {
        CloudAdapter.standardColor = standardColor;
    }
    protected String getStandardStyle() {
        return standardStyle;
    }
    protected void setStandardStyle(String standardStyle) {
        CloudAdapter.standardStyle = standardStyle;
    }
}
