/*FreeMind - A Program for creating and viewing Mindmaps
*Copyright (C) 2000-2006 Joerg Mueller, Daniel Polansky, Christian Foltin, Dimitri Polivaev and others.
*
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
/*
 * Created on 07.04.2005
 */
package freemind.view.mindmapview;

import java.awt.Dimension;

import javax.swing.JLabel;

import freemind.modes.MindMapNode;
import freemind.view.mindmapview.attributeview.AttributeView;

/**
 * @author Dimitri
 */
public abstract class MoveableNodeView extends NodeView {

	private final int LISTENER_VIEW_WIDTH = 10; 
	private NodeMotionListenerView motionListenerView;
	
	protected MoveableNodeView(MindMapNode model, MapView map) {
		super(model, map);
		motionListenerView = new NodeMotionListenerView(this);
	}
	public NodeMotionListenerView getMotionListenerView() {
		return motionListenerView;
	}

    protected void addToMap(){
        map.add(getMotionListenerView(), 0);
    	map.add(this);
    }
    
    protected void removeFromMap(){
    	map.remove(this);
    	map.remove(getMotionListenerView());
    }

	public void setLocation(int x,	int y){
	    super.setLocation(x, y);
		int motionListenerViewX 
		  = isLeft() ? x + getWidth() : x-LISTENER_VIEW_WIDTH;
		motionListenerView.setLocation(motionListenerViewX, y);
		motionListenerView.setSize(LISTENER_VIEW_WIDTH, getHeight());
	}

	    public void setVisible(boolean isVisible) {
        super.setVisible(isVisible);
        motionListenerView.setVisible(isVisible);
    }
}
