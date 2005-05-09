/*
 * Created on 07.05.2005
 *
 */
package freemind.controller.filter;

import freemind.modes.MindMap;
import freemind.modes.MindMapNode;

/**
 * @author dimitri
 * 07.05.2005
 */
public interface Filter {
	public static final int FILTER_INITIAL_VALUE = 1;
	
	void applyFilter(MindMap map); 
	boolean isVisible(MindMapNode node); 
}
