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
/*$Id: CloudAdapter.java,v 1.1.4.1 2004-02-28 12:48:11 christianfoltin Exp $*/

package freemind.modes;

import freemind.main.FreeMindMain;
import freemind.main.Tools;
import java.awt.Color;
import java.awt.Stroke;
import java.awt.BasicStroke;

public abstract class CloudAdapter extends LineAdapter implements MindMapCloud {

    //
    // Constructors
    //
    public CloudAdapter(MindMapNode target,FreeMindMain frame) {
        this(target, frame, "standardcloudcolor", "standardcloudstyle");
    }

    /** For derived classes.*/
    protected  CloudAdapter(MindMapNode target,FreeMindMain frame, String standardColorPropertyString, String standardStylePropertyString)  {
        super(target, frame, standardColorPropertyString, standardStylePropertyString);
        NORMAL_WIDTH = 3;
        iterativeLevel = -1;
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
	
}
