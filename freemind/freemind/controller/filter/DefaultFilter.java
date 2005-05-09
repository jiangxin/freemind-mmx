/*
 * Created on 07.05.2005
 *
 */
package freemind.controller.filter;

import java.util.ListIterator;

import freemind.controller.filter.condition.Condition;
import freemind.modes.MindMap;
import freemind.modes.MindMapNode;

/**
 * @author dimitri
 * 07.05.2005
 */
class DefaultFilter implements Filter{
	
	static final int FILTER_MATCHED = 2;
	static final int FILTER_ANCESTOR = 4;
	static final int FILTER_DESCENDER = 8;
	static final int FILTER_HIDDEN = 16;

	private Condition condition = null;
    private int options = 0;
    
    /**
     * @param condition
     * @param options
     */
    DefaultFilter(
            Condition condition, 
            boolean isActive, 
            boolean areAnchestorsShown, 
            boolean areDescendersShown) {
        super();
        this.condition = isActive ? condition : null;
        this.options = FILTER_INITIAL_VALUE | FILTER_MATCHED;
        if (areAnchestorsShown) options += FILTER_ANCESTOR;
        if (areDescendersShown) options += FILTER_DESCENDER;
    }
    /* (non-Javadoc)
     * @see freemind.controller.filter.Filter#applyFilter(freemind.modes.MindMap)
     */
    public void applyFilter(MindMap map) {
        MindMapNode root = (MindMapNode)map.getRoot();
        if(condition != null){
            resetFilter(root);
            if (applyFilter(root.childrenUnfolded(), false))
                addFilterResult(root, FILTER_ANCESTOR);
        }
        map.nodeChanged(root);
    }

    /**
     * @param iterator
     * @return
     */
    private boolean applyFilter(ListIterator iterator, boolean isAncestorSelected) {
        boolean result = false;
        while(iterator.hasNext()){
            MindMapNode node = (MindMapNode)iterator.next();
            resetFilter(node);
            if (isAncestorSelected) addFilterResult(node, FILTER_DESCENDER);
            boolean lastCheck = condition.checkNode(node);
            if (lastCheck){
                result = true;
                addFilterResult(node, FILTER_MATCHED);
            }
            else
            {
                addFilterResult(node, FILTER_HIDDEN);
            }
            ListIterator children = node.childrenUnfolded();
            if(applyFilter(children, lastCheck || isAncestorSelected)){
                addFilterResult(node, FILTER_ANCESTOR);
            }            
        }
        return result;
    }
    /* (non-Javadoc)
     * @see freemind.controller.filter.Filter#isVisible(freemind.modes.MindMapNode)
     */
    public boolean isVisible(MindMapNode node) {
        return condition == null  || (node.getFilterResult() & options) != 0;
    }
    
    static public void resetFilter(MindMapNode node){
        node.setFilterResult(FILTER_INITIAL_VALUE);
    }
    
    void addFilterResult(MindMapNode node, int flag){
        int filterResult = node.getFilterResult();
	    filterResult &= ~FILTER_INITIAL_VALUE;
		filterResult |= flag;        
		node.setFilterResult(filterResult);
    }
}
