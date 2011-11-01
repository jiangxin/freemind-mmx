/*FreeMind - A Program for creating and viewing Mindmaps
 *Copyright (C) 2006  Christian Foltin <christianfoltin@users.sourceforge.net>
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
/*$Id: MindMapMock.java,v 1.1.2.9 2007/07/08 16:27:06 dpolivaev Exp $*/

package tests.freemind;

import java.awt.Color;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.Writer;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.List;

import javax.swing.event.TreeModelListener;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;

import freemind.controller.filter.Filter;
import freemind.main.XMLParseException;
import freemind.modes.MapRegistry;
import freemind.modes.MindMap;
import freemind.modes.MindMapLinkRegistry;
import freemind.modes.MindMapNode;
import freemind.modes.ModeController;

/** */
public class MindMapMock implements MindMap {

	private final String mapXmlString;

	/**
     * 
     */
	public MindMapMock(String mapXmlString) {
		super();
		this.mapXmlString = mapXmlString;

	}

	public ModeController getModeController() {
		return null;
	}

	public void nodeChanged(TreeNode node) {
	}

	public void nodeRefresh(TreeNode node) {
	}

	public String getAsPlainText(List mindMapNodes) {
		return null;
	}

	public String getAsRTF(List mindMapNodes) {
		return null;
	}

	public String getAsHTML(List mindMapNodes) {
		return null;
	}

	public void insertNodeInto(MindMapNode newChild, MindMapNode parent,
			int index) {
	}

	public File getFile() {
		return null;
	}

	public URL getURL() throws MalformedURLException {
		return null;
	}

	public void getXml(Writer fileout) throws IOException {
	}

	public void getFilteredXml(Writer fileout) throws IOException {
		fileout.write(mapXmlString);
		fileout.close();
	}

	public String getRestorable() {
		return null;
	}

	public TreeNode[] getPathToRoot(TreeNode node) {
		return null;
	}

	public Color getBackgroundColor() {
		return null;
	}

	public void setBackgroundColor(Color color) {
	}

	public MindMapLinkRegistry getLinkRegistry() {
		return null;
	}

	public void destroy() {
	}

	public boolean isReadOnly() {
		return false;
	}

	public MapRegistry getRegistry() {
		return null;
	}

	public Filter getFilter() {
		return null;
	}

	public void setFilter(Filter inactiveFilter) {
	}

	public Object getRoot() {
		return null;
	}

	public Object getChild(Object parent, int index) {
		return null;
	}

	public int getChildCount(Object parent) {
		return 0;
	}

	public boolean isLeaf(Object node) {
		return false;
	}

	public void valueForPathChanged(TreePath path, Object newValue) {
	}

	public int getIndexOfChild(Object parent, Object child) {
		return 0;
	}

	public void addTreeModelListener(TreeModelListener l) {
	}

	public void removeTreeModelListener(TreeModelListener l) {
	}

	public MindMapNode getRootNode() {
		return null;
	}

	public void insertNodeInto(MindMapNode copy, MindMapNode rootNode) {
	}

	public void nodeStructureChanged(TreeNode node) {
	}

	public void setSaved(boolean isSaved) {
	}

	public boolean isSaved() {
		return false;
	}

	public boolean save(File pFile) {
		// TODO Auto-generated method stub
		return false;
	}

	public void load(URL pFile) throws FileNotFoundException, IOException,
			XMLParseException, URISyntaxException {
		// TODO Auto-generated method stub

	}

	public void registerMapSourceChangedObserver(
			MapSourceChangedObserver pMapSourceChangedObserver,
			long pGetEventIfChangedAfterThisTimeInMillies) {
		// TODO Auto-generated method stub

	}

	public long deregisterMapSourceChangedObserver(
			MapSourceChangedObserver pMapSourceChangedObserver) {
		// TODO Auto-generated method stub
		return 0;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see freemind.modes.MindMap#changeRoot(freemind.modes.MindMapNode)
	 */
	public void changeRoot(MindMapNode pNewRoot) {
		// TODO Auto-generated method stub

	}

}
