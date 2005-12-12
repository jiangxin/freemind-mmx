/*FreeMind - A Program for creating and viewing Mindmaps
 *Copyright (C) 2000-2004  Joerg Mueller, Daniel Polansky, Christian Foltin and others.
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
 *
 * Created on 08.08.2004
 */
/*$Id: MapModuleManager.java,v 1.1.4.3.2.1.2.2 2005-12-12 21:14:50 dpolivaev Exp $*/

package freemind.controller;

import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;

import freemind.controller.Controller.HistoryManager;
import freemind.modes.MindMap;
import freemind.modes.Mode;
import freemind.view.MapModule;
import freemind.view.mindmapview.MapView;


/**
     * Manages the list of MapModules.
     * As this task is very complex, I exported it
     * from Controller to this class to keep Controller
     * simple.
     */
    public class MapModuleManager {
        // Variable below: The instances of mode, ie. the Model/View pairs. Normally, the
        // order should be the order of insertion, but such a Map is not
        // available.
        private Map mapModules = new HashMap();

        private MapModule mapModule; //reference to the current mapmodule, could be done
                                     //with an index to mapModules, too.
        // private String current;
        
        private Controller c;

        private final LastOpenedList lastOpened;

        private final HistoryManager history;

        MapModuleManager(Controller c, HistoryManager history, LastOpenedList lastOpened) {
           this.c=c;
        this.history = history;
        this.lastOpened = lastOpened; }

        public Map getMapModules() {
           return mapModules; }
        
        public MapModule getMapModule() {
           return mapModule; }

        public void newMapModule(MindMap map) {
            MapModule mapModule = new MapModule(map, new MapView(map, c), c.getMode());
            addToMapModules(mapModule.toString(), mapModule);
            setMapModule(mapModule);
            history.mapChanged(mapModule);
        }

        public void updateMapModuleName() {
            getMapModules().remove(getMapModule().toString());
            //removeFromViews() doesn't work because MapModuleChanged()
            //must not be called at this state
            getMapModule().rename();
            addToMapModules(getMapModule().toString(),getMapModule());
            setMapModule(getMapModule());
        }

        void nextMapModule() {
            List keys = new LinkedList(getMapModules().keySet());
            int index = keys.indexOf(getMapModule().toString());
            ListIterator i = keys.listIterator(index+1);
            if (i.hasNext()) {
               changeToMapModule((String)i.next()); }
            else if (keys.iterator().hasNext()) {
               // Change to the first in the list
               changeToMapModule((String)keys.iterator().next()); }}

        void previousMapModule() {
            List keys = new LinkedList(getMapModules().keySet());
            int index = keys.indexOf(getMapModule().toString());
            ListIterator i = keys.listIterator(index);
            if (i.hasPrevious()) {
               changeToMapModule((String)i.previous()); }
            else {
               Iterator last = keys.listIterator(keys.size()-1);
               if (last.hasNext()) {
                  changeToMapModule((String)last.next()); }}}

        //Change MapModules
		/** This is the question whether the map is already opened. If this is the case,
		 * the map is automatically opened + returns true. Otherwise does nothing + returns false.*/
        public boolean tryToChangeToMapModule(String mapModule) {
            if (mapModule != null && getMapModules().containsKey(mapModule)) {
                changeToMapModule(mapModule);
                return true; }
            else {
               return false; }}

    	/** adds the mapModule to the history and calls changeToMapModuleWithoutHistory. */
        void changeToMapModule(String mapModule) {
            MapModule map = (MapModule)(getMapModules().get(mapModule));
            history.mapChanged(map);
            setMapModule(map); 
        }


        public void changeToMapOfMode(Mode mode) {
            for (Iterator i = getMapModules().keySet().iterator(); i.hasNext(); ) {
                String next = (String)i.next();
                if ( ((MapModule)getMapModules().get(next)).getMode() == mode ) {
                    changeToMapModule(next);
                    return; }}}

    /**
	 * @param mapModule
	 *            is null if the old mode should be closed.
	 */
	void setMapModule(MapModule mapModule) {
		MapModule oldMapModule = this.mapModule;
		if (oldMapModule != null) {
			// shut down screens of old view + frame
			c.getModeController().setVisible(false);
			c.getModeController().shutdownController();
		}

		if (mapModule != null) {
			// change mode ?
			if (mapModule.getMode() != c.getMode()) {
				// create mode
				c.changeToMode(mapModule.getMode().toString());
			}
		}
		this.mapModule = mapModule;
		List keys = new LinkedList(getMapModules().keySet());
		c.navigationPreviousMap.setEnabled(keys.size() > 1);
		c.navigationNextMap.setEnabled(keys.size() > 1);
		// FIXME: This is done twice (the first time was in changeToMode above),
		// but we need it to update the maps menus
		c.getFrame().getFreeMindMenuBar().updateMenus();

		if (mapModule != null) {
			//FIXME: This is controller code and has to be moved.
			c.getFrame().setView(mapModule.getView());
			c.setAllActions(true);
			if (c.getView().getSelected() == null) {
				// Only for the new modules move to root
				c.moveToRoot();
			}
			lastOpened.mapOpened(getMapModule());
			c.setTitle();
			((MainToolBar) c.getToolbar()).setZoomComboBox(getMapModule()
					.getView().getZoom());
			c.obtainFocusForSelected();
			c.getModeController().startupController();
			c.getModeController().setVisible(true);
			fireMapChanged(mapModule);
		} else {
			c.getFrame().setView(null);

		}
	}


        //private

        private void fireMapChanged(MapModule mapModule) {
        c.mapChanged(mapModule.getModel());
    }

        private void addToMapModules(String key, MapModule newMapModule) {
            // begin bug fix, 20.12.2003, fc.
            // check, if already present:
            String extension = "";
            int count = 1;
            while (mapModules.containsKey(key+extension)) {
                extension = "<"+(++count)+">";
            }
            // rename map:
            newMapModule.setName(key+extension);
            mapModules.put(key+extension,newMapModule);
            // end bug fix, 20.12.2003, fc.
       }

       private void changeToAnotherMap(String toBeClosed) {
          List keys = new LinkedList(getMapModules().keySet());
          for (ListIterator i = keys.listIterator(); i.hasNext();) {
             String key = (String)i.next();
             if (!key.equals(toBeClosed)) {
                changeToMapModule(key);
                return; }}}

        /**
        *  Close the currently active map, return false if closing cancelled.
         * @param force forces the closing without any save actions.
        */
       public boolean close(boolean force) {
       	    // (DP) The mode controller does not close the map
            boolean closingNotCancelled = c.getMode().getModeController().close(force);
            if (!closingNotCancelled) {
               return false; }	
            
            String toBeClosed = getMapModule().toString();
            mapModules.remove(toBeClosed);
            if (mapModules.isEmpty()) {
               c.setAllActions(false);
               setMapModule(null);
               c.getFrame().setView(null); }
            else {
               changeToMapModule((String)mapModules.keySet().iterator().next());
            }
            return true; }

       // }}

    }