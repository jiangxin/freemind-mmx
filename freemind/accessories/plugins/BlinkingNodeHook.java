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
/** this is only a test class */
package accessories.plugins;

import java.awt.Color;
import java.util.Timer;
import java.util.TimerTask;
import java.util.Vector;

import javax.swing.SwingUtilities;

import freemind.modes.MindMapNode;
import freemind.modes.mindmapmode.hooks.PermanentMindMapNodeHookAdapter;

/**
 * @author christianfoltin
 *
 * @file BlinkingNodeHook.java 
 * @package freemind.modes.mindmapmode
 * */
public class BlinkingNodeHook extends PermanentMindMapNodeHookAdapter {

	private Timer timer = null;

	/**
	 * @param node
	 */
	public BlinkingNodeHook() {
		super();
	}

	/* (non-Javadoc)
	 * @see freemind.modes.NodeHook#startupMapHook(java.lang.String)
	 */
	public void invoke(MindMapNode node) {
		super.invoke(node);
		if(timer == null) {
			timer = new Timer();
			timer.schedule(new TimerColorChanger(), 500, 500);
			nodeChanged(getNode());
		}
	}

//  add a new node:
//	MindMapNode newNode=((ControllerAdapter)getController()).newNode();
//	((MapAdapter) getMap()).insertNodeInto(newNode, getNode(), 0);


	static Vector colors = new Vector();
	protected class TimerColorChanger extends TimerTask {
		TimerColorChanger() {
			colors.clear();
			colors.add(Color.BLUE);
			colors.add(Color.RED);
			colors.add(Color.MAGENTA);
			colors.add(Color.CYAN);
			
		}
		/** TimerTask method to enable the selection after a given time.*/
		public void run() {
			if(getController().isBlocked()||getNode()==null /*before invocation*/)
				return;
			SwingUtilities.invokeLater(new Runnable() {
				public void run() {
					Color col = getNode().getColor();
					int index = -1;
                    if (col != null && colors.contains(col)) {
                        index = colors.indexOf(col);
                    }
					index++;
					if (index >= colors.size())
						index = 0;
					getNode().setColor((Color) colors.get(index));
					nodeChanged(getNode());
				}
			});
		}
	}
	/* (non-Javadoc)
	 * @see freemind.extensions.MindMapHook#shutdownMapHook()
	 */
	public void shutdownMapHook() {
		timer.cancel();
		timer = null;
		super.shutdownMapHook();
	}

}
