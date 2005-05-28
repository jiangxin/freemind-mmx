/** this is only a test class */
package accessories.plugins;

import java.awt.Color;

import freemind.extensions.*;
import freemind.modes.MindMap;
import freemind.modes.MindMapNode;
import freemind.modes.ModeController;

import java.util.TimerTask;
import java.util.Timer;
import java.util.Vector;

import javax.swing.SwingUtilities;

/**
 * @author christianfoltin
 *
 * @file BlinkingNodeHook.java 
 * @package freemind.modes.mindmapmode
 * */
public class BlinkingNodeHook extends PermanentNodeHookAdapter {

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
