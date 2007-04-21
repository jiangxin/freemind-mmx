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
 * Created on 05.06.2005
 *
 */
package freemind.view.mindmapview;

import java.awt.Container;
import java.awt.Dimension;
import java.awt.Point;

import javax.swing.JComponent;
import javax.swing.JLabel;

import freemind.main.Tools;

/**
 * @author dimitri
 * 05.06.2005
 */
public class RightNodeViewLayout extends NodeViewLayoutAdapter {
    static private RightNodeViewLayout instance = null;
    
    protected void layout() {
        final int contentHeight = getChildContentHeight(false);
        int childVerticalShift =  getChildVerticalShift(false);
        final int childHorizontalShift = getChildHorizontalShift();
        
        if(getView().getModel().isVisible()){
            getContent().setVisible(true);
            final Dimension contentPreferredSize = getContent().getPreferredSize();
            final int x = Math.max(getSpaceAround(), -contentPreferredSize.width -childHorizontalShift);
            childVerticalShift += (contentPreferredSize.height - contentHeight) / 2;
            final int y = Math.max(getSpaceAround(), - childVerticalShift );
            getContent().setBounds(x, y , contentPreferredSize.width, contentPreferredSize.height);
        }
        else{
            getContent().setVisible(false);
            final int x = Math.max(getSpaceAround(), -childHorizontalShift);
            final int y = Math.max(getSpaceAround(), - childVerticalShift );
            getContent().setBounds(x, y, 0, contentHeight);
        }
        
        placeRightChildren(childVerticalShift);
    }

   static RightNodeViewLayout getInstance() {
        if (instance == null) instance = new RightNodeViewLayout();
        return instance;
    }

public void layoutNodeMotionListenerView(NodeMotionListenerView view) {
    NodeView movedView = view.getMovedView();
    final JComponent content = movedView.getContent();
    location.x = -LISTENER_VIEW_WIDTH;
    location.y = 0;
    Tools.convertPointToAncestor(content, location, view.getParent());  
    view.setLocation(location);
    view.setSize(LISTENER_VIEW_WIDTH, content.getHeight());
}

public Point getOutPoint(NodeView view, Point destinationPoint) {
    final Point centerPoint = view.getMainView().getCenterPoint();
    centerPoint.x = view.getContent().getWidth() - 1;
    return centerPoint;
}

public Point getInPoint(NodeView view) {
    final Point centerPoint = view.getMainView().getCenterPoint();
    centerPoint.x =  0;
    return centerPoint;
}

}
