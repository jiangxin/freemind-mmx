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
/*$Id: EdgeAdapter.java,v 1.14.12.2 2004-10-17 21:22:54 christianfoltin Exp $*/

package freemind.modes;

import freemind.main.FreeMindMain;
import freemind.main.Tools;
import freemind.main.XMLElement;

import java.awt.Color;
import java.awt.Stroke;
import java.awt.BasicStroke;

public abstract class EdgeAdapter extends LineAdapter implements MindMapEdge {

	public static final int WIDTH_PARENT = -1;
	public static final int WIDTH_THIN = 0;

    public EdgeAdapter(MindMapNode target,FreeMindMain frame) {
        this(target, frame, "standardedgecolor", "standardedgestyle");
    }

    /** For derived classes.*/
    protected  EdgeAdapter(MindMapNode target,FreeMindMain frame, String standardColorPropertyString, String standardStylePropertyString)  {
        super(target, frame, standardColorPropertyString, standardStylePropertyString);
        NORMAL_WIDTH = WIDTH_PARENT;
    }

    //
    // Attributes
    //

    public Color getColor() {
        if(color==null) {
            if (getTarget().isRoot()) {
                String stdcolor = getFrame().getProperty(standardColorPropertyString);
                if (stdcolor.length() == 7) {
                    return Tools.xmlToColor(stdcolor);
                }
                return Color.blue;
            }
            return getSource().getEdge().getColor();
        }
        return color;
    }

    public Color getRealColor() {
        return color;
    }
    public int getWidth() {
        if (width==WIDTH_PARENT) {
            if (getTarget().isRoot()) {
                return WIDTH_THIN; }
            return getSource().getEdge().getWidth(); }
        return width; }

    public Stroke getStroke() {
        if (width==WIDTH_THIN)
            return null;
        if(stroke==null) {
            if (getTarget().isRoot()) {
                return null;
            }
            return getSource().getEdge().getStroke();
        }
        return stroke;
    }
		
    public void setWidth(int width) {
        this.width = width;
        stroke = ((width==WIDTH_PARENT) || (width==WIDTH_THIN)) ? null :
            new BasicStroke(getWidth(),BasicStroke.CAP_BUTT,BasicStroke.JOIN_MITER); }

    public int getRealWidth() {
        return width;
    }

    public String getStyle() {
        if(style==null) {
            if (getTarget().isRoot()) {
                return getFrame().getProperty(standardStylePropertyString);
            }
            return getSource().getEdge().getStyle();
        }
        return style;
    }

    ///////////
    // Private Methods
    /////////

    private MindMapNode getSource() {
        return target.getParentNode();
    }

    public XMLElement save() {
    if (style!=null || color!=null || width!=WIDTH_PARENT) {
        XMLElement edge = new XMLElement();
        edge.setName("edge");
    
        if (style != null) {
    	edge.setAttribute("STYLE",style);
        }
        if (color != null) {
    	edge.setAttribute("COLOR",Tools.colorToXml(color));
        }
        if (width != WIDTH_PARENT) {
    	    if (width == WIDTH_THIN)
    			edge.setAttribute("WIDTH","thin");
    		else
    			edge.setAttribute("WIDTH",Integer.toString(width));
        }
        return edge;
    }
    return null;
    }
}
