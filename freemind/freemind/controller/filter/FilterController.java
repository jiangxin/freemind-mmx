/*FreeMind - A Program for creating and viewing Mindmaps
*Copyright (C) 2000-2006 Joerg Mueller, Daniel Polansky, Christian Foltin, Dimitri Polivaev and others.
*
*See COPYING for Details
*
*This program is free software; you can redistribute it and/or
*modify it under the terms of the GNU General Public License
*as published by the Free Software Foundation; either version 2
*of the License, or (at your option) any later version.
*
*This program is distributed in the hope that it will be useful,
*but WITHOUT ANY WARRANTY; without even the implied warranty of
*MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
*GNU General Public License for more details.
*
*You should have received a copy of the GNU General Public License
*along with this program; if not, write to the Free Software
*Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
*/
/*
 * Created on 05.05.2005
 * Copyright (C) 2005 Dimitri Polivaev
 */
package freemind.controller.filter;

import freemind.controller.Controller;
import freemind.controller.MapModuleManager.MapModuleChangeObserver;
import freemind.controller.filter.condition.Condition;
import freemind.controller.filter.condition.ConditionFactory;
import freemind.controller.filter.condition.ConditionRenderer;
import freemind.controller.filter.condition.NoFilteringCondition;
import freemind.modes.MindMap;
import freemind.modes.MindMapNode;
import freemind.modes.Mode;
import freemind.view.MapModule;
import freemind.view.mindmapview.NodeView;

/**
 * @author dimitri
 *
 */
 public class FilterController implements MapModuleChangeObserver{
     private Controller c;
	private FilterToolbar filterToolbar;
	static private ConditionRenderer conditionRenderer = null;
	static private ConditionFactory conditionFactory;
    private MindMap map;
    private static Filter inactiveFilter;
	
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
     */
    public FilterToolbar getFilterToolbar() {
        if(filterToolbar == null)
            filterToolbar  = new FilterToolbar(c);
        return filterToolbar;
    }
    /**
     */
    public void showFilterToolbar(boolean show){
        if (show == getFilterToolbar().isVisible())
            return;
        getFilterToolbar().setVisible(show);
        final Filter filter = getMap().getFilter();
        if (show){
            filter.applyFilter(c);
        }
        else{
            createTransparentFilter().applyFilter(c);                
        }
        refreshMap();        
    }
    void refreshMap() {
        c.getModeController().refreshMap();
    }
    public ConditionFactory getConditionFactory(){
        if(conditionFactory == null)
            conditionFactory = new ConditionFactory();
        return conditionFactory;
    }
    
    /**
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
            MindMap newMap = newMapModule != null ? newMapModule.getModel() : null;
            FilterComposerDialog fd = getFilterToolbar().getFilterDialog();
            if (fd != null){
                fd.mapChanged(newMap);
            }
            map = newMap;
            getFilterToolbar().mapChanged(newMap);
    }

    public void numberOfOpenMapInformation(int number) {
    }
    private static Filter createTransparentFilter(){
        if(inactiveFilter == null)
            inactiveFilter = new DefaultFilter(NoFilteringCondition.createCondition(), true, false);
        return inactiveFilter;

    }
}
