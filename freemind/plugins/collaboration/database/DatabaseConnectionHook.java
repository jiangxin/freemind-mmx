/*FreeMind - A Program for creating and viewing Mindmaps
 *Copyright (C) 2000-2008  Joerg Mueller, Daniel Polansky, Christian Foltin and others.
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
 * Created on 28.12.2008
 */
/* $Id: DatabaseConnectionHook.java,v 1.1.2.1 2009-02-04 19:31:21 christianfoltin Exp $ */

package plugins.collaboration.database;

import freemind.extensions.PermanentNodeHook;
import freemind.main.XMLElement;
import freemind.modes.MindMapNode;
import freemind.view.mindmapview.NodeView;

/**
 * @author foltin
 * 
 */
public class DatabaseConnectionHook extends DatabaseBasics implements PermanentNodeHook {

	/**
     *
     */

	public void startupMapHook() {
		super.startupMapHook();
		// this is the internal call. do nothing
		logger.info("Startup of the permanent hook.");
		return;
	}

	public void loadFrom(XMLElement pChild) {
		// this plugin should not be saved.
	}

	public void save(XMLElement pXml) {
		// this plugin should not be saved.
		// nothing to do.
	}

	public void shutdownMapHook() {
		// this is the internal call. shutdown
		logger.info("Shut down of the permanent hook.");
		shutdownConnection();
		super.shutdownMapHook();
	}
	
	public void shutdownConnection() {
		if (mUpdateThread != null) {
			mUpdateThread.deregisterFilter();
			mUpdateThread.commitSuicide();
		}
		mUpdateThread.shutdown(false);
	}

	public void onAddChild(MindMapNode pAddedChildNode) {
	}
	
	public void onAddChildren(MindMapNode pAddedChild) {
	}
	
	public void onDeselectHook(NodeView pNodeView) {
	}
	
	public void onNewChild(MindMapNode pNewChildNode) {
	}
	
	public void onRemoveChild(MindMapNode pOldChildNode) {
	}
	
	public void onRemoveChildren(MindMapNode pOldChildNode, MindMapNode pOldDad) {
	}
	
	public void onSelectHook(NodeView pNodeView) {
	}
	
	public void onUpdateChildrenHook(MindMapNode pUpdatedNode) {
	}
	
	public void onUpdateNodeHook() {
	}
	
	public void onViewCreatedHook(NodeView pNodeView) {
	}
	
	public void onViewRemovedHook(NodeView pNodeView) {
	}

}

