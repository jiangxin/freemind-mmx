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
 * Created on 12.03.2004
 *
 */
package accessories.plugins;

import java.awt.Container;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.Rectangle;

import javax.swing.JScrollPane;
import javax.swing.JViewport;

import freemind.extensions.ModeControllerHookAdapter;
import freemind.view.mindmapview.MapView;

/**
 * @author foltin
 * @author dimitri: Bug fixes.
 *
 */
public class FitToPage extends ModeControllerHookAdapter {

	private MapView view;

	/**
	 * 
	 */
	public FitToPage() {
		super();
	}

    public void startupMapHook() {
        super.startupMapHook();
        view = getController().getView();
        if (view == null)
            return;
        zoom();
        EventQueue.invokeLater(new Runnable() {
            public void run() {
               scroll();                
            }
            
        });
    }
    
    private int shift(int coord1, int size1, int coord2, int size2)
    {
        return coord1 - coord2 + (size1 - size2)/ 2;
    }

    private void scroll() {
        Rectangle rect = view.getInnerBounds();
        Rectangle viewer = view.getVisibleRect();
        view.scrollBy(
                shift(rect.x, rect.width, viewer.x, viewer.width), 
                shift(rect.y, rect.height, viewer.y, viewer.height));
    }
    
	private void zoom() {        
		Rectangle rect = view.getInnerBounds();
		// calculate the zoom:
		double oldZoom = getController().getView().getZoom();
        JViewport viewPort = (JViewport)view.getParent();
        JScrollPane pane = (JScrollPane)viewPort.getParent();
        Dimension viewer = viewPort.getExtentSize();
		logger.info(
			"Found viewer rect="
				+ viewer.height
				+ "/"
				+ rect.height
				+ ", "
				+ viewer.width
				+ "/"
				+ rect.width);
		double newZoom = viewer.width * oldZoom / (rect.width + 0.0);
		double heightZoom = viewer.height * oldZoom / (rect.height + 0.0);
		if (heightZoom < newZoom) {
			newZoom = heightZoom;
		}
		logger.info("Calculated new zoom " + (newZoom));
		getController().getController().setZoom((float) (newZoom));
	}

}
