/*FreeMind - A Program for creating and viewing Mindmaps
 *Copyright (C) 2000  Joerg Mueller <joergmueller@bigfoot.com>
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

package freemind.view.mindmapview;

import freemind.main.FreeMind;
import java.awt.LayoutManager;
import java.awt.Container;
import java.awt.Component;
import java.awt.Dimension;
import java.util.Vector;
import java.lang.Math;

/**
 * This class will Layout the Nodes and Edges of an MapView.
 */
public class MindMapLayout implements LayoutManager {

    private int hgap = 15;//width of the gap that contains the edges
    private MapView map;
    private int ySize = Integer.parseInt(FreeMind.userProps.getProperty("mapysize"));
    private int minXSize = Integer.parseInt(FreeMind.userProps.getProperty("mapxsize"));
    private int totalXSize;

    public MindMapLayout(MapView map) {
	this.map = map;
    }
    

    public void addLayoutComponent(String name, Component comp){
    }

    public void removeLayoutComponent(Component comp) {
    }

    public void layoutContainer(Container parent) {
	if ( parent instanceof MapView ) {
	    int ncomponents = parent.getComponentCount();
	    
	    if (ncomponents == 0) return; //nothing to do

	    Dimension size = parent.getSize();
	    int totalW = size.width;
	    int totalH = size.height;

	    //Calculating the necessary size of container
	    int maxX = 0;
	    int minX = 0;

	    int xRoot = getTotalXSize() / 2;
	    int yRoot = totalH / 2;

	    //Place the nodes right and left from root
	    //do Layout
	    //maybe browsing the tree structure is better than linear layouting
	    for ( int i = 0; i < ncomponents; i++ ) {
		NodeView c = (NodeView)parent.getComponent(i);
		int x = xRoot + getXPos(c);
		int y = yRoot + c.getPosition().y ;
		//Paint every column a bit deeper(Just looks better)
		//y += Math.abs(c.getPosition().x) * 5;
		c.setBounds(x,y,c.getPreferredSize().width,c.getPreferredSize().height);
	    }
	} else {
	    throw new IllegalArgumentException("cannot layout: container must be a MapView");
	}
    }

    public int getXPos(NodeView node) {
	int hgap = (int)(this.hgap * map.getZoom());
	int x = 0;
	if(node.isRoot()) {
	} else if(node.isLeft()) {
	    while (!node.isRoot()) {
		x -= node.getPreferredSize().width + hgap;
		node = node.getParentView();
	    }
	} else {
	    while (!node.isRoot()) {
		NodeView parentView = node.getParentView();
		x += parentView.getPreferredSize().width + hgap;
		node = parentView;
	    }
	}
	return x;
    }

    public void updateTotalXSize() {
	int maxX = 0;
	int minX = 0;
	for (int i = 0;i < map.getComponentCount();i++) {
	    NodeView c = (NodeView)map.getComponent(i);
	    int x = getXPos(c);
	    if (c.isLeft()) { //left
		if (x < minX) {
		    minX = x;
		}
	    } else { //right
		//getXPos returns the upper left point of node, but we need the total distance from root:
		x += c.getPreferredSize().width;
		if (x > maxX) {
		    maxX = x;
		}
	    }
	}
	minX = -minX;
	if (maxX > minX) {
	    totalXSize = (maxX * 2)+30;
	} else {
	    totalXSize = (minX * 2)+30;
	}
	if (totalXSize < minXSize) {
	    totalXSize = minXSize;
	}
    }

    public int getTotalXSize() {
	return totalXSize;
    }

    public Dimension minimumLayoutSize(Container parent) {
	return new Dimension(200,200);//For testing Purposes
    }

    public Dimension preferredLayoutSize(Container parent) {
	updateTotalXSize();
	return new Dimension(getTotalXSize(),ySize);
    }

}//class MindMapLayout
