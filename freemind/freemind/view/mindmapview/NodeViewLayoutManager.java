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

import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.LayoutManager;

/**
 * @author dimitri
 * 05.06.2005
 */
public class NodeViewLayoutManager implements LayoutManager {
    protected NodeViewLayoutManager(){
        
    }
    
    
    static private NodeViewLayoutManager instance = null;
    private Dimension minDimension ;
    
    public void addLayoutComponent(String arg0, Component arg1) {
    }

    /* (non-Javadoc)
     * @see java.awt.LayoutManager#removeLayoutComponent(java.awt.Component)
     */
    public void removeLayoutComponent(Component arg0) {
    }

    /* (non-Javadoc)
     * @see java.awt.LayoutManager#minimumLayoutSize(java.awt.Container)
     */
    public Dimension minimumLayoutSize(Container arg0) {
        if (minDimension == null) 
            minDimension= new Dimension(0,0);
        return minDimension;
    }

    /* (non-Javadoc)
     * @see java.awt.LayoutManager#layoutContainer(java.awt.Container)
     */
    /* (non-Javadoc)
     * @see java.awt.LayoutManager#preferredLayoutSize(java.awt.Container)
     */
    public Dimension preferredLayoutSize(Container c) {
        Dimension prefSize = new Dimension(0, 0);
        for(int i = 0; i < c.getComponentCount(); i++){
            final Component component = c.getComponent(i);
            if(!component.isVisible())
                continue;
            Dimension componentPrefSize = component.getPreferredSize();
            prefSize.width = Math.max(componentPrefSize.width, prefSize.width);
            prefSize.height += componentPrefSize.height;
        }
        return prefSize;
    }

      public void layoutContainer(Container c) {        
        Dimension totalSize = preferredLayoutSize(c);
        int y = 0;
        for(int i = 0; i < c.getComponentCount(); i++){
            final Component component = c.getComponent(i);
            if(!component.isVisible())
                continue;
            Dimension componentPreferredSize = component.getPreferredSize();
            Dimension componentMaximumSize = component.getMaximumSize();
            int spaceX = totalSize.width - componentMaximumSize.width;
            if(spaceX > 0){
                component.setBounds(spaceX / 2, y, componentMaximumSize.width, componentPreferredSize.height);
            }
            else{
                component.setBounds(0, y, totalSize.width, componentPreferredSize.height);                
            }
            y += componentPreferredSize.height;
        }
    }

    static NodeViewLayoutManager getInstance() {
        if (instance == null) instance = new NodeViewLayoutManager();
        return instance;
    }
}
