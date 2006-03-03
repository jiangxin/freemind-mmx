/*
 * Created on 05.05.2005
 * Copyright (C) 2005 Dimitri Polivaev
 */
package freemind.controller.filter;

import javax.swing.AbstractButton;

import freemind.controller.Controller;
import freemind.controller.filter.condition.ConditionFactory;
import freemind.controller.filter.condition.ConditionRenderer;
import freemind.modes.MindMap;

/**
 * @author dimitri
 *
 */
 public class FilterController {
     private Controller c;
	private FilterToolbar filterToolbar;
	static private ConditionRenderer conditionRenderer = null;
	static private ConditionFactory conditionFactory;
    private MindMap map;
	
	public FilterController(Controller c){
		this.c = c;		
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
        AbstractButton btnFilterActive = getFilterToolbar().getBtnApply();
        if (show){
            if (! getFilterToolbar().isVisible()){ 
                getFilterToolbar().setVisible(true);
            }
        }
        else{
            if (getFilterToolbar().isVisible() && btnFilterActive.getModel().isSelected()) 
                btnFilterActive.doClick();
            getFilterToolbar().setVisible(false);
        }
        
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
}
