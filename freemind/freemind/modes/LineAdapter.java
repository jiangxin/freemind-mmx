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
/*$Id: LineAdapter.java,v 1.2.18.1 2004-10-17 23:00:08 dpolivaev Exp $*/

package freemind.modes;

import freemind.main.FreeMindMain;
import freemind.main.Tools;
import java.awt.Color;
import java.awt.Stroke;
import java.awt.BasicStroke;

public abstract class LineAdapter implements MindMapLine {

    protected FreeMindMain frame;
    protected MindMapNode target;
    protected String standardColorPropertyString;
    protected String standardStylePropertyString;
    
	public static final int DEFAULT_WIDTH = -1;
	protected int NORMAL_WIDTH = 1;

    //recursive attributes. may be accessed directly by the save() method.
    protected Color color; 
    protected String style;
    protected int width;
	protected Stroke stroke;
    private String standardStyle;
    private Color standardColor;

    //
    // Constructors
    //
    public LineAdapter(MindMapNode target,FreeMindMain frame, String standardColorPropertyString, String standardStylePropertyString) {
        this.frame = frame;
        this.target = target;
        this.standardColorPropertyString = standardColorPropertyString;
        this.standardStylePropertyString = standardStylePropertyString;
        width = DEFAULT_WIDTH;
        stroke = null;
        standardStyle = getFrame().getProperty(standardStylePropertyString);
        String stdColor = getFrame().getProperty(standardColorPropertyString);
        if (stdColor != null && stdColor.length() == 7) {
            standardColor = Tools.xmlToColor(stdColor);
        } else {
            standardColor = Color.RED;
        }

    }

    //
    // Attributes
    //

    public FreeMindMain getFrame() {
        return frame;
    }

    public Color getColor() {
        if(color==null) {
            return standardColor;
        }
        return color;
    }

    public void setColor(Color color) {
        this.color = color;
    }


    public int getWidth() {
        if(width == DEFAULT_WIDTH)
            return NORMAL_WIDTH;
        return width; 
    }

    public Stroke getStroke() {
        return stroke;
    }
		
    public void setWidth(int width) {
        this.width = width;
        stroke = new BasicStroke(getWidth(),BasicStroke.CAP_BUTT,BasicStroke.JOIN_MITER); 
    }

    public String getStyle() {
        if(style==null) {
            return standardStyle;
        }
        return style;
    }

    public void setStyle(String style) {
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
    /** I see no reason to hide the node, the line belongs to, to the public, but... fc. */
    public MindMapNode getTarget() {
        return target;
    }

    public Object clone() {
        try {
            LineAdapter link = (LineAdapter) super.clone();
            // color, ...
            link.color = (color==null)?null:new Color(color.getRGB());
            return link;
        } catch(java.lang.CloneNotSupportedException e) {
            return null;
        }
    }

    
}
