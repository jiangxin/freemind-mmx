/*
 * Created on 05.05.2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package freemind.controller.filter;

import javax.swing.AbstractButton;
import javax.swing.DefaultComboBoxModel;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JToolBar;

import freemind.controller.Controller;
import freemind.controller.filter.condition.ConditionFactory;
import freemind.controller.filter.condition.ConditionRenderer;
import freemind.controller.filter.condition.MindIconRenderer;
import freemind.main.FreeMindMain;
import freemind.modes.MindIcon;
import freemind.modes.MindMap;

/**
 * @author dimitri
 *
 */
 public class FilterController {
	private FilterToolbar filterToolbar;
	private MindIconRenderer mindIconRenderer;
	private ConditionRenderer conditionRenderer;
	private ConditionFactory conditionFactory;
	
	public FilterController(){
		mindIconRenderer = new MindIconRenderer();
		conditionRenderer = new ConditionRenderer();
		filterToolbar = new FilterToolbar(this);
	}
	 JFrame getFrame() {
		FreeMindMain f = Controller.getInstance().getFrame();
		if (f instanceof JFrame) return (JFrame) f;
		return null;
	}

    public MindIconRenderer getMindIconRenderer() {
        return mindIconRenderer;
    }
     ConditionRenderer getConditionRenderer() {
        return conditionRenderer;
    }
    
     /**
     * @return
     */
    public JToolBar getFilterToolbar() {
        return filterToolbar;
    }
    /**
     * @return
     */
    public Filter getFilter() {
        // TODO Auto-generated method stub
        return filterToolbar.getFilter();
    }
    
    public void showFilterToolbar(boolean show){
        AbstractButton btnFilterActive = filterToolbar.getBtnApply();
        if (show){
            if (! filterToolbar.isVisible()){ 
                filterToolbar.setVisible(true);
            }
        }
        else{
            if (filterToolbar.isVisible() && btnFilterActive.getModel().isSelected()) 
                btnFilterActive.doClick();
            filterToolbar.setVisible(false);
        }
        
    }
    public ConditionFactory getConditionFactory(){
        if(conditionFactory == null)
            conditionFactory = new ConditionFactory();
        return conditionFactory;
    }
    
    public void mapChanged(MindMap newMap){
        FilterDialog fd = filterToolbar.getFilterDialog();
        if (fd != null){
            fd.mapChanged(newMap);
        }
    }
}
