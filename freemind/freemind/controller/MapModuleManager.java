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
/*$Id: MapModuleManager.java,v 1.1.4.4.2.14 2008/05/31 10:55:04 dpolivaev Exp $*/

package freemind.controller;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import freemind.main.Tools;
import freemind.modes.MindMap;
import freemind.modes.Mode;
import freemind.modes.ModeController;
import freemind.view.MapModule;
import freemind.view.mindmapview.MapView;

/**
 * Manages the list of MapModules. As this task is very complex, I exported it
 * from Controller to this class to keep Controller simple.
 * 
 * The information exchange between controller and this class is managed by
 * oberser pattern (the controller observes changes to the map modules here).
 * 
 * TODO: Use an vector with the map modules ordered by the screen order.
 */
public class MapModuleManager {

	public static interface MapModuleChangeObserver {
		/**
		 * The params may be null to indicate the there was no previous map, or
		 * that the last map is closed now.
		 */
		boolean isMapModuleChangeAllowed(MapModule oldMapModule, Mode oldMode,
				MapModule newMapModule, Mode newMode);

		void beforeMapModuleChange(MapModule oldMapModule, Mode oldMode,
				MapModule newMapModule, Mode newMode);

		void afterMapClose(MapModule oldMapModule, Mode oldMode);

		void afterMapModuleChange(MapModule oldMapModule, Mode oldMode,
				MapModule newMapModule, Mode newMode);

		/**
		 * To enable/disable the previous/next map actions.
		 * 
		 * @param pIndex
		 *            TODO
		 */
		void numberOfOpenMapInformation(int number, int pIndex);
	}

	public static class MapModuleChangeObserverCompound implements
			MapModuleChangeObserver {
		private HashSet listeners = new HashSet();

		public void addListener(MapModuleChangeObserver listener) {
			listeners.add(listener);
		}

		public void removeListener(MapModuleChangeObserver listener) {
			listeners.remove(listener);
		}

		public boolean isMapModuleChangeAllowed(MapModule oldMapModule,
				Mode oldMode, MapModule newMapModule, Mode newMode) {
			boolean returnValue = true;
			for (Iterator iter = new Vector(listeners).iterator(); iter
					.hasNext();) {
				MapModuleChangeObserver observer = (MapModuleChangeObserver) iter
						.next();
				returnValue = observer.isMapModuleChangeAllowed(oldMapModule,
						oldMode, newMapModule, newMode);
				if (!returnValue) {
					break;
				}
			}
			return returnValue;
		}

		public void beforeMapModuleChange(MapModule oldMapModule, Mode oldMode,
				MapModule newMapModule, Mode newMode) {
			for (Iterator iter = new Vector(listeners).iterator(); iter
					.hasNext();) {
				MapModuleChangeObserver observer = (MapModuleChangeObserver) iter
						.next();
				observer.beforeMapModuleChange(oldMapModule, oldMode,
						newMapModule, newMode);
			}
		}

		public void afterMapModuleChange(MapModule oldMapModule, Mode oldMode,
				MapModule newMapModule, Mode newMode) {
			for (Iterator iter = new Vector(listeners).iterator(); iter
					.hasNext();) {
				MapModuleChangeObserver observer = (MapModuleChangeObserver) iter
						.next();
				observer.afterMapModuleChange(oldMapModule, oldMode,
						newMapModule, newMode);
			}
		}

		public void numberOfOpenMapInformation(int number, int pIndex) {
			for (Iterator iter = new Vector(listeners).iterator(); iter
					.hasNext();) {
				MapModuleChangeObserver observer = (MapModuleChangeObserver) iter
						.next();
				observer.numberOfOpenMapInformation(number, pIndex);
			}
		}

		public void afterMapClose(MapModule pOldMapModule, Mode pOldMode) {
			for (Iterator iter = new Vector(listeners).iterator(); iter
					.hasNext();) {
				MapModuleChangeObserver observer = (MapModuleChangeObserver) iter
						.next();
				observer.afterMapClose(pOldMapModule, pOldMode);
			}
		}
	}

	/**
	 * You can register yourself to this listener at the main controller.
	 */
	public static interface MapTitleChangeListener {
		void setMapTitle(String pNewMapTitle, MapModule pMapModule,
				MindMap pModel);
	}

	MapModuleChangeObserverCompound listener = new MapModuleChangeObserverCompound();

	public void addListener(MapModuleChangeObserver pListener) {
		listener.addListener(pListener);
	}

	public void removeListener(MapModuleChangeObserver pListener) {
		listener.removeListener(pListener);
	}

	/**
	 * You can register yourself as a contributor to the title at the main
	 * controller.
	 */
	public static interface MapTitleContributor {
		/**
		 * @param pOldTitle
		 *            The current title
		 * @param pMapModule
		 * @param pModel
		 * @return The current title can be changed or something can be added,
		 *         but it must be returned as a whole.
		 */
		String getMapTitle(String pOldTitle, MapModule pMapModule,
				MindMap pModel);
	}

