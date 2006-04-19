/*
 * Created on 05.05.2005
 * Copyright (C) 2005 Dimitri Polivaev
 */
package freemind.controller.filter;

import freemind.controller.Controller;
import freemind.controller.MapModuleManager.MapModuleChangeOberser;
import freemind.controller.filter.condition.Condition;
import freemind.controller.filter.condition.ConditionFactory;
import freemind.controller.filter.condition.ConditionRenderer;
import freemind.controller.filter.condition.NoFilteringCondition;
import freemind.modes.MindMap;
import freemind.modes.MindMapNode;
import freemind.modes.Mode;
import freemind.view.MapModule;

/**
 * @author dimitri
 *
 */
 public class FilterController implements MapModuleChangeOberser{
     private Controller c;
	private FilterToolbar filterToolbar;
	static private ConditionRenderer conditionRenderer = null;
	static private ConditionFactory conditionFactory;
    private MindMap map;
	
	public FilterController(Controller c){
		this.c = c;		
        c.getMapModuleManager().addListener(this);
	}

     ConditionRenderer getConditionRenderer() {
         if(conditionRenderer == null)
             conditionRenderer = new ConditionRenderer();
        return conditionRenderer;
    }
    
     /**
     * @return
     */
    public FilterToolbar getFilterToolbar() {
        if(filterToolbar == null)
            filterToolbar  = new FilterToolbar(c);
        return filterToolbar;
    }
    /**
     * @return
     */
    public void showFilterToolbar(boolean show){
        if (show == getFilterToolbar().isVisible())
            return;
        getFilterToolbar().setVisible(show);
        final Filter filter = getMap().getFilter();
        if( filter == null || filter == NoFilteringCondition.createTransparentFilter()){
            return;
        }
        if (show){
            filter.applyFilter(c);
        }
        else{
            NoFilteringCondition.createTransparentFilter().applyFilter(c);                
        }
        refreshMap();        
    }
    void refreshMap() {
        MindMapNode root = (MindMapNode)map.getRoot();
        root.getViewer().invalidateDescendantsTreeGeometries();         
        map.nodeRefresh(root);
    }
    public ConditionFactory getConditionFactory(){
        if(conditionFactory == null)
            conditionFactory = new ConditionFactory();
        return conditionFactory;
    }
    
    public void mapChanged(MindMap newMap){
        FilterComposerDialog fd = getFilterToolbar().getFilterDialog();
        if (fd != null){
            fd.mapChanged(newMap);
        }
        map = newMap;
        getFilterToolbar().mapChanged(newMap);
    }

    /**
     * @return
     */
    public MindMap getMap() {
        return map;
    }

    /**
     * @param filterToolbar The filterToolbar to set.
     */
    private void setFilterToolbar(FilterToolbar filterToolbar) {
        this.filterToolbar = filterToolbar;
    }

    public boolean isMapModuleChangeAllowed(MapModule oldMapModule, Mode oldMode, MapModule newMapModule, Mode newMode) {
        return true;
    }

    public void beforeMapModuleChange(MapModule oldMapModule, Mode oldMode, MapModule newMapModule, Mode newMode) {
    }

    public void afterMapModuleChange(MapModule oldMapModule, Mode oldMode, MapModule newMapModule, Mode newMode) {
        if(newMapModule != null)
        mapChanged(newMapModule.getModel());
    }

    public void numberOfOpenMapInformation(int number) {
    }
}
