/*
 * Created on 07.05.2005
 *
 */
package freemind.controller.filter;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.ListIterator;

import freemind.controller.Controller;
import freemind.controller.filter.condition.Condition;
import freemind.modes.MindMap;
import freemind.modes.MindMapNode;
import freemind.view.mindmapview.MapView;
import freemind.view.mindmapview.NodeView;

/**
 * @author dimitri
 * 07.05.2005
 */
public class DefaultFilter implements Filter{
	
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
            boolean areDescendantsShown) {
        super();
        this.condition = isActive ? condition : null;
        this.options = FILTER_INITIAL_VALUE | FILTER_SHOW_MATCHED;
        if (areAnchestorsShown) options += FILTER_SHOW_ANCESTOR;
        options += FILTER_SHOW_ECLIPSED;
        if (areDescendantsShown) options += FILTER_SHOW_DESCENDANT;
    }
    /* (non-Javadoc)
     * @see freemind.controller.filter.Filter#applyFilter(freemind.modes.MindMap)
     */
    public void applyFilter(Controller c) {
        if(condition != null){
            MindMap map = c.getModel();
            MapView mapView = c.getView();
            MindMapNode root = (MindMapNode)map.getRoot();
            resetFilter(root);
            if (filterChildren(root, false, false)){
                addFilterResult(root, FILTER_SHOW_ANCESTOR);
            }
            selectVisibleNode(mapView);
        }
    }

    static public void selectVisibleNode(MapView mapView) {
        LinkedList selectedNodes = mapView.getSelecteds();
        ListIterator iterator = selectedNodes.listIterator(selectedNodes.size()-1);
        while(iterator.hasPrevious()){
            NodeView previous = (NodeView) iterator.previous();
            if ( ! previous.getModel().isVisible()){
                mapView.toggleSelected(previous); 
            }
        }
        NodeView selected = mapView.getSelected();
        if(! selected.getModel().isVisible()){
            mapView.selectAsTheOnlyOneSelected(getNearestVisibleParent(selected));
        }
    }
    
    static private NodeView getNearestVisibleParent(NodeView selectedNode) {
        if(selectedNode.getModel().isVisible())
            return selectedNode;
        return getNearestVisibleParent(selectedNode.getParentView());
    }
    /**
     * @param iterator
     * @return
     */
    private boolean filterChildren(MindMapNode parent, boolean isAncestorSelected, boolean isAncestorEclipsed) {
        ListIterator iterator = parent.childrenUnfolded();
        boolean isDescendantSelected = false;
        while(iterator.hasNext()){
            MindMapNode node = (MindMapNode)iterator.next();
            isDescendantSelected = applyFilter(node, isAncestorSelected, isAncestorEclipsed, isDescendantSelected);            
        }
        return isDescendantSelected;
    }
    
    private boolean applyFilter(MindMapNode node, boolean isAncestorSelected, boolean isAncestorEclipsed, boolean isDescendantSelected) {
        resetFilter(node);
        if (isAncestorSelected) addFilterResult(node, FILTER_SHOW_DESCENDANT);
        boolean conditionSatisfied = condition.checkNode(node);
        if (conditionSatisfied){
            isDescendantSelected = true;
            addFilterResult(node, FILTER_SHOW_MATCHED);
        }
        else
        {
            addFilterResult(node, FILTER_SHOW_HIDDEN);
        }
        if (isAncestorEclipsed){
            addFilterResult(node, FILTER_SHOW_ECLIPSED);               
        }
        if(filterChildren(node, conditionSatisfied || isAncestorSelected, !conditionSatisfied || isAncestorEclipsed)){
            addFilterResult(node, FILTER_SHOW_ANCESTOR);
            isDescendantSelected = true;
        }
        return isDescendantSelected;
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
    /* (non-Javadoc)
     * @see freemind.controller.filter.Filter#areMatchedShown()
     */
    public boolean areMatchedShown() {
        return true;
    }
    /* (non-Javadoc)
     * @see freemind.controller.filter.Filter#areHiddenShown()
     */
    public boolean areHiddenShown() {
        return false;
    }
    /* (non-Javadoc)
     * @see freemind.controller.filter.Filter#areAncestorsShown()
     */
    public boolean areAncestorsShown() {
        return 0 != (options & FILTER_SHOW_ANCESTOR) ;
    }
    /* (non-Javadoc)
     * @see freemind.controller.filter.Filter#areDescendantsShown()
     */
    public boolean areDescendantsShown() {
        return 0 != (options & FILTER_SHOW_DESCENDANT) ;
    }
    /* (non-Javadoc)
     * @see freemind.controller.filter.Filter#areEclipsedShown()
     */
    public boolean areEclipsedShown() {
        return true;
    }
}