	// /** Contains pairs String (key+extension) => MapModule instances.
	// * The instances of mode, ie. the Model/View pairs. Normally, the
	// * order should be the order of insertion, but such a Map is not
	// * available. */
	// private Map mapModules = new HashMap();

	/**
	 * A vector of MapModule instances. They are ordered according to their
	 * screen order.
	 */
	private Vector mapModuleVector = new Vector();

	/** reference to the current mapmodule; null is allowed, too. */
	private MapModule mapModule;
	/**
	 * Reference to the current mode as the mapModule may be null.
	 */
	private Mode mCurrentMode = null;

	private Controller mController;

	MapModuleManager(Controller c) {
		this.mController = c;
	}

	/**
	 * @return a map of String to MapModule elements.
	 * @deprecated use getMapModuleVector instead (and get the displayname as
	 *             MapModule.getDisplayName().
	 */
	public Map getMapModules() {
		HashMap returnValue = new HashMap();
		for (Iterator iterator = mapModuleVector.iterator(); iterator.hasNext();) {
			MapModule module = (MapModule) iterator.next();
			returnValue.put(module.getDisplayName(), module);
		}
		return Collections.unmodifiableMap(returnValue);
	}

	public List getMapModuleVector() {
		return Collections.unmodifiableList(mapModuleVector);
	}

	/** @return an unmodifiable set of all display names of current opened maps. */
	public List getMapKeys() {
		LinkedList returnValue = new LinkedList();
		for (Iterator iterator = mapModuleVector.iterator(); iterator.hasNext();) {
			MapModule module = (MapModule) iterator.next();
			returnValue.add(module.getDisplayName());
		}
		return Collections.unmodifiableList(returnValue);
	}

	public MapModule getMapModule() {
		return mapModule;
	}

	public void newMapModule(MindMap map, ModeController modeController) {
		MapModule mapModule = new MapModule(map, new MapView(map, mController),
				modeController.getMode(), modeController);
		addToOrChangeInMapModules(mapModule.toString(), mapModule);
		setMapModule(mapModule, modeController.getMode());
	}

	public MapModule getModuleGivenModeController(ModeController pModeController) {
		MapModule mapModule = null;
		for (Iterator iter = getMapModules().entrySet().iterator(); iter
				.hasNext();) {
			Map.Entry mapEntry = (Map.Entry) iter.next();
			mapModule = (MapModule) mapEntry.getValue();
			if (pModeController.equals(mapModule.getModeController())) {
				break;
			}
			mapModule = null;
		}
		return mapModule;
	}

	public void updateMapModuleName() {
		// removeFromViews() doesn't work because MapModuleChanged()
		// must not be called at this state
		getMapModule().rename();
		addToOrChangeInMapModules(getMapModule().toString(), getMapModule());
		setMapModule(getMapModule(), getMapModule().getMode());
	}

	void nextMapModule() {
		int index;
		int size = mapModuleVector.size();
		if (getMapModule() != null)
			index = mapModuleVector.indexOf(getMapModule());
		else
			index = size - 1;

		if (index + 1 < size && index >= 0) {
			changeToMapModule((MapModule) mapModuleVector.get(index + 1));
		} else if (size > 0) {
			// Change to the first in the list
			changeToMapModule((MapModule) mapModuleVector.get(0));
		}
	}

	void previousMapModule() {
		int index;
		int size = mapModuleVector.size();
		if (getMapModule() != null)
			index = mapModuleVector.indexOf(getMapModule());
		else
			index = 0;
		if (index > 0) {
			changeToMapModule((MapModule) mapModuleVector.get(index - 1));
		} else {
			if (size > 0) {
				changeToMapModule((MapModule) mapModuleVector.get(size - 1));
			}
		}
	}

	// Change MapModules
	/**
	 * This is the question whether the map is already opened. If this is the
	 * case, the map is automatically opened + returns true. Otherwise does
	 * nothing + returns false.
	 */
	public boolean tryToChangeToMapModule(String mapModule) {
		if (mapModule != null && getMapKeys().contains(mapModule)) {
			changeToMapModule(mapModule);
			return true;
		} else {
			return false;
		}
	}

	/**
	 * 
	 * Checks, whether or not a given url is already opened. Unlike
	 * tryToChangeToMapModule, it does not consider the map+extension
	 * identifiers nor switches to the module.
	 * 
	 * @return null, if not found, the map+extension identifier otherwise.
	 */
	public String checkIfFileIsAlreadyOpened(URL urlToCheck)
			throws MalformedURLException {
		for (Iterator iter = mapModuleVector.iterator(); iter.hasNext();) {
			MapModule module = (MapModule) iter.next();
			if (module.getModel() != null) {
				final URL moduleUrl = module.getModel().getURL();
				if (sameFile(urlToCheck, moduleUrl))
					return module.getDisplayName();
			}
		}
		return null;
	}

