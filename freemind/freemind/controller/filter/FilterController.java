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
import freemind.controller.filter.condition.Condition;
import freemind.controller.filter.condition.ConditionRenderer;
import freemind.controller.filter.condition.IconExistCondition;
import freemind.controller.filter.condition.MindIconRenderer;
import freemind.controller.filter.util.TranslatedString;
import freemind.main.FreeMindMain;
import freemind.modes.MindIcon;

/**
 * @author dimitri
 *
 */
 public class FilterController {
	private Controller c;
	private FilterToolbar filterToolbar;
	private MindIconRenderer mindIconRenderer;
	private ConditionRenderer conditionRenderer;
	public FilterController(final Controller c){
		this.c = c;
		mindIconRenderer = new MindIconRenderer(c);
		conditionRenderer = new ConditionRenderer();
		filterToolbar = new FilterToolbar(this);
	}
	 JFrame getFrame() {
		FreeMindMain f = c.getFrame();
		if (f instanceof JFrame) return (JFrame) f;
		return null;
	}

    Controller getController() {
        return c;
    }
    
    /**
     * @param iconName
     * @return
     */
     MindIcon getIcon(String iconName) {
        return  MindIcon.factory(iconName);
    }

     public MindIconRenderer getMindIconRenderer() {
        return mindIconRenderer;
    }
     ConditionRenderer getConditionRenderer() {
        return conditionRenderer;
    }
    
     Condition createCondition(
            TranslatedString attributeType,
            String attribute, 
            TranslatedString simpleCondition,
            String conditionValue)
    {
        if (attributeType.equals("filter_icon") 
            && simpleCondition.equals("filter_exist")    )
            return new IconExistCondition(attribute, this);
        return null;
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
}
