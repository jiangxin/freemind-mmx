/*
 * Created on 07.05.2005
 *
 */
package freemind.controller.filter;

import freemind.controller.Controller;
import freemind.modes.MindMap;
import freemind.modes.MindMapNode;

/**
 * @author dimitri
 * 07.05.2005
 */
public interface Filter {
	public static final int FILTER_INITIAL_VALUE = 1;
	static final int FILTER_SHOW_MATCHED = 2;
	static final int FILTER_SHOW_ANCESTOR = 4;
	static final int FILTER_SHOW_DESCENDANT = 8;
	static final int FILTER_SHOW_ECLIPSED = 16;
	static final int FILTER_SHOW_HIDDEN = 32;

	
	void applyFilter(Controller c); 
	boolean isVisible(MindMapNode node);
	boolean areMatchedShown();
	boolean areHiddenShown();
	boolean areAncestorsShown();
	boolean areDescendantsShown();
	boolean areEclipsedShown();
}