	private boolean sameFile(URL urlToCheck, final URL moduleUrl) {
		if (moduleUrl == null) {
			return false;
		}
		if (urlToCheck.getProtocol().equals("file")
				&& moduleUrl.getProtocol().equals("file")) {
			return (new File(urlToCheck.getFile())).equals(new File(moduleUrl
					.getFile()));
		}
		return urlToCheck.sameFile(moduleUrl);
	}

	public boolean changeToMapModule(String mapModuleDisplayName) {
		MapModule mapModuleCandidate = null;
		for (Iterator iterator = mapModuleVector.iterator(); iterator.hasNext();) {
			MapModule mapMod = (MapModule) iterator.next();
			if (Tools.safeEquals(mapModuleDisplayName, mapMod.getDisplayName())) {
				mapModuleCandidate = mapMod;
				break;
			}
		}
		if (mapModuleCandidate == null) {
			throw new IllegalArgumentException("Map module "
					+ mapModuleDisplayName + " not found.");
		}
		return changeToMapModule(mapModuleCandidate);
	}

	public boolean changeToMapModule(MapModule mapModuleCandidate) {
		return setMapModule(mapModuleCandidate, mapModuleCandidate.getMode());
	}

	public void changeToMapOfMode(Mode mode) {
		for (Iterator iterator = mapModuleVector.iterator(); iterator.hasNext();) {
			MapModule mapMod = (MapModule) iterator.next();
			if (mapMod.getMode() == mode) {
				changeToMapModule(mapMod);
				return;
			}
		}
		// there is no map with the given mode open. We have to create an empty
		// one?
		setMapModule(null, mode);
		// FIXME: Is getting here an error? fc, 25.11.2005.
	}

	/**
	 * is null if the old mode should be closed.
	 * 
	 * @return true if the set command was successful.
	 */
	boolean setMapModule(MapModule newMapModule, Mode newMode) {
		// allowed?
		MapModule oldMapModule = this.mapModule;
		Mode oldMode = mCurrentMode;
		if (!listener.isMapModuleChangeAllowed(oldMapModule, oldMode,
				newMapModule, newMode)) {
			return false;
		}

		listener.beforeMapModuleChange(oldMapModule, oldMode, newMapModule,
				newMode);
		this.mapModule = newMapModule;
		this.mCurrentMode = newMode;
		listener.afterMapModuleChange(oldMapModule, oldMode, newMapModule,
				newMode);
		fireNumberOfOpenMapInformation();
		return true;
	}

	private void fireNumberOfOpenMapInformation() {
		listener.numberOfOpenMapInformation(mapModuleVector.size(),
				mapModuleVector.indexOf(getMapModule()));
	}

	// private

	private void addToOrChangeInMapModules(String key,
			MapModule newOrChangedMapModule) {
		// begin bug fix, 20.12.2003, fc.
		// check, if already present:
		String extension = "";
		int count = 1;
		List mapKeys = getMapKeys();
		while (mapKeys.contains(key + extension)) {
			extension = "<" + (++count) + ">";
		}
		// rename map:
		newOrChangedMapModule.setName(key + extension);
		newOrChangedMapModule.setDisplayName(key + extension);
		if (!mapModuleVector.contains(newOrChangedMapModule)) {
			mapModuleVector.add(newOrChangedMapModule);
		}
		// end bug fix, 20.12.2003, fc.
	}

	/**
	 * Close the currently active map, return false if closing canceled.
	 * 
	 * @param force
	 *            forces the closing without any save actions.
	 * @param pRestorable
	 *            is a buffer, if the name of the restorable is needed after
	 *            saving.
	 * @return false if saving was canceled.
	 */
	public boolean close(boolean force, StringBuffer pRestorable) {
		// (DP) The mode controller does not close the map
		MapModule module = getMapModule();
		// FIXME: This is not correct, as this class should not ask somebody.
		// This class is only a list!
		boolean closingNotCancelled = module.getModeController().close(force,
				this);
		if (!closingNotCancelled) {
			return false;
		}
		if (pRestorable != null) {
			pRestorable.append(module.getModel().getRestorable());
		}

		int index = mapModuleVector.indexOf(module);
		mapModuleVector.remove(module);
		if (mapModuleVector.isEmpty()) {
			/* Keep the current running mode */
			setMapModule(null, module.getMode());
		} else {
			if (index >= mapModuleVector.size() || index < 0) {
				index = mapModuleVector.size() - 1;
			}
			changeToMapModule((MapModule) mapModuleVector.get(index));
		}
		listener.afterMapClose(module, module.getMode());
		return true;
	}

	// }}

	public void swapModules(int src, int dst) {
		Tools.swapVectorPositions(mapModuleVector, src, dst);
		fireNumberOfOpenMapInformation();
	}

}
