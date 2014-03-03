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
/** this is only a test class */
package plugins.search;

import java.io.File;
import java.util.List;
import java.util.logging.Logger;

import javax.swing.JFrame;

import freemind.modes.mindmapmode.hooks.MindMapHookAdapter;
import freemind.view.MapModule;

/**
 * 
 * 
 * 
 * @author Stephen Leonard
 * @since 14 Feb 2014
 * 
 * @version $Author:: $: Author of last commit
 * @version $Rev:: $: Revision of last commit
 * @version $Date:: $: Date of last commit
 * 
 */
public class SearchControllerHook extends MindMapHookAdapter implements
		ISearchController {

	/**
	 */
	public SearchControllerHook() {
		super();
		if (logger == null) {
			logger = getController().getFrame().getLogger(
					this.getClass().getName());
		}
	}

	public Logger getLogger(Class className) {
		return getController().getFrame().getLogger(className.getName());
	}

	/**
	 * 
	 */
	/* (non-Javadoc)
	 * @see freemind.extensions.HookAdapter#startupMapHook()
	 */
	@Override
	public void startupMapHook() {
		super.startupMapHook();
		SearchViewPanel panel = new SearchViewPanel(this);
		panel.setVisible(true);
	}


	public JFrame getJFrame() {
		return getController()
				.getFrame().getJFrame();
	}

	public void openMap(String mapModule) {
		logger.fine("open map :" + mapModule);
		getController().loadURL(mapModule);
	}

	public File[] getFilesOfOpenTabs() {
		@SuppressWarnings("unchecked")
		List<MapModule> maps = getController().getFrame().getController()
				.getMapModuleManager().getMapModuleVector();
		File[] mapFiles = new File[maps.size()];
		for (int i = 0; i < mapFiles.length; i++) {
			mapFiles[i] = maps.get(i).getModel().getFile();
		}
		return mapFiles;
	}

	@Override
	public void setWaitingCursor(boolean waiting) {
		getController().getFrame().setWaitingCursor(waiting);
	}
}
