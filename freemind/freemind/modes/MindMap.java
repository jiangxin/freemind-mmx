/*FreeMind - A Program for creating and viewing Mindmaps
 *Copyright (C) 2000-2001  Joerg Mueller <joergmueller@bigfoot.com>
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


package freemind.modes;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.Writer;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.List;

import javax.swing.tree.TreeModel;
import javax.swing.tree.TreeNode;

import freemind.controller.filter.Filter;
import freemind.main.XMLParseException;

public interface MindMap extends TreeModel {

	MindMapNode getRootNode();

	/**
	 * @return The mode controller, the model belongs to.
	 */
	ModeController getModeController();

	// void changeNode(MindMapNode node, String newText);
	// nodeChanged has moved to the modeController. (fc, 2.5.2004)
	void nodeChanged(TreeNode node);

	void nodeRefresh(TreeNode node);

	String getAsPlainText(List mindMapNodes);

	String getAsRTF(List mindMapNodes);

	String getAsHTML(List mindMapNodes);

	/**
	 * Returns the file name of the map edited or null if not possible.
	 */
	File getFile();

	//
	// Abstract methods that _must_ be implemented.
	//

	public boolean save(File file);

	public void load(URL file) throws FileNotFoundException, IOException,
			XMLParseException, URISyntaxException;

	/**
	 * Return URL of the map (whether as local file or a web location)
	 */
	URL getURL() throws MalformedURLException;

	/**
	 * writes the content of the map to a writer.
	 * 
	 * @throws IOException
	 */
	void getXml(Writer fileout) throws IOException;

	/**
	 * writes the content of the map to a writer.
	 * 
	 * @throws IOException
	 */
	void getFilteredXml(Writer fileout) throws IOException;

	/**
	 * Returns a string that may be given to the modes restore() to get this map
	 * again. The Mode must take care that two different maps don't give the
	 * same restoreable key.
	 */
	String getRestorable();

	TreeNode[] getPathToRoot(TreeNode node);

	/**
	 * @return returns the link registry associated with this mode, or null, if
	 *         no registry is present.
	 */
	MindMapLinkRegistry getLinkRegistry();

	/**
	 * Destroy everything you have created upon opening.
	 */
	void destroy();

	boolean isReadOnly();

	/**
	 * @return true if map is clean (saved), false if it is dirty.
	 */
	boolean isSaved();

	/**
     */
	MapRegistry getRegistry();

	Filter getFilter();

	/**
     */
	void setFilter(Filter inactiveFilter);

	void nodeStructureChanged(TreeNode node);

	/**
	 * Use this method to make the map dirty/clean.
	 * 
	 * @param isSaved
	 */
	void setSaved(boolean isSaved);

	/**
	 * When the map source is changed (eg. on disk, there is a newer version
	 * edited from somebody else), this observer can be used to notice this.
	 * 
	 * @author foltin
	 * @date 04.07.2011
	 */
	public interface MapSourceChangedObserver {
		/**
		 * @param pMap
		 * @return true, if the map was reloaded, false otherwise. This means, that if the method returns
		 * true, then the next change on disk is reported as well. If it returns false, the 
		 * next changes will be ignored until the map is saved.
		 * @throws Exception
		 */
		boolean mapSourceChanged(MindMap pMap) throws Exception;
	}

	/**
	 * @param pMapSourceChangedObserver
	 * @param pGetEventIfChangedAfterThisTimeInMillies
	 *            if 0, nothing happens, but if you have ever registered,
	 *            unregistered at time t, and register again at time t+s, you
	 *            should specify t here. If there was an event in between t and
	 *            t+s, and event is issued directly.
	 */
	void registerMapSourceChangedObserver(
			MapSourceChangedObserver pMapSourceChangedObserver,
			long pGetEventIfChangedAfterThisTimeInMillies);

	/**
	 * @param pMapSourceChangedObserver
	 * @return the last saving time to be stored (see
	 *         {@link MindMap#registerMapSourceChangedObserver(MapSourceChangedObserver, long)}
	 *         )
	 */
	long deregisterMapSourceChangedObserver(
			MapSourceChangedObserver pMapSourceChangedObserver);

	/**
	 * @param newRoot
	 *            one of the nodes, that is now root. The others are grouped
	 *            around.
	 */
	void changeRoot(MindMapNode newRoot);
}
