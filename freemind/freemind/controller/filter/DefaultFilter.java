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
            boolean areEclipsedShown, 
            boolean areDescendersShown) {
        super();
        this.condition = isActive ? condition : null;
        this.options = FILTER_INITIAL_VALUE | FILTER_SHOW_MATCHED;
        if (areAnchestorsShown) options += FILTER_SHOW_ANCESTOR;
        if (areEclipsedShown) options += FILTER_SHOW_ECLIPSED;
        if (areDescendersShown) options += FILTER_SHOW_DESCENDER;
    }
    /* (non-Javadoc)
     * @see freemind.controller.filter.Filter#applyFilter(freemind.modes.MindMap)
     */
    public void applyFilter(MindMap map) {
        MindMapNode root = (MindMapNode)map.getRoot();
        if(condition != null){
            resetFilter(root);
            if (applyFilter(root.childrenUnfolded(), false, false))
                addFilterResult(root, FILTER_SHOW_ANCESTOR);
        }
        map.nodeChanged(root);
    }

    /**
     * @param iterator
     * @return
     */
    private boolean applyFilter(ListIterator iterator, boolean isAncestorSelected, boolean isAncestorEclipsed) {
        boolean isDescenderSelected = false;
        while(iterator.hasNext()){
            MindMapNode node = (MindMapNode)iterator.next();
            resetFilter(node);
            if (isAncestorSelected) addFilterResult(node, FILTER_SHOW_DESCENDER);
            boolean lastCheck = condition.checkNode(node);
            if (lastCheck){
                isDescenderSelected = true;
                addFilterResult(node, FILTER_SHOW_MATCHED);
            }
            else
            {
                addFilterResult(node, FILTER_SHOW_HIDDEN);
            }
            if (isAncestorEclipsed){
                addFilterResult(node, FILTER_SHOW_ECLIPSED);               
            }
            ListIterator children = node.childrenUnfolded();
            if(applyFilter(children, lastCheck || isAncestorSelected, !lastCheck || isAncestorEclipsed)){
                addFilterResult(node, FILTER_SHOW_ANCESTOR);
                isDescenderSelected = true;
            }            
        }
        return isDescenderSelected;
    }
    /* (non-Javadoc)
     * @see freemind.controller.filter.Filter#isVisible(freemind.modes.MindMapNode)
     */
    public boolean isVisible(MindMapNode node) {
        if (condition == null) return true;
        int filterResult = node.getFilterInfo().get();
        return ((options & FILTER_SHOW_ANCESTOR) != 0 
             || (options & FILTER_SHOW_ECLIPSED) >= (filterResult & FILTER_SHOW_ECLIPSED)
             ) &&  ((options & filterResult & ~FILTER_SHOW_ECLIPSED) != 0);
       
    }
    
    static public void resetFilter(MindMapNode node){
        node.getFilterInfo().reset();
    }
    
    static void addFilterResult(MindMapNode node, int flag){
		node.getFilterInfo().add(flag);
    }
}
