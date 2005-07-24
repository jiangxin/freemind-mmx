/*
 * Created on 05.05.2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package freemind.controller.filter.condition;

import javax.swing.JComponent;

import freemind.modes.MindMapNode;

/**
 * @author d
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public interface Condition{
	boolean checkNode(MindMapNode node);	
    public JComponent getListCellRendererComponent();
}
