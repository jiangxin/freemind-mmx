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
/*$Id: EdgeView.java,v 1.4 2001-03-24 22:45:46 ponder Exp $*/

package freemind.view.mindmapview;

import freemind.modes.MindMapEdge;
import java.awt.Graphics2D;
import java.awt.Color;
import java.awt.Point;
import java.awt.Font;
import javax.swing.JLabel;

/**
 * This class represents a single Edge of a MindMap.
 */
public abstract class EdgeView {
    protected NodeView source,target;
    private JLabel label = new JLabel();
    protected Point start, end;
    private static int i;

    protected EdgeView(NodeView source, NodeView target) {
	this.source = source;
	this.target = target;
	label.setFont(new Font("Sans Serif",0,10));

	label.setText(getModel().toString());//Calling update() crashes BezierEdgeView

	getMap().add(label);
    }


    /**
     * This should be a task of MindMapLayout
     * start,end must be initialized...
     */
    public void update() {
	label.setText(getModel().toString());	
    }

    public void paint(Graphics2D g) {
	//	label.repaint();
    }

    public JLabel getLabel() {
	return label;
    }

    void remove() {
	getMap().remove(label);
    }

    public abstract Color getColor();

    protected MindMapEdge getModel() {
	return target.getModel().getEdge();
    }

    protected MapView getMap() {
	return source.getMap();
    }
}
