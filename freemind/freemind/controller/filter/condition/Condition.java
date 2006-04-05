/*
 * Created on 05.05.2005
 * Copyright (C) 2005 Dimitri Polivaev
 */
package freemind.controller.filter.condition;

import javax.swing.JComponent;

import freemind.modes.MindMapNode;

public interface Condition{
	boolean checkNode(MindMapNode node);	
    public JComponent getListCellRendererComponent();
}
