/*FreeMind - A Program for creating and viewing Mindmaps
 *Copyright (C) 2000-2006  Joerg Mueller, Daniel Polansky, Christian Foltin, Dimitri Polivaev and others.
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
/* $Id: MapViewportLayout.java,v 1.1.4.2 2007-04-21 15:11:23 dpolivaev Exp $ */
package freemind.view.mindmapview;


import java.awt.AWTError;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Point;

import javax.swing.JScrollPane;
import javax.swing.JViewport;
import javax.swing.SwingUtilities;
import javax.swing.ViewportLayout;

import freemind.main.Tools;

public class MapViewportLayout extends ViewportLayout
{
    /**
     * Called by the AWT when the specified container needs to be laid out.
     *
     * @param parent  the container to lay out
     *
     * @exception AWTError  if the target isn't the container specified to the
     *                      <code>BoxLayout</code> constructor
     */
    public void layoutContainer(Container parent)
    {        
        JViewport vp = (JViewport)parent;
        Component view = vp.getView();
        if(! (view instanceof MapView)){
            super.layoutContainer(parent);
            return;
        }
        
        MapView mapView = (MapView) view;
        Dimension viewPrefSize = mapView.getPreferredSize();
        vp.setViewSize(viewPrefSize);
        
        Point viewPosition = vp.getViewPosition();    
        Point oldRootContentLocation = mapView.getRootContentLocation();
        final NodeView root = mapView.getRoot();
        Point rootContentLocation = root.getContent().getLocation();
        SwingUtilities.convertPointToScreen(rootContentLocation, root);
        
        final int deltaX = rootContentLocation.x - oldRootContentLocation.x ;
        final int deltaY = rootContentLocation.y - oldRootContentLocation.y;
        if(deltaX != 0 || deltaY != 0)
        {
            viewPosition.x += deltaX;
            viewPosition.y += deltaY;
            final int scrollMode = vp.getScrollMode();
            //avoid immediate scrolling here:
            vp.setScrollMode(JViewport.SIMPLE_SCROLL_MODE);
            vp.setViewPosition(viewPosition);
            vp.setScrollMode(scrollMode);
        }
        else
        {
            vp.repaint();
        }
        if(! mapView.isValid()){
            mapView.validate();
        }
        mapView.scrollSelectedNodeToVisible();
    }
}

