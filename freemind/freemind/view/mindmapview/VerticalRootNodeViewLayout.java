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
import javax.swing.SwingUtilities;

import freemind.main.Tools;

/**
 * @author dimitri
 * 05.06.2005
 */
public class VerticalRootNodeViewLayout extends NodeViewLayoutAdapter {
    static private VerticalRootNodeViewLayout instance = null;
    
    protected void layout() {
        final int rightContentHeight = getChildContentHeight(false);
        int rightChildVerticalShift =  getChildVerticalShift(false);
        final int leftContentHeight = getChildContentHeight(true);
        int leftChildVerticalShift =  getChildVerticalShift(true);
        final int childHorizontalShift = getChildHorizontalShift();
        final int contentHeight = Math.max(rightContentHeight, leftContentHeight);
        final int x  = Math.max(getSpaceAround(), -childHorizontalShift);
        if(getView().getModel().isVisible()){
            getContent().setVisible(true);
            final Dimension contentPreferredSize = getContent().getPreferredSize();
            rightChildVerticalShift += (contentPreferredSize.height - rightContentHeight) / 2;
            leftChildVerticalShift += (contentPreferredSize.height - leftContentHeight) / 2;
            final int childVerticalShift = Math.min(rightChildVerticalShift, leftChildVerticalShift);
            final int y = Math.max(getSpaceAround(), - childVerticalShift );
            getContent().setBounds(x, y, contentPreferredSize.width, contentPreferredSize.height);
        }
        else{
            getContent().setVisible(false);
            int childVerticalShift = Math.min(rightChildVerticalShift, leftChildVerticalShift);
            final int y = Math.max(getSpaceAround(), - childVerticalShift );
            getContent().setBounds(x, y, 0, contentHeight);
        }
        
        placeLeftChildren(leftChildVerticalShift);
        int width1 = getView().getWidth();
        int height1 = getView().getHeight();
        placeRightChildren(rightChildVerticalShift);
        int width2 = getView().getWidth();
        int height2 = getView().getHeight();
        getView().setSize(Math.max(width1, width2), Math.max(height1, height2));
        
    }

   static VerticalRootNodeViewLayout getInstance() {
        if (instance == null) instance = new VerticalRootNodeViewLayout();
        return instance;
    }

public void layoutNodeMotionListenerView(NodeMotionListenerView view) {
    NodeView movedView = view.getMovedView();
    final JComponent content = movedView.getContent();
    location.x = 0;
    location.y = - LISTENER_VIEW_WIDTH;
    Tools.convertPointToAncestor(content, location, view.getParent());  
    view.setLocation(location);
    view.setSize(content.getWidth(), LISTENER_VIEW_WIDTH);
}

public Point getOutPoint(NodeView view, Point destinationPoint) {
    MainView mainView = view.getMainView();
    Point p = new Point(destinationPoint);
    Tools.convertPointFromAncestor(view, p, mainView);  
    double nWidth = mainView.getWidth();
    double nHeight = mainView.getHeight();
    Point centerPoint = new Point((int) (nWidth / 2f), (int) (nHeight / 2f));
    // assume, that destinationPoint is on the right:
    double angle = Math.atan((p.y - centerPoint.y + 0f)
            / (p.x - centerPoint.x + 0f));
    if (p.x < centerPoint.x) {
        angle += Math.PI;
    }
    // now determine point on ellipsis corresponding to that angle:
    Point out = new Point(centerPoint.x
            + (int) (Math.cos(angle) * nWidth / 2f), centerPoint.y
            + (int) (Math.sin(angle) * (nHeight) / 2f));
    return out;
}

public Point getInPoint(NodeView view) {
    final Point centerPoint = view.getMainView().getCenterPoint();
    return centerPoint;
}

}
