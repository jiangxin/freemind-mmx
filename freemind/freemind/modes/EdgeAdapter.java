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
/*$Id: EdgeAdapter.java,v 1.12 2003-11-03 11:00:12 sviles Exp $*/

package freemind.modes;

import freemind.main.FreeMindMain;
import freemind.main.Tools;
import java.awt.Color;
import java.awt.Stroke;
import java.awt.BasicStroke;

public abstract class EdgeAdapter implements MindMapEdge {

    private MindMapNode target;
    private FreeMindMain frame;
    
	public static final int WIDTH_PARENT = 0;
	public static final int WIDTH_THIN = -1;
//	public static final int WIDTH_PROPORTIONAL = -2;

    //recursive attributes. may be accessed directly by the save() method.
    protected Color color; 
    protected String style;
    protected int width = WIDTH_PARENT;
	protected Stroke stroke = null;

    //
    // Constructors
    //
    public EdgeAdapter(MindMapNode target,FreeMindMain frame) {
	this.frame = frame;
	this.target = target;
    }

    //
    // Attributes
    //

    public FreeMindMain getFrame() {
	return frame;
    }

    public Color getColor() {
	if(color==null) {
	    if (getTarget().isRoot()) {
		String stdcolor = getFrame().getProperty("standardedgecolor");
		if (stdcolor.length() == 7) {
		    return Tools.xmlToColor(stdcolor);
		}
		return Color.blue;
	    }
	    return getSource().getEdge().getColor();
	}
	return color;
    }

    protected void setColor(Color color) {
	this.color = color;
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

    public String getStyle() {
	if(style==null) {
	    if (getTarget().isRoot()) {
		return getFrame().getProperty("standardedgestyle");
	    }
	    return getSource().getEdge().getStyle();
	}
	return style;
    }

    protected void setStyle(String style) {
	this.style = style;
    }

    public String toString() {
	return "";
    }

    public void setTarget(MindMapNode target) {
       this.target = target; 
    }

    ///////////
    // Private Methods
    /////////

    private MindMapNode getTarget() {
  	return target;
    }
    
    private MindMapNode getSource() {
 	return target.getParentNode();
    }
}
