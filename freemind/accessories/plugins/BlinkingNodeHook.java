/** this is only a test class */
package accessories.plugins;

import java.awt.Color;

import freemind.extensions.*;
import freemind.modes.ControllerAdapter;
import freemind.modes.MapAdapter;
import freemind.modes.MindMap;
import freemind.modes.MindMapNode;
import freemind.modes.ModeController;

import java.util.TimerTask;
import java.util.Timer;
import java.util.Vector;

/**
 * @author christianfoltin
 *
 * @file BlinkingNodeHook.java 
 * @package freemind.modes.mindmapmode
 * */
public class BlinkingNodeHook extends NodeHookAdapter {

	private Timer timer;

	/**
	 * @param node
	 */
	public BlinkingNodeHook(MindMapNode node, MindMap map, ModeController controller) {
		super(node, map, controller);
		timer = new Timer();
		timer.schedule(new timerColorChanger(), 500, 500);
	}

	/* (non-Javadoc)
	 * @see freemind.modes.NodeHook#getName()
	 */
	public String getName() {
		return "blue";
	}
	/* (non-Javadoc)
	 * @see freemind.modes.NodeHook#startupMapHook(java.lang.String)
	 */
	public void invoke() {
		getNode().setColor(Color.BLUE);
		setToolTip("<html><a href='web'>BLUE IS GEIL!</a></html>");
		nodeChanged(getNode());
	}

//  add a new node:
//	MindMapNode newNode=((ControllerAdapter)getController()).newNode();
//	((MapAdapter) getMap()).insertNodeInto(newNode, getNode(), 0);

	/* (non-Javadoc)
	 * @see freemind.modes.NodeHook#onUpdateNodeHook()
	 */
	public void onUpdateNodeHook() {
// 		getNode().setColor(Color.BLUE);
// 		nodeChanged(getNode());
// 		System.out.println("onUpdateNodeHook"+this);
	}

	/* (non-Javadoc)
	 * @see freemind.modes.NodeHook#onUpdateChildrenHook(freemind.modes.MindMapNode)
	 */
	public void onUpdateChildrenHook(MindMapNode updatedNode) {
		// updatedNode.setColor(Color.BLUE);
		//don't do this: nodeChanged(updatedNode);
	}

	/* (non-Javadoc)
	 * @see freemind.modes.NodeHook#onReceiveFocusHook()
	 */
	public void onReceiveFocusHook() {
// 		getNode().setColor(Color.RED);
// 		nodeChanged(getNode());
	}
	static Vector colors = new Vector();
	protected class timerColorChanger extends TimerTask {
		timerColorChanger() {
			colors.clear();
			colors.add(Color.BLUE);
			colors.add(Color.RED);
			colors.add(Color.MAGENTA);
			colors.add(Color.CYAN);
			
		}
		/** TimerTask method to enable the selection after a given time.*/
		public void run() {
			if(getController().isBlocked())
				return;
			Color col = getNode().getColor();
			int index = colors.indexOf(col);
			index++;
			if(index >= colors.size())
				index = 0;
			getNode().setColor((Color) colors.get(index));
			nodeChanged(getNode());
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
