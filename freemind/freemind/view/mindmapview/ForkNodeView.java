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
/*$Id: ForkNodeView.java,v 1.6 2003-11-03 10:15:46 sviles Exp $*/

package freemind.view.mindmapview;

import freemind.modes.NodeAdapter;//This should not be done.
import freemind.modes.MindMapNode;
import java.awt.*;
import javax.swing.ImageIcon;
import javax.swing.Icon;


/**
 * This class represents a single Fork-Style Node of a MindMap
 * (in analogy to TreeCellRenderer).
 */
public class ForkNodeView extends NodeView {


    //
    // Constructors
    //
    
    public ForkNodeView(MindMapNode model, MapView map) {
	super(model,map);
    }

    public Dimension getPreferredSize() {
	return new Dimension(super.getPreferredSize().width, super.getPreferredSize().height+3);
    }	
  
    /**
     * Paints the node
     */
    public void paint(Graphics graphics) {
	Graphics2D g = (Graphics2D)graphics;
	Dimension size = getSize();

	if (this.getModel()==null) return;

        paintSelected(g, size);
        paintDragOver(g, size);

	//Draw a standard node
	g.setColor(getEdge().getColor());
	g.drawLine(0,size.height-1,size.width,size.height-1);
   
	super.paint(g);
    }
}









