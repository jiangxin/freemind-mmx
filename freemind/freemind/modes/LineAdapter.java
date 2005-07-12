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
/*$Id: LineAdapter.java,v 1.2.18.1.10.1 2005-07-12 15:41:15 dpolivaev Exp $*/

package freemind.modes;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Stroke;

import freemind.main.FreeMindMain;
import freemind.main.Tools;
import freemind.preferences.FreemindPropertyListener;

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
        updateStandards();

    }

    //
    // Attributes
    //

    /**
     * @param standardColorPropertyString
     */
    protected void updateStandards() {
        if (getStandardColor() == null) {
            String stdColor = getFrame().getProperty(
                    standardColorPropertyString);
            if (stdColor != null && stdColor.length() == 7) {
                setStandardColor(Tools.xmlToColor(stdColor));
            } else {
                setStandardColor(Color.RED);
            }
        }
        if (getStandardStyle() == null) {
            String stdStyle = getFrame().getProperty(
                    standardStylePropertyString);
            if (stdStyle != null ) {
                setStandardStyle(stdStyle);
            } else {
//                setStandardStyle(Style.RED);
            }
        }
    }

    public FreeMindMain getFrame() {
        return frame;
    }

    public Color getColor() {
        if(color==null) {
            return getStandardColor();
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
            return getStandardStyle();
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
    /** As this color is static but used in at least three different 
     * objects (edges, clouds and links), the abstract mechanism was chosen.
     * The derived classes set and get the static instance variable. */
    protected abstract void setStandardColor(Color standardColor);

    /** See @see setStandardColor
     */
    protected abstract Color getStandardColor();
    
    protected abstract void setStandardStyle(String standardStyle);

    protected abstract String getStandardStyle() ;

    protected class LineAdapterListener implements FreemindPropertyListener {
        public void propertyChanged(String propertyName,
                String newValue, String oldValue) {
            if (propertyName.equals(standardColorPropertyString)) {
                setStandardColor(Tools.xmlToColor(newValue));
            }
            if (propertyName.equals(standardStylePropertyString)) {
                setStandardStyle(newValue);
            }
        }
    }
    
}
